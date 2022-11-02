package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDates;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDtoWithBookingDates getItemById(long id, long userId);

    List<ItemDtoWithBookingDates> getAllItems(long userId, Pageable pageRequest);

    List<ItemDto> findItemFromAvailable(String text, Pageable pageRequest);

    CommentResponseDto addComment(CommentRequestDto comment);
}
