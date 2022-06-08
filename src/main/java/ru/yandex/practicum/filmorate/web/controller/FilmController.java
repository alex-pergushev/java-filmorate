package ru.yandex.practicum.filmorate.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.db.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping(value = "/films")
public class FilmController {
	private static final Logger log = LoggerFactory.getLogger(FilmController.class);
	private static final LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895,12,28);
	private final Map<Integer, Film> films = new HashMap<>();

	@PostMapping
	public Film create(@Valid @RequestBody Film film) throws ValidationException {
		if (film.getReleaseDate().isBefore(BIRTHDAY_CINEMA)) {
			log.error("Дата релиза — не может быть раньше 28 декабря 1895 года.");
			throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года.");
		}
		if (films.containsKey(film.getId())) {
			log.error("Фильм с Id {} уже добавлен.", film.getId());
			throw new ValidationException(String.format("Фильм с Id %s уже добавлен.", film.getId()));
		}
		validationFilm(film);
		if (film.getId() <= 0) {
			film.setId(generateNewId());
		}
		films.put(film.getId(), film);
		log.debug("Добавлен новый фильм: {}", film);
		return film;
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film film) throws ValidationException {
		if (film.getReleaseDate().isBefore(BIRTHDAY_CINEMA)) {
			log.error("Дата релиза — не может быть раньше 28 декабря 1895 года.");
			throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года.");
		}
		if (films.containsKey(film.getId())) {
			validationFilm(film);
			films.replace(film.getId(), film);
			log.debug("Обновлен фильм: {}", film);
			return film;
		} else {
			log.error("Фильм с Id {} отсутствует.", film.getId());
			throw new ValidationException(String.format("Фильм с Id %s отсутствует.", film.getId()));
		}
	}

	private void validationFilm(@RequestBody @Valid Film film) throws ValidationException {
		for (Film someFilm : films.values()) {
			if (someFilm.getName().equals(film.getName())
					&& (someFilm.getReleaseDate() == null && film.getReleaseDate() == null
					|| someFilm.getReleaseDate() != null && someFilm.getReleaseDate().equals(film.getReleaseDate()))) {
				log.error("Фильм с названием {} вышедший на экраны в {} уже добавлен.",
						film.getName(), film.getReleaseDate());
				throw new ValidationException(String.format("Фильм с названием %s вышедший на экраны в %s уже добавлен.",
						film.getName(), film.getReleaseDate()));
			}
		}
	}

	@GetMapping
	public Collection<Film> findAll() {
		return films.values();
	}

	private int generateNewId() {
		int resultate = 0;
		//Поиск первого незанятого идентификатора
		for (int i = 1; i <= (films.size() + 1); i++) {
			if (!films.containsKey(i)){
				resultate = i;
				break;
			}
		}
		return resultate;
	}
}
