package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingStatus;

import java.time.LocalDateTime;

@Data
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
