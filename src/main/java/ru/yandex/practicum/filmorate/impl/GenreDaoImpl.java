package ru.yandex.practicum.filmorate.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EntityDao;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GenreDaoImpl implements EntityDao<Genre> {

    protected static final Logger log = LoggerFactory.getLogger(GenreDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Genre get(Integer id) {

        try {
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?", id);
            if (genreRows.next()) {
                Genre genre = new Genre(
                        genreRows.getInt("genre_id"),
                        genreRows.getString("name"));
                return genre;
            } else {
                String errorMessage = String.format("Жанр с идентификатором \'%d\' не найден", id);
                log.error(errorMessage);
                throw new EntityNotFoundException(errorMessage);
            }

        } catch (EmptyResultDataAccessException e) {
            String errorMessage = String.format("Жанр с идентификатором \'%d\' не найден", id);
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
    }

    @Override
    public Genre create(Genre genre) {
        try {
            jdbcTemplate.update("INSERT INTO genres VALUES (?, ?)", genre.getId(), genre.getName());
            log.info("Создан жанр \'{}\'", genre);
            return genre;
        } catch (DuplicateKeyException e) {
            String errorMessage = String.format("Жанр с идентификатором \'%d\' уже существует", genre.getId());
            log.error(errorMessage);
            throw new EntityAlreadyExistException(errorMessage);
        }
    }

    @Override
    public void delete(Integer id) {
        Genre genre = get(id);
        jdbcTemplate.update("DELETE FROM genres WHERE genre_id = ?", id);
        log.info("Создан жанр \'{}\'", genre);
    }

    @Override
    public Genre update(Genre genre) {
        get(genre.getId());
        jdbcTemplate.update("UPDATE genres SET name = ? WHERE genre_id = ?", genre.getName(), genre.getId());
        log.info("Обновлен жанр \'{}\'", genre);
        return genre;
    }

    @Override
    public List<Genre> getAll() {
        List<Genre> result = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres");

        while (genreRows.next()) {
            result.add(new Genre(
                    genreRows.getInt("genre_id"),
                    genreRows.getString("name")));
        }
        return result;
    }
}
