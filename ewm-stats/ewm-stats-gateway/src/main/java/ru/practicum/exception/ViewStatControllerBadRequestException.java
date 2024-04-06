package ru.practicum.exception;

public class ViewStatControllerBadRequestException extends RuntimeException {
    public ViewStatControllerBadRequestException(String message) {
        super(message);
    }
}
