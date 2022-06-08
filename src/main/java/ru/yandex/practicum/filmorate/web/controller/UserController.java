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
			log.error("Пользователь с Id" + user.getId() + " уже зарегистрирован.");
			throw new ValidationException("Пользователь с Id " +
					user.getId() + " уже зарегистрирован.");
		}
		for (User someUser : users.values()) {
			if (someUser.getEmail().equals(user.getEmail())) {
				log.error("Пользователь с электронной почтой " +
						user.getEmail() + " уже зарегистрирован.");
				throw new ValidationException("Пользователь с электронной почтой " +
						user.getEmail() + " уже зарегистрирован.");
			}
			if (someUser.getLogin().equals(user.getLogin())) {
				log.error("Логин " +
						user.getEmail() + " уже занят.");
				throw new ValidationException("Логин " +
						user.getEmail() + " уже занят.");
			}
		}

		if (user.getId() <= 0) {
			user.setId(generateNewId());
		}

		users.put(user.getId(), user);

		log.debug("Добавлен новый пользователь: {}", user);
		log.debug("Количество пользователей в текущий момент: {}", users.size());

		return user;
	}



	@PutMapping
	public User update(@Valid @RequestBody User user) throws ValidationException {

		if (users.containsKey(user.getId())) {
			for (User someUser : users.values()) {
				if (user.getId() != someUser.getId()) {
					if (someUser.getEmail().equals(user.getEmail())) {
						log.error("Пользователь с электронной почтой " +
								user.getEmail() + " уже зарегистрирован.");
						throw new ValidationException("Пользователь с электронной почтой " +
								user.getEmail() + " уже зарегистрирован.");
					}
					if (someUser.getLogin().equals(user.getLogin())) {
						log.error("Логин " +
								user.getEmail() + " уже занят.");
						throw new ValidationException("Логин " +
								user.getEmail() + " уже занят.");
					}
				}
			}
		} else {
			log.error("Пользователь с идентификатором " + user.getId() + " не зарегистрирован");
			throw new ValidationException("Пользователь с идентификатором " +
					user.getId() + " не зарегистрирован.");
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
