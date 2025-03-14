package ru.practicum.shareit.request.service.interfaces;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newItemRequestDto);

    List<ItemRequestDto> getAllItemRequestsByUser(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);

    ItemRequestDto getItemRequest(Long userId, Long requestId);
}