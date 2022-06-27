package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryDataStorage;

import java.time.LocalDate;

@Component
public class InMemoryFilmStorage extends InMemoryDataStorage<Film> implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895, 12, 28);

    @Override
    public Film create(Film film) {
        validateFilmRelease(film);
         if (data.containsKey(film.getId())) {
            log.error("Фильм с Id {} уже добавлен.", film.getId());
            throw new FilmAlreadyExistException(String.format("Фильм с Id %s уже добавлен.", film.getId()));
        }
        validateFilm(film);
        if (film.getId() <= 0) {
            film.setId(generateNewId());
        }
        data.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilmRelease(film);
        if (data.containsKey(film.getId())) {
            data.replace(film.getId(), film);
            log.info("Обновлен фильм: {}", film);
            return film;
        } else {
            log.error("Фильм с Id {} отсутствует.", film.getId());
            throw new FilmNotFoundException(String.format("Фильм с Id %s отсутствует.", film.getId()));
        }
    }

    private void validateFilm(Film film) {
        for (Film someFilm : data.values()) {
            if (someFilm.getName().equals(film.getName())
                    && (someFilm.getReleaseDate() == null && film.getReleaseDate() == null
                    || someFilm.getReleaseDate() != null && someFilm.getReleaseDate().equals(film.getReleaseDate()))) {
                log.error("Фильм с названием {} вышедший на экраны в {} уже добавлен.",
                        film.getName(), film.getReleaseDate());
                throw new FilmAlreadyExistException(String.format("Фильм с названием %s вышедший на экраны в %s уже добавлен.",
                        film.getName(), film.getReleaseDate()));
            }
        }
    }
    private void validateFilmRelease(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_CINEMA)) {
            log.error("Дата релиза — не может быть раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года.");
        }
    }

    @Override
    public void deleteFilm(Integer id) {
        if (data.containsKey(id)) {
            data.remove(id);
        } else {
            log.error(String.format("Фильм с идентификатором %d не найден", id));
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден", id));
        }
    }

    @Override
    public Film findFilmById(Integer id) {
        if (data.containsKey(id)) {
            return data.get(id);
        } else {
            log.error(String.format("Фильм с идентификатором %d не найден", id));
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден", id));
        }
    }
}
