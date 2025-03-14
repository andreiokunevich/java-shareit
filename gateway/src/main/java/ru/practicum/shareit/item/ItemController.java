package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemDto itemDto,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemDto itemDto,
                                             @RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemClient.deleteItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsOfUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @RequestParam @NotBlank String text) {
        return itemClient.searchItemsByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentDto commentDto) {
        return itemClient.createComment(userId, itemId, commentDto);
    }
}