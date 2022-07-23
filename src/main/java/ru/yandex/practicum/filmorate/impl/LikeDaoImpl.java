package ru.yandex.practicum.filmorate.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EntityLinkDao;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class LikeDaoImpl implements EntityLinkDao<Integer> {

    private final JdbcTemplate jdbcTemplate;

    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Integer userId, Integer filmId) {
        try {
            jdbcTemplate.update("INSERT INTO likes (user_id, film_id) VALUES (?, ?)", userId, filmId);
            log.info("Пользователь с идентификатором \'{}\' поставил лайк фильму с идентификатором \'{}\'",
                    userId, filmId);
        } catch (DuplicateKeyException e) {
            String errorMessage = String.format("Пользователь с идентификатором \'%d\' " +
                    "уже отмечал лайком фильм с идентификатором \'%d\'", userId, filmId);
            log.error(errorMessage);
            throw new EntityAlreadyExistException(errorMessage);
        }
    }

    @Override
    public void delete(Integer userId, Integer filmId) {
        Integer item = jdbcTemplate.update("DELETE FROM likes WHERE user_id = ? AND film_id = ?", userId, filmId);
        if (item == null || item == 0) {
            String errorMessage = String.format("Лайк пользователя с идентификатором \'%d\' " +
                    "на фильм с идентификатором \'%d\' не найден", userId, filmId);
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        } else {
            log.info("Удален лайк пользователя с идентификатором \'{}\' фильму с идентификатором \'{}\'",
                    userId, filmId);
        }
    }

    @Override
    public void merge(Integer filmId, List<Integer> likes) {
        List<Integer> base = getFilmLikes(filmId);
        List<Integer> toDelete = new ArrayList<>(base);
        List<Integer> toInsert = new ArrayList<>();

        if (likes != null) {
            toInsert.addAll(likes);
        }

        // на удаление
        toDelete.removeAll(toInsert);
        // на вставку
        toInsert.removeAll(base);

        for (Integer userId : toDelete) {
            delete(userId, filmId);
        }

        for (Integer userId : toInsert) {
            create(userId, filmId);
        }
    }

    // пользователи оценившие фильм
    public List<Integer> getFilmLikes(Integer filmId) {
        List<Integer> result = new ArrayList<>();
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet("SELECT * FROM lires WHERE film_id = ?", filmId);

        while (likeRows.next()) {
            result.add(likeRows.getInt("user_id"));
        }
        return result;
    }
}
