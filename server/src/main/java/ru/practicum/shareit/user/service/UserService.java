package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user);

    UserDto update(UserDto user, Long id);

    UserDto getUserById(long id);

    void deleteUser(long id);

    List<UserDto> getAll();
}
