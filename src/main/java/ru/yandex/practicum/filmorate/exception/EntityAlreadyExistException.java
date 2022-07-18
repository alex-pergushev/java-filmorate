package ru.yandex.practicum.filmorate.exception;

public class EntityAlreadyExistException extends RuntimeException {
    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
