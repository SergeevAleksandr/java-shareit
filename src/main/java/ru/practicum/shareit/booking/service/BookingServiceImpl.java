package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingEnum.BookingState;
import ru.practicum.shareit.booking.BookingEnum.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("checkstyle:Regexp")
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto create(BookingRequestDto bookingRequestDto, long bookerId) {
        User booker = checkUserById(bookerId);
        Item item = checkItemById(bookingRequestDto);

        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Нельзя арендовать собственный предмет");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования!");
        }
        Booking booking = BookingMapper.toBooking(bookingRequestDto, booker, item, BookingStatus.WAITING);
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Время начала бронмрования позже времени окончания бронирования");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto bookingApproving(long bookingId, long userId, Boolean approved) {
        Booking booking = checkBookingById(bookingId);
        if ((booking.getStatus() == BookingStatus.APPROVED && approved) ||
                (booking.getStatus() == BookingStatus.REJECTED && !approved)) {
            throw new ValidationException("Нет необходимости менять статус!");
        }
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        if (userId == bookerId) {
            if (approved) {
                throw new NotFoundException("Нет доступа к изменению бронирования!");
            }
        } else if (ownerId != userId) {
            throw new NotFoundException("Нет доступа к изменению бронирования!");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(long bookingId, long userId) {
        checkUserById(userId);
        Booking booking = checkBookingById(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        if (userId != bookerId && ownerId != userId) {
            throw new NotFoundException("Нет доступа к бронированию!");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingList(long userId, String requestState) {
        BookingState state;
        try {
            state = BookingState.valueOf(requestState);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + requestState);
        }
        checkUserById(userId);
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId,
                                LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                                BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingResponseDto> getOwnerBookingList(long userId, String requestState) {
        BookingState state;
        try {
            state = BookingState.valueOf(requestState);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + requestState);
        }
        checkUserById(userId);
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllItemOwnerCurrentBookings(userId,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllItemOwnerPastBookings(userId,
                                LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllItemOwnerFutureBookings(userId,
                                LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllItemOwnerBookingsByStatus(userId,
                                BookingStatus.WAITING).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllItemOwnerBookingsByStatus(userId,
                                BookingStatus.REJECTED).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:

                return bookingRepository.findAllItemOwnerBookings(userId).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    public User checkUserById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с таким id не найден!"));
    }

    public Item checkItemById(BookingRequestDto bookingRequestDto) {
        return itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с таким id не найден!"));
    }

    public Booking checkBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Такого бронировния не существует"));
    }
}