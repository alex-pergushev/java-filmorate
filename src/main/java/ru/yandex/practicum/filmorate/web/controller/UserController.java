package ru.yandex.practicum.filmorate.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.db.model.User;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private final Map<Integer, User> users = new HashMap<>();

	@GetMapping
	public Collection<User> findAll() {
		return users.values();
	}

	@PostMapping
	public User create(@RequestBody @Valid User user) throws ValidationException {
		if (users.containsKey(user.getId())) {
			log.error("Пользователь с Id {} уже зарегистрирован.", user.getId());
			throw new ValidationException(String.format("Пользователь с Id %s уже зарегистрирован.", user.getId()));
		}
		for (User someUser : users.values()) {
			validationUser(user, someUser);
		}
		if (user.getId() <= 0) {
			user.setId(generateNewId());
		}
		users.put(user.getId(), user);
		log.debug("Добавлен новый пользователь: {}", user);
		log.debug("Количество пользователей в текущий момент: {}", users.size());
		return user;
	}

	private void validationUser(@RequestBody @Valid User user, User someUser) throws ValidationException {
		if (someUser.getEmail().equals(user.getEmail())) {
			log.error("Пользователь с электронной почтой {} уже зарегистрирован.", user.getEmail());
			throw new ValidationException(String.format("Пользователь с электронной почтой %s уже зарегистрирован.", user.getEmail()));
		}
		if (someUser.getLogin().equals(user.getLogin())) {
			log.error("Логин {} уже занят.", user.getLogin());
			throw new ValidationException(String.format("Логин %s уже занят.", user.getLogin()));
		}
	}

	@PutMapping
	public User update(@Valid @RequestBody User user) throws ValidationException {
		if (users.containsKey(user.getId())) {
			for (User someUser : users.values()) {
				if (user.getId() != someUser.getId()) {
					validationUser(user, someUser);
				}
			}
		} else {
			log.error("Пользователь с идентификатором {} не зарегистрирован", user.getId());
			throw new ValidationException(String.format("Пользователь с идентификатором %s не зарегистрирован.", user.getId()));
		}
		users.replace(user.getId(), user);
		log.debug("Изменен пользователь: {}", user);
		return user;
	}

	private int generateNewId() {
		int resultate = 0;
		//Поиск первого незанятого идентификатора
		for (int i = 1; i <= (users.size() + 1); i++) {
			if (!users.containsKey(i)){
				resultate = i;
				break;
			}
		}
		return resultate;
	}
}
