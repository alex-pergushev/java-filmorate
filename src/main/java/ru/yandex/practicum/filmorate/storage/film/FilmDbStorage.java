package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.impl.FilmGenreDaoImpl;
import ru.yandex.practicum.filmorate.impl.LikeDaoImpl;
import ru.yandex.practicum.filmorate.model.*;


import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

// реализация интерфейса для работы с хранилищем фильмов
@Component("FilmDbStorage")
@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final String SQL_GET_MOST_POPULAR = "SELECT * FROM films AS f ORDER BY (SELECT count(*) " +
            "FROM likes AS l WHERE l.film_id = f.film_id) DESC";

    private final LikeDaoImpl likeDaoImpl;
    private final FilmGenreDaoImpl filmGenreDaoImpl;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(LikeDaoImpl likeDaoImpl, FilmGenreDaoImpl filmGenreDaoImpl, JdbcTemplate jdbcTemplate) {
        this.likeDaoImpl = likeDaoImpl;
        this.filmGenreDaoImpl = filmGenreDaoImpl;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement("INSERT INTO films (name, description, release_date, duration, mpa_id)" +
                                " values (?,?,?,?,?)", new String[]{"film_id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setInt(5, film.getMpa().getId());
                return ps;
            }, keyHolder);

            film.setId(keyHolder.getKey().intValue());
            log.info("Создан фильм с идентификатором \'{}\'", film.getId());
            if (film.getGenres() != null)
                filmGenreDaoImpl.merge(film.getId(), film.getGenres());
            return film;
        } catch (DuplicateKeyException e) {
            mapDuplicateFilmException(film, e);
            throw e;
        }
    }

    @Override
    public Film update(Film film) {
        findFilmById(film.getId());
        try {
            jdbcTemplate.update("UPDATE films SET name = ?, description = ?, release_date = ?, " +
                            "duration = ?, " +
                            "mpa_id = ? " +
                            "WHERE film_id = ?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            filmGenreDaoImpl.merge(film.getId(), film.getGenres());

            if(film.getGenres() != null) {
                film.setGenres(filmGenreDaoImpl.getFilmGenres(film.getId()));
            }

            log.info("Обновлен фильм с идентификатором \'{}\'", film.getId());
            return film;
        }catch (DuplicateKeyException e){
            mapDuplicateFilmException(film, e);
            throw e;
        }
    }

    @Override
    public void deleteFilm(Integer id) {
        findFilmById(id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        log.info("Удален фильм с идентификатором \'{}\'", id);
    }

    @Override
    public List<Film> findAll() {
        List<Film> result = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films");

        while (filmRows.next()) {
            result.add(new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new Mpa(filmRows.getInt("mpa_id"))));
        }
        return result;
    }

    @Override
    public Film findFilmById(Integer id) {
        try {
            SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM v_film_mpa WHERE film_id = ?", id);

            if (filmRows.next()) {
                Film film = new Film(
                        filmRows.getInt("film_id"),
                        filmRows.getString("name"),
                        filmRows.getString("description"),
                        filmRows.getDate("release_date").toLocalDate(),
                        filmRows.getInt("duration"),
                        new Mpa(filmRows.getInt("mpa_id"),
                                filmRows.getString("mpa_name")));
                List<Genre> genres = filmGenreDaoImpl.getFilmGenres(id);
                film.setGenres((genres.size() == 0) ? null : genres);
                return film;
            } else {
                log.error("Фильм с идентификатором \'{}\' не найден", id);
                throw new FilmNotFoundException(String.format("Фильм с идентификатором \'%d\' не найден", id));
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Фильм с идентификатором \'{}\' не найден", id);
            throw new FilmNotFoundException(String.format("Фильм с идентификатором \'%d\' не найден", id));
        }
    }

    @Override
    public void addLike(Film film, User user) {
        likeDaoImpl.create(user.getId(), film.getId());
        log.info("Фильму с идентификатором \'{}\' добавлен лайк пользователем с идентификатором \'{}\'",
                film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        likeDaoImpl.delete(user.getId(), film.getId());
        log.info("Пользователь с идентификатором \'{}\' удалил лайк у фильма с идентификатором \'{}\'",
                user.getId(), film.getId());
    }

    @Override
    public List<Film> getMostPopular(Integer count) {
        List<Film> result = new ArrayList<>();
        SqlRowSet filmRows;

        if (count == null || count == 0) {
            filmRows = jdbcTemplate.queryForRowSet(SQL_GET_MOST_POPULAR);
        } else {
            filmRows = jdbcTemplate.queryForRowSet(SQL_GET_MOST_POPULAR + " LIMIT ?", count);
        }

        while (filmRows.next()) {
            result.add(new Film(
                    filmRows.getInt("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    new Mpa(filmRows.getInt("mpa_id"))));
        }

        return result;
    }

    private void mapDuplicateFilmException(Film film, DuplicateKeyException e) {
        if (e.toString().contains("IDX_FILM_NAME_DATE")) {
            log.error("Фильм с названием \'{}\' вышедший на экраны \'{}\' уже существует",
                    film.getName(), film.getReleaseDate());
            throw new FilmAlreadyExistException(String
                    .format("Фильм с названием \'{}\' вышедший на экраны \'{}\' уже существует",
                            film.getName(), film.getReleaseDate()));
        }

        if (e.toString().contains("PK_FILMS")) {
            log.error(String.format("Фильм с идентификатором \'%d\' уже существует", film.getId()));
            throw new FilmAlreadyExistException(String
                    .format("Фильм с идентификатором \'%d\' уже существует", film.getId()));
        }
    }
}
