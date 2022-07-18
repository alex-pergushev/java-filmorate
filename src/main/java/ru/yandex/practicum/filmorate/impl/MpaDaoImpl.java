package ru.yandex.practicum.filmorate.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EntityDao;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MpaDaoImpl implements EntityDao<Mpa> {

    protected static final Logger log = LoggerFactory.getLogger(MpaDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Mpa get(Integer id) {

        try {
            SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpa_id = ?", id);
            if (mpaRows.next()) {
                Mpa mpa = new Mpa(
                        mpaRows.getInt("mpa_id"),
                        mpaRows.getString("name"));
                return mpa;
            } else {
                String errorMessage = String.format("Рейтинг с идентификатором \'%d\' не найден", id);
                log.error(errorMessage);
                throw new EntityNotFoundException(errorMessage);
            }

        } catch (EmptyResultDataAccessException e) {
            String errorMessage = String.format("Рейтинг с идентификатором \'%d\' не найден", id);
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
    }

    @Override
    public Mpa create(Mpa mpa) {
        try {
            jdbcTemplate.update("INSERT INTO mpa VALUES (?, ?)", mpa.getId(), mpa.getName());
            log.info("Создан рейтинг \'{}\'", mpa);
            return mpa;
        } catch (DuplicateKeyException e) {
            String errorMessage = String.format("Рейтинг с идентификатором \'%d\' уже существует", mpa.getId());
            log.error(errorMessage);
            throw new EntityAlreadyExistException(errorMessage);
        }
    }

    @Override
    public void delete(Integer id) {
        Mpa mpa = get(id);
        jdbcTemplate.update("DELETE FROM mpa WHERE mpa_id = ?", id);
        log.info("Удален рейтинг \'{}\'", mpa);
    }

    @Override
    public Mpa update(Mpa mpa) {
        get(mpa.getId());
        jdbcTemplate.update("UPDATE mpa SET name = ? WHERE mpa_id = ?", mpa.getName(), mpa.getId());
        log.info("Обновлен рейтинг \'{}\'", mpa);
        return mpa;
    }

    @Override
    public List<Mpa> getAll() {
        List<Mpa> result = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");

        while (mpaRows.next()) {
            result.add(new Mpa(
                    mpaRows.getInt("mpa_id"),
                    mpaRows.getString("name")));
        }
        return result;
    }
}
