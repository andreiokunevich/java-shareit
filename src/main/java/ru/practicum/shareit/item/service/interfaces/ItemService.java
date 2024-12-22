package ru.practicum.shareit.item.service.interfaces;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    Collection<ItemDto> getAllItemsOfUser(Long userId);

    Collection<ItemDto> searchItems(String text);
}