package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return filmService.update(film);
    }

    // пользователь ставит лайк фильму
    @PutMapping(value = "/{id}/like/{userId}")
    public void addLikesFilm(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        filmService.addLikesFilm(filmId, userId);
    }

    @GetMapping
    public List<Film> findAll(){
        return filmService.findAll();
    }

    // получать каждый фильм по их уникальному идентификатору
    @GetMapping(value = "/{id}")
    public Film findFilmById(@PathVariable("id") Integer filmId) {
        return filmService.findFilmById(filmId);
    }

    // возвращает список из первых count фильмов по количеству лайков.
    // Если значение параметра count не задано, вернет первые 10.
    @GetMapping(value = "/popular")
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getMostPopularFilm(count);
    }

    // пользователь удаляет лайк
    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLikesFilm(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        filmService.deleteLikesFilm(filmId, userId);
    }
}
