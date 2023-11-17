package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.yandex.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler
    public ErrorResponse handleConflictException(final ConflictException e) {
        return new ErrorResponse(
                String.format("Введенный параметр уже используется " + e.getParameter())
        );
    }

    @ExceptionHandler
    public ErrorResponse handleValidationException(final ValidateException e) {
        return new ErrorResponse(
                "Введен некорректный параметр "
        );
    }

    @ExceptionHandler
    public ErrorResponse handleValidationException(final UserNotFoundException e) {
        return new ErrorResponse(
                String.format("Введен некорректный параметр " + e.getParameter())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }
}
