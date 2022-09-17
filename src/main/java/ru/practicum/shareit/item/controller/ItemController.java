package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingDates;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("checkstyle:Regexp")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    ItemDto createItem(@Validated({Create.class}) @RequestBody ItemDto itemDto,@RequestHeader("X-Sharer-User-Id")  long userId) {
        log.info("Запрос POST на создание вещи от пользователя id - {}", userId);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                       @PathVariable("itemId") long itemId,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH на обновление вещи от пользователя id - {}", userId);
        return itemService.update(itemDto, userId,itemId);
    }

    @GetMapping("/{itemId}")
    ItemDtoWithBookingDates getById(@PathVariable("itemId") long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение вещи по id - {}", itemId);
        return itemService.getItemById(itemId,userId);
    }

    @GetMapping
    List<ItemDtoWithBookingDates> getItem(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение всех вещей пользователя - {}", userId);
        return itemService.getAllItems(userId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> findItemFromAvailable(@RequestParam(name = "text") String text) {
        log.info("Получен запрос GET на получение всех вещей по тексту - {}",text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.findItemFromAvailable(text.toLowerCase());
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Validated({Create.class}) @RequestBody CommentRequestDto comment,
                                         @PathVariable("itemId") long itemId) {
        comment.setCreated(LocalDateTime.now());
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        CommentResponseDto newComment = itemService.addComment(comment);
        log.info("Добавлен комментарий - {}", newComment.getId());
        return newComment;
    }
}
