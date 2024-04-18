package ru.practicum.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.*;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleRecordNotFoundException(final RecordNotFoundException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleRecordNotFoundException(final DataIntegrityViolationException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongUserData(final EventValidationException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongUserData(final UserValidationException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongUserData(final CategoryValidationException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongUserData(final CompilationValidationException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongUserData(final ConstraintViolationException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }

}
