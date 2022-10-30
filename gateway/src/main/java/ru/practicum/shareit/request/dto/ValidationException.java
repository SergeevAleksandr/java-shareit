package ru.practicum.shareit.request.dto;

public class ValidationException extends IllegalStateException {
    public ValidationException(final String message) {
        super(message);
    }
}