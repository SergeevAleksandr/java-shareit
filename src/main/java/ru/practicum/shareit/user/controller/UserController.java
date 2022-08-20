package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> getAll() {
        log.info("Запрос GET на получение всех пользователей");
        return userService.getAll();
    }

    @PostMapping()
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto user) {
        user = userService.create(user);
        log.info("Запрос POST на добавление нового пользователя {}, id {}, email {}", user.getName(), user.getId(), user.getEmail());
        return user;
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable long userId,
                          @Validated({Update.class}) @RequestBody UserDto user) {
        log.info("Запрос PATCH на обновление пользователя {}, id {}, email {} ", user.getName(), user.getId(), user.getEmail());
        return userService.update(user,userId);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Запрос DELETE на удаление пользователя с  id {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto findByID(@PathVariable long userId) {
        log.info("Запрос GET на получение пользователя по id {}", userId);
        return userService.getUserById(userId);
    }
}
