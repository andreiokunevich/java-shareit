package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDtoRequest {
    private Long itemId;
    private String name;
    private Long userId;
}