package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.ArrayList;
import java.util.List;

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
    ItemDto getById(@PathVariable("itemId") long itemId) {
        log.info("Получен запрос GET на получение вещи по id - {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    List<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение всех вещей пользователя - {}");
        return itemService.getAllItems(userId);
    }

    @GetMapping(value = "/search")
    public List<ItemDto> findItemFromAvailable(@RequestParam(name = "text") String text) {
        log.info("Получен запрос GET на получение всех вещей по тексту - {}");
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemService.findItemFromAvailable(text.toLowerCase());
    }
}
