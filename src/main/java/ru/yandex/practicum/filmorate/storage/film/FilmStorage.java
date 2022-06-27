package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    void deleteFilm(Integer id);

    List<Film> findAll();

    Film findFilmById(Integer id);
}
