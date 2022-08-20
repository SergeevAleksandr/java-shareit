package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item);

    void update(long userId, Item item);

    Optional<Item> getItemById(long id);

    List<Item> findAllByIdUser(long userId);

    List<Item> findAll();
}
