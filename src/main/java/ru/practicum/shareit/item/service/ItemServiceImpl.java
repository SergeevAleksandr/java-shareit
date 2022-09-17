package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:Regexp")
@Service
@RequiredArgsConstructor

public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = checkUser(userId);
        Item item = ItemMapper.toItem(itemDto,user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto item, long userId,long itemId) {
        User user = checkUser(userId);
        Item updateItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена"));
        if (!updateItem.getOwner().getId().equals(user.getId())) {
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
        itemRepository.save(updateItem);
        return ItemMapper.toItemDto(itemRepository.findById(itemId).get());
    }

    @Override
    public ItemDtoWithBookingDates getItemById(long id, long userId) {
        return convertItem(itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Предмет с таким id не найден!")), userId);
    }

    @Override
    public List<ItemDtoWithBookingDates> getAllItems(long userId) {
        checkUser(userId);
        return itemRepository.findOwnersItems(userId).stream().map(i -> convertItem(i, userId))
                .collect(Collectors.toList());
    }

    public User checkUser(Long userId) {
       return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден"));
    }

    @Override
    public List<ItemDto> findItemFromAvailable(String text) {
        return itemRepository.findAll().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)) && item.getAvailable())
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(CommentRequestDto comment) {
        User author = userRepository.findById(comment.getAuthorId()).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
        Item item = itemRepository.findById(comment.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с таким id не найден!"));

        List<Booking> bookingList = bookingRepository.findAllUserBookings(comment.getAuthorId(),
                comment.getItemId(), comment.getCreated());
        if (bookingList.isEmpty()) {
            throw new ValidationException("Нет доступа к созданию отзыва!");
        }
        Comment newComment = commentRepository.save(CommentMapper.toComment(comment, item, author));
        return CommentMapper.toCommentDtoOut(newComment);
    }

    private ItemDtoWithBookingDates convertItem(Item item, long userId) {
        List<Booking> bookingList = bookingRepository.findAllByItemId(item.getId());

        LocalDateTime now = LocalDateTime.now();
        Booking current = null;
        Booking next = null;
        if (item.getOwner().getId() == userId) {
            for (Booking booking : bookingList) {
                if (booking.getStart().isBefore(now)) {
                    if ((current == null) || (current.getStart().isBefore(booking.getStart())))
                        current = booking;
                    continue;
                }

                if (booking.getStart().isAfter(now)) {
                    if ((next == null) || (next.getStart().isAfter(booking.getStart()))) {
                        next = booking;
                    }
                }
            }
        }

        List<CommentResponseDto> commentList = commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDtoOut).collect(Collectors.toList());

        return ItemMapper.toItemDtoWithBookingDates(item, current, next, commentList);
    }
}
