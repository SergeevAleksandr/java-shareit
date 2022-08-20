package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = checkUser(userId);
        Item item = ItemMapper.toItem(itemDto,user);
        return ItemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(ItemDto item, long userId,long itemId) {
        User user = checkUser(userId);
        Item updateItem = itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));
        if (updateItem.getOwner().getId() != user.getId()) {
            throw  new NotFoundException("Чужая вещь не можеть быть обновлена");
        }
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        itemRepository.update(itemId,updateItem);
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId).get());
    }

    @Override
    public ItemDto getItemById(long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id).orElseThrow(() ->
                new NotFoundException("Вещь не найдена")));
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        checkUser(userId);
        return itemRepository.findAllByIdUser(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public User checkUser(Long userId) {
       return userRepository.getUserById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден"));
    }

    @Override
    public List<ItemDto> findItemFromAvailable(String text) {
        return itemRepository.findAll().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)) && item.getAvailable())
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
