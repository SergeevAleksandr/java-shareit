package ru.practicum.shareit.exception;

public class ConflictException extends IllegalStateException  {
    public ConflictException(final String message) {
        super(message);
    }
}
