package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;
    ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setup() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                itemRepository, userRepository);
    }

    @Test
    void createTest() {
        User user = new User(1L, "name1", "email1@mail");
        ItemRequestDto request = new ItemRequestDto(1L, "text",  LocalDateTime.now(), user.getId());

        when(itemRequestRepository.save(any())).thenReturn(ItemRequestMapper.toItemRequest(request, user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemRequestDto requestDto = itemRequestService.addItemRequest(request);

        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getDescription(), requestDto.getDescription());
        assertEquals(request.getRequesterId(), requestDto.getRequesterId());
    }

    @Test
    void findItemRequestsByRequesterTest() {
        User user = new User(1L, "name1", "email1@mail");
        User owner = new User(1L, "owner", "owner@mail");

        ItemRequestDto request = new ItemRequestDto(1L, "text", LocalDateTime.now(),user.getId());
        Item item = new Item(1L, "item", "description", true, owner, ItemRequestMapper
                .toItemRequest(request, user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(ItemRequestMapper
                .toItemRequest(request, user)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestDtoResponse> requestList = itemRequestService.findAllByOwner(user.getId());

        assertEquals(request.getId(), requestList.get(0).getId());
        assertEquals(request.getDescription(), requestList.get(0).getDescription());
        assertEquals(item.getId(), requestList.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), requestList.get(0).getItems().get(0).getName());
    }

    @Test
    void findItemRequestsTest() {
        User user = new User(1L, "name1", "email1@mail");
        User owner = new User(1L, "owner", "owner@mail");

        ItemRequestDto request = new ItemRequestDto(1L, "text", LocalDateTime.now(), user.getId());
        Item item = new Item(1L, "item", "description", true, owner, ItemRequestMapper
                .toItemRequest(request, user));
        when(itemRequestRepository.findAllByOtherUser(anyLong(), any())).thenReturn(List.of(ItemRequestMapper
                .toItemRequest(request, user)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestDtoResponse> requestList = itemRequestService.findAll(owner.getId(), Pageable.unpaged());

        assertEquals(request.getId(), requestList.get(0).getId());
        assertEquals(request.getDescription(), requestList.get(0).getDescription());
        assertEquals(item.getId(), requestList.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), requestList.get(0).getItems().get(0).getName());
    }

    @Test
    void findItemRequestsByIdTest() {
        User user = new User(1L, "name1", "email1@mail");
        User owner = new User(1L, "owner", "owner@mail");

        ItemRequestDto request = new ItemRequestDto(1L, "text",  LocalDateTime.now(),user.getId());
        Item item = new Item(1L, "item", "description", true, owner, ItemRequestMapper
                .toItemRequest(request, user));
        when(itemRequestRepository.findItemRequestById(anyLong())).thenReturn(Optional.of(ItemRequestMapper
                .toItemRequest(request, user)));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDtoResponse requestDto = itemRequestService.findRequest(owner.getId(),request.getId());

        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getDescription(), requestDto.getDescription());
        assertEquals(item.getId(), requestDto.getItems().get(0).getId());
        assertEquals(item.getName(), requestDto.getItems().get(0).getName());
    }
}