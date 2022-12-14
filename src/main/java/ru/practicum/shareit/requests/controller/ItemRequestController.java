package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping()
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Validated({Create.class}) @RequestBody ItemRequestDto requestDto) {
        requestDto.setRequesterId(userId);
        requestDto.setCreated(LocalDateTime.now());
        ItemRequestDto request = requestService.addItemRequest(requestDto);
        log.info("Был добавлен новый запрос id: {}, описание: {}", requestDto.getId(), requestDto.getDescription());
        return request;
    }

    @GetMapping()
    public List<ItemRequestDtoResponse> findRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        int page = from / size;
        return requestService.findAll(userId, PageRequest.of(page, size));
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoResponse findRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long requestId) {
        return requestService.findRequest(userId, requestId);
    }
}
