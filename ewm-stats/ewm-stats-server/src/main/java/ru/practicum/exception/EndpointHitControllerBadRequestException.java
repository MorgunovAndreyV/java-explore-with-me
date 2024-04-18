package ru.practicum.exception;

public class EndpointHitControllerBadRequestException extends RuntimeException {
    public EndpointHitControllerBadRequestException(String message) {
        super(message);
    }
}
