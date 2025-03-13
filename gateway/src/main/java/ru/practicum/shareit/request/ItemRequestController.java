package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }
}