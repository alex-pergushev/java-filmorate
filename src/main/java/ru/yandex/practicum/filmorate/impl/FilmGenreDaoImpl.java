package ru.yandex.practicum.filmorate.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EntityLinkDao;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

// принадлежность фильма к определенным жанрам
@Repository
public class FilmGenreDaoImpl implements EntityLinkDao<Genre> {

    protected static final Logger log = LoggerFactory.getLogger(FilmGenreDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final GenreDaoImpl genreDaoImpl;

    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate, GenreDaoImpl genreDaoImpl) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDaoImpl = genreDaoImpl;
    }

    // добавить фильму жанр
    @Override
    public void create(Integer filmId, Integer genreId) {
        Genre genre = genreDaoImpl.get(genreId);

        jdbcTemplate.update("MERGE INTO film_genres KEY (film_id, genre_id) VALUES (?, ?)", filmId, genreId);
        log.info("Фильму с идентификатором \'{}\' добавлен жанр \'{}\'", filmId, genre.getName());
    }

    // удалить жанр из фильма
    @Override
    public void delete(Integer filmId, Integer genreId) {
        Genre genre = genreDaoImpl.get(genreId);

        Integer item = jdbcTemplate.update("DELETE FROM film_genres " +
                "WHERE film_id = ? AND genre_id = ?", filmId, genreId);

        if (item == null || item == 0) {
            String errorMessage = String.format("Жанр \'%s\' " +
                    "не может быть удален у фильм с идентификатором \'%d\'", genre.getName(), filmId);
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        } else {
            log.info("У фильма с идентификатором \'{}\' удален жанр \'{}\'", filmId, genre.getName());
        }
    }

    @Override
    public void merge(Integer filmId, List<Genre> genres) {
        List<Integer> base = new ArrayList<>();
        List<Integer> toDelete = new ArrayList<>();
        List<Integer> toInsert = new ArrayList<>();

        for (Genre genre : getFilmGenres(filmId)) {
            base.add(genre.getId());
        }

        if (genres != null) {
            for (Genre genre : genres) {
                toInsert.add(genre.getId());
            }
        }

        toDelete.addAll(base);
        // на удаление
        toDelete.removeAll(toInsert);
        // на вставку
        toInsert.removeAll(base);

        for (Integer genreId : toDelete) {
            delete(filmId, genreId);
        }

        for (Integer genreId : toInsert) {
            create(filmId, genreId);
        }
    }

    // пользователи оценившие фильм
    public List<Genre> getFilmGenres(Integer filmId) {
        List<Genre> result = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT g.* FROM genres AS g " +
                "JOIN film_genres AS fg ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?", filmId);

        while (genreRows.next()) {
            result.add(new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name")));
        }
        return result;
    }
}
