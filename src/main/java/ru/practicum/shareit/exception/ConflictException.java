package ru.practicum.shareit.exception;

public class ConflictException extends RuntimeException {
    private final String parameter;

    public ConflictException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
