package ru.practicum.shareit.requests.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getRequester() != null ? itemRequest.getRequester().getId() : null
        );
    }

    public static ItemRequestDtoResponse toItemRequestDtoResponse(ItemRequest itemRequest, List<ItemDto> itemList) {
        return new ItemRequestDtoResponse(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemList
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequest, User requester) {
        return new ItemRequest(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requester,
                itemRequest.getCreated()
        );
    }
}