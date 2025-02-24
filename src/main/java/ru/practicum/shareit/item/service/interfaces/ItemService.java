package ru.practicum.shareit.item.service.interfaces;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemCommentsDto getItem(Long itemId);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    void deleteItem(Long itemId);

    Collection<ItemDto> getAllItemsOfUser(Long userId);

    Collection<ItemDto> searchItems(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}