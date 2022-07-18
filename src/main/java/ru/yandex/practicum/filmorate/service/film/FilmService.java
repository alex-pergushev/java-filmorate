package ru.yandex.practicum.filmorate.service.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private static final LocalDate RELEASE_NOT_EARLIER = LocalDate.of(1895, 12, 28);

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    @Autowired
    public FilmService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) {
        checkReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        checkReleaseDate(film);
        return filmStorage.update(film);
    }

    private void checkReleaseDate(Film film) {
        if (RELEASE_NOT_EARLIER.isAfter(film.getReleaseDate())) {
            log.error("Дата выхода фильма на экраны не может быть раньше \'28.12.1895 года\'");
            throw new ValidationException(String
                    .format("Дата выхода фильма на экраны не может быть раньше \'28.12.1895 года\'"));
        }
    }

    public void addLikesFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);

        filmStorage.addLike(film, user);
    }

    public void deleteLikesFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);

        filmStorage.deleteLike(film, user);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer id) {
        return filmStorage.findFilmById(id);
    }

    //Получение списка наиболее популярных фильмов
    public List<Film> getMostPopularFilm(int count) {
        return filmStorage.getMostPopular(count);
    }
}
