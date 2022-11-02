package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequest) {
        User requester = userRepository.findById(itemRequest.getRequesterId()).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));

        ItemRequest newItemRequest = ItemRequestMapper.toItemRequest(itemRequest, requester);
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(newItemRequest));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoResponse> findAllByOwner(long userId) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requester.getId());
        return requestsToDto(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDtoResponse> findAll(long userId, Pageable pageRequest) {
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
        List<ItemRequest> requests = requestRepository.findAllByOtherUser(requester.getId(), pageRequest);
        return requestsToDto(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDtoResponse findRequest(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден!");
        }
        ItemRequest request = requestRepository.findItemRequestById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с таким id не найден!"));
        return ItemRequestMapper.toItemRequestDtoResponse(request,
                itemRepository.findAllByRequestId(request.getId()).stream().map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));
    }

    private List<ItemRequestDtoResponse> requestsToDto(List<ItemRequest> requestsList) {
        List<ItemRequestDtoResponse> requestsDtoList = new ArrayList<>();
        for (ItemRequest request : requestsList) {
            requestsDtoList.add(ItemRequestMapper.toItemRequestDtoResponse(request,
                    itemRepository.findAllByRequestId(request.getId()).stream().map(ItemMapper::toItemDto)
                            .collect(Collectors.toList())));
        }
        return requestsDtoList;
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден!");
        }
    }
}
