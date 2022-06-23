package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "/films")
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    // пользователь ставит лайк фильму
    @PutMapping(value = "/{id}/like/{userId}")
    public void addLikesFilm(@PathVariable("id") int filmId, @PathVariable int userId) {
        filmService.addLikesFilm(filmId, userId);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    // получать каждый фильм по их уникальному идентификатору
    @GetMapping(value = "/{id}")
    public Film findFilmById(@PathVariable("id") int filmId) {
        return filmService.findFilmById(filmId);
    }

    // возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, вернет первые 10.
    @GetMapping(value = "/popular")
    public List<Film> getPopularFilm(@RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        return filmService.getTopTenFilm(count);
    }

    // пользователь удаляет лайк
    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLikesFilm(@PathVariable("id") int filmId, @PathVariable int userId){
        filmService.deleteLikesFilm (filmId, userId);
    }




}
