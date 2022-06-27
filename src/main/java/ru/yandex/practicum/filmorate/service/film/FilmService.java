package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private UserStorage userStorage;
    private FilmStorage filmStorage;


    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLikesFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);

        if (film != null && user != null) {
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            film.getLikes().add(user.getId());
        }
    }

    public void deleteLikesFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);

        if (film != null && user != null && film.getLikes().contains(user.getId())) {
            film.getLikes().remove(user.getId());
        }
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer id) {
        return filmStorage.findFilmById(id);
    }

    //Получение списка наиболее популярных фильмов
    public List<Film> getTopTenFilm(int count) {
        List<Film> films = filmStorage.findAll();
        return films.stream()
                .sorted((film1, film2) -> Integer.compare((film2 == null ||
                                film2.getLikes() == null) ? 0 : film2.getLikes().size(),
                                (film1 == null ||
                                film1.getLikes() == null) ? 0 : film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
