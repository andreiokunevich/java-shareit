package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        log.info("Попытка создания вещи");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Item item = ItemMapper.toItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id: " + itemDto.getRequestId() + " не найден."));
            item.setRequest(itemRequest);
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemCommentsDto getItem(Long userId, Long itemId) {
        log.info("Попытка получения вещи по айди {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с айди " + itemId + " не найдена!"));

        List<CommentDto> commentsDto = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList());

        ItemCommentsDto itemCommentsDto = ItemMapper.toItemCommentsDto(item, commentsDto);

        Optional<Booking> last = bookingRepository.findFirstByItemIdAndStatusAndStartAfter(itemId,
                Status.APPROVED, LocalDateTime.now(), Sort.by("start").descending());
        Optional<Booking> next = bookingRepository.findFirstByItemIdAndStatusAndStartAfter(itemId,
                Status.APPROVED, LocalDateTime.now(), Sort.by("start"));

        itemCommentsDto.setLastBooking(last.map(Booking::getEnd).orElse(null));
        itemCommentsDto.setNextBooking(next.map(Booking::getStart).orElse(null));

        return itemCommentsDto;
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        log.info("Удаление вещи с айди {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с айди " + itemId + " не найдена!"));
        itemRepository.delete(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        log.info("Попытка обновления данных по вещи с айди {}", itemId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        if (!item.getOwner().equals(user)) {
            throw new ValidationException("Пользователь не является владельцем вещи!");
        }
        if (Objects.nonNull(itemDto.getName()) && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
            log.info("Задано новое название вещи с айди {}", itemId);
        }
        if (Objects.nonNull(itemDto.getDescription()) && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
            log.info("Задано новое описание вещи с айди {}", itemId);
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
            log.info("Задан новый статус доступности вещи с айди {}", itemId);
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUser(Long userId) {
        log.info("Получение списка всех вещей пользователя с айди {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<ItemDto> searchItems(Long userId, String text) {
        log.info("Поиск вещи с заданным текстом {}", text);
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findItemByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
        if (bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Попытка написать отзыв у вещи, которую не бронировал пользователь.");
        }
        Comment comment = CommentMapper.toComment(user, item, commentDto);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}