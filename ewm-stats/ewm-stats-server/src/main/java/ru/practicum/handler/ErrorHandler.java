package ru.practicum.handler;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.EndpointHitControllerBadRequestException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleWrongUserData(final EndpointHitControllerBadRequestException e) {
        log.error(String.format(e.getMessage()));
        return new ExceptionResponse(
                String.format(e.getMessage())
        );
    }


}
