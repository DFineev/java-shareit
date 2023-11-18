package ru.practicum.shareit.exception;

public class ValidateException extends RuntimeException {
    private final String parameter;

    public ValidateException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
