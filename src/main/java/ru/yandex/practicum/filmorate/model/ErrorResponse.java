package ru.yandex.practicum.filmorate.model;

public class ErrorResponse {
    // название ошибки
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
