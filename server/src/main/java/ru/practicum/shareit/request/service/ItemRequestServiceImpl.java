package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private static final Sort SORT = Sort.by("created").descending();

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newItemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, newItemRequestDto);
        itemRequestRepository.save(itemRequest);
        log.info("Создан ItemRequest с id: {} от пользователя с id: {}", itemRequest.getId(), userId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequester(UserMapper.userToDto(user));
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));
        log.info("Получение всех запросов пользователя с id: {}", userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId, SORT);
        List<ItemRequestDto> listToReturn = new ArrayList<>();
        itemRequests.forEach(
                itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    List<ItemDtoRequest> items = itemRepository.findAllByRequest(itemRequest).stream()
                            .map(ItemMapper::toItemDtoRequest)
                            .toList();
                    itemRequestDto.setItems(items);
                    itemRequestDto.setRequester(UserMapper.userToDto(itemRequest.getRequester()));
                    listToReturn.add(itemRequestDto);
                }
        );
        return listToReturn;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));
        log.info("Получение всех запросов пользователей, кроме пользователя с id: {}", userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId, SORT);
        List<ItemRequestDto> listToReturn = new ArrayList<>();
        itemRequests.forEach(
                itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    itemRequestDto.setRequester(UserMapper.userToDto(itemRequest.getRequester()));
                    listToReturn.add(itemRequestDto);
                }
        );
        return listToReturn;
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден."));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id: " + requestId + " не найден."));
        log.info("Получение запроса ItemRequest по id: {}", requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<ItemDtoRequest> items = itemRepository.findAllByRequest(itemRequest).stream()
                .map(ItemMapper::toItemDtoRequest)
                .toList();
        itemRequestDto.setItems(items);
        itemRequestDto.setRequester(UserMapper.userToDto(itemRequest.getRequester()));
        return itemRequestDto;
    }
}