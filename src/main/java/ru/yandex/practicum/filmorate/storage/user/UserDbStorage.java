package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

// реализация интерфейса для работы с хранилищем пользователей
@Component("UserDbStorage")
@Repository
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement("INSERT INTO users (email, login, name, birthday) VALUES (?,?,?,?)",
                                new String[]{"user_id"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
                return ps;
            }, keyHolder);

            user.setId(keyHolder.getKey().intValue());
            log.info("Создан пользователь с идентификатором \'{}\'", user.getId());
            return user;
        } catch (DuplicateKeyException e) {
            mapDuplicateUserException(user, e);
            throw e;
        }
    }

    private void mapDuplicateUserException(User user, DuplicateKeyException e) {
        if (e.toString().contains("IDX_USER_EMAIL")) {
            log.error(String.format("Пользователь с email \'%s\' уже существует", user.getEmail()));
            throw new UserAlreadyExistException(String.format("Пользователь с email \'%s\' уже существует",
                    user.getEmail()));
        }

        if (e.toString().contains("IDX_USER_LOGIN")) {
            log.error(String.format("Пользователь с логином \'%s\' уже существует", user.getLogin()));
            throw new UserAlreadyExistException(String.format("Пользователь с логином \'%s\' уже существует",
                    user.getLogin()));
        }

        if (e.toString().contains("PK_USERS")) {
            log.error(String.format("Пользователь с идентификатором \'%d\' уже существует", user.getId()));
            throw new UserAlreadyExistException(String.format("Пользователь с идентификатором \'%d\' уже существует",
                    user.getId()));
        }
    }

    // обновить пользователя
    @Override
    public User update(User user) {
        findUserById(user.getId());
        try {
            jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());

            log.info("Изменен пользователь с идентификатором \'{}\'", user.getId());
            return user;
        } catch (DuplicateKeyException e) {
            mapDuplicateUserException(user, e);
            throw e;
        }
    }

    // удалить пользователя
    @Override
    public void deleteUser(Integer id) {
        findUserById(id);
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
        log.info("Удален пользователь с идентификатором \'{}\'", id);
    }

    // найти всех пользователей
    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");

        while (userRows.next()) {
            result.add(new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }

        return result;
    }

    @Override
    public User findUserById(Integer id) {
        try {
            SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);

            if (userRows.next()) {
                User user = new User(userRows.getInt("user_id"),
                        userRows.getString("email"),
                        userRows.getString("login"),
                        userRows.getString("name"),
                        userRows.getDate("birthday").toLocalDate());
                return user;
            } else {
                log.error("Пользователь с идентификатором \'{}\' не найден", id);
                throw new UserNotFoundException(String.format("Пользователь с идентификатором \'%d\' не найден", id));
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователь с идентификатором \'{}\' не найден", id);
            throw new UserNotFoundException(String.format("Пользователь с идентификатором \'%d\' не найден", id));
        }
    }

    // получить друзей пользователя
    @Override
    public List<User> getUserFriends(Integer id) {
        List<User> result = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * " +
                "FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)", id);

        while (userRows.next()) {
            result.add(new User(userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }
        return result;
    }

    // добавить в друзья
    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);

        jdbcTemplate.update("MERGE INTO friends KEY (user_id, friend_id) VALUES (?, ?)", userId, friendId);
        log.info("Пользователь с идентификатором \'{}\' добавил в друзья пользователя  с идентификатором \'{}\'",
                user.getId(), friend.getId());

    }

    // удалить из друзей
    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        try {
            jdbcTemplate.update("DELETE FROM friends WHERE friend_id = ? AND user_id = ?", friendId, userId);
            log.info("Пользователь с идентификатором \'{}\' удалил из друзей пользователя  с идентификатором \'{}\'",
                    user.getId(), friend.getId());
        } catch (EmptyResultDataAccessException e) {
            log.error("Между пользователем с идентификатором \'{}\' и пользователем " +
                    "с идентификатором \'{}\' дружба не зарегистрирована", user.getId(), friend.getId());
        }
    }

    // общие друзья
    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherId);
        List<User> result = new ArrayList<>();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users AS u " +
                "WHERE u.user_id IN (SELECT friend_id " +
                "FROM friends AS f " +
                "WHERE f.user_id = ?) " +
                "AND u.user_id IN (SELECT friend_id " +
                "FROM friends AS f " +
                "WHERE f.user_id = ?)", userId, otherId);

        while (userRows.next()) {
            result.add(new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()));
        }
        return result;
    }
}
