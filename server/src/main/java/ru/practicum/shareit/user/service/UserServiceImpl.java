package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto user) {
        //checkUserEmail(UserMapper.toUser(user));
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }

    @Override
    public UserDto update(UserDto user, Long id) {
        User updateUser = checkUserbyId(id);
        checkUserEmail(UserMapper.toUser(user));
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        userRepository.save(updateUser);
        return UserMapper.toUserDto(updateUser);
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!")));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> list = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserDto userDto = UserMapper.toUserDto(user);
            list.add(userDto);
        }
        return list;
    }

    public void checkUserEmail(User user) {
        List<User> userList = userRepository.findAll().stream()
                .filter(userInList -> userInList.getEmail().equals(user.getEmail())).collect(Collectors.toList());
        if (userList.size() > 0) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
    }

    public User checkUserbyId(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден!"));
    }
}
