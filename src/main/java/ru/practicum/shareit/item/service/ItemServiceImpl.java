package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.interfaces.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.interfaces.ItemService;
import ru.practicum.shareit.user.dal.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        log.info("Попытка создания вещи");
        User user = userStorage.getUser(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto getItem(Long id) {
        log.info("Попытка получения вещи по айди {}", id);
        return ItemMapper.toItemDto(itemStorage.getItem(id).orElseThrow(() -> new NotFoundException("Вещь не найдена!")));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        log.info("Попытка обновления данных по вещи с айди {}", itemId);
        User user = userStorage.getUser(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        Item item = itemStorage.getItem(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена!"));
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
        itemStorage.updateItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUser(Long userId) {
        log.info("Получения списка всех вещей пользователя с айди {}", userId);
        User user = userStorage.getUser(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        return itemStorage.getAllItemsOfUser(user).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        log.info("Поиск вещи с заданным текстом {}", text);
        if (text.isBlank()) {
            return List.of();
        }
        return itemStorage.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }
}