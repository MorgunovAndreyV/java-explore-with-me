package ru.practicum.exception;

public class CompilationValidationException extends RuntimeException {
    public CompilationValidationException(String message) {
        super(message);
    }
}
