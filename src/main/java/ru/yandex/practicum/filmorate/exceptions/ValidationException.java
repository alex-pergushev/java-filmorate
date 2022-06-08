package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends Throwable {

	private String message;
	public ValidationException(String message) {
		super(message);
	}
	public String ValidationException() {
		return message;
	}

}
