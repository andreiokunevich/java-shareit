package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.interfaces.ItemService;

import java.util.Collection;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody @Valid ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemCommentsDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable Long itemId) {
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsOfUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Valid CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}