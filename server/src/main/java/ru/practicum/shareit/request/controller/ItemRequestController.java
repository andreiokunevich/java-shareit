package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.interfaces.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestBody NewItemRequestDto newItemRequestDto) {
        return itemRequestService.createItemRequest(userId, newItemRequestDto);
    }
}