package ru.practicum.shareit.exception;

public class UnknownBookingState extends RuntimeException {
    private final String parameter;

    public UnknownBookingState(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}