package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleConflictException(final ConflictException e) {
        return new ErrorResponse(
                String.format("Введенный параметр уже используется " + e.getParameter())
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFoundException(final ObjectNotFoundException e) {
        return new ErrorResponse(
                String.format("Объект не найден", e.getMessage())
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final ValidateException e) {
        return new ErrorResponse(
                String.format("Введен некорректный параметр " + e.getParameter())
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final UserNotFoundException e) {
        return new ErrorResponse(
                String.format("Введен некорректный параметр " + e.getParameter())
        );
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка."
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleUnknownStateException(final UnknownBookingState e) {
        return new ErrorResponse(String.format("Unknown state: " + e.getParameter()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleException(MethodArgumentNotValidException ex) {
        return ex.getMessage();
    }
}

