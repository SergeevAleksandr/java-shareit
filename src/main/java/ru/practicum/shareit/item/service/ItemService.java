package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDto getItemById(long id);

    List<ItemDto> getAllItems(long userId);

    List<ItemDto> findItemFromAvailable(String text);
}
