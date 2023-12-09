package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    @Test
    void getError() {
        ErrorResponse errorResponse = new ErrorResponse("ERROR");
        assertEquals(errorResponse.getError(), "ERROR");
    }
}
