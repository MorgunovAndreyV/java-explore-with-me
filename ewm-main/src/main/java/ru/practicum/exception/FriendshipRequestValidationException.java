package ru.practicum.exception;

public class FriendshipRequestValidationException extends RuntimeException {
    public FriendshipRequestValidationException(String message) {
        super(message);
    }
}
