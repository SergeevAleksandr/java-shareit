package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;

import java.util.List;

public interface ItemRequestService {

    @Transactional
    ItemRequestDto addItemRequest(ItemRequestDto itemRequest);

    List<ItemRequestDtoResponse> findAllByOwner(long userId);

    List<ItemRequestDtoResponse> findAll(long userId, Pageable pageRequest);

    ItemRequestDtoResponse findRequest(long userId, long requestId);
}
