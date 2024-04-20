package ru.practicum.exception;

public class SubscriptionValidationException extends RuntimeException {
    public SubscriptionValidationException(String message) {
        super(message);
    }
}
