package ru.practicum.shareit.exception;

public class ValidationException extends IllegalStateException {
    public ValidationException(final String message) {
        super(message);
    }
}