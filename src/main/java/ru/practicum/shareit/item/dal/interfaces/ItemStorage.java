package ru.practicum.shareit.item.dal.interfaces;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Optional<Item> getItem(Long id);

    Item updateItem(Item item);

    Collection<Item> getAllItemsOfUser(User owner);

    Collection<Item> searchItems(String text);
}