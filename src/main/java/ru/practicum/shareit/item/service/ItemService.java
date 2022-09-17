package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDates;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithBookingDates getItemById(long id, long userId);

    List<ItemDtoWithBookingDates>  getAllItems(long userId);

    List<ItemDto> findItemFromAvailable(String text);

    CommentResponseDto addComment(CommentRequestDto comment);
}
