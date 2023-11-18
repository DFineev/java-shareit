package ru.practicum.shareit.exception;

public class UserNotFoundException extends IllegalArgumentException {
    private final String parameter;

    public UserNotFoundException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
