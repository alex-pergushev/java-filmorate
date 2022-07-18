package ru.yandex.practicum.filmorate.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EntityLinkDao;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

//дружеские связи между пользователями
@Repository
public class FriendsDaoImpl implements EntityLinkDao<Integer>{

    protected static final Logger log = LoggerFactory.getLogger(FriendsDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;

    public FriendsDaoImpl(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }


    @Override
    public void create(Integer userId, Integer friendId) {
        User user = userDbStorage.findUserById(userId);
        User friend = userDbStorage.findUserById(friendId);

        jdbcTemplate.update("MERGE INTO friends AS f KEY (user_id, friend_id) VALUES (?, ?)", userId, friendId);
        log.info("Пользователь \'{}\' добавил в друзья пользователя \'{}\'", user.getName(), friend.getName());
    }

    @Override
    public void delete(Integer userId, Integer friendId) {
        User user = userDbStorage.findUserById(userId);
        User friend = userDbStorage.findUserById(friendId);

        Integer item = jdbcTemplate.update("DELETE FROM friends WHERE friend_id = ? AND user_id = ?", friendId, userId);

        if (item == null || item == 0) {
            String errorMessage = String.format("Между пользователем \'%s\' " +
                    "и пользователем \'%s\' дружба не зарегистрирована", user.getName(), friend.getName());
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        } else {
            log.info("Пользователь \'{}\' отменил дружбу с пользователем \'{}\'", user.getName(), friend.getName());
        }
    }

    @Override
    public void merge(Integer userId, List<Integer> friends) {
        List<Integer> base = new ArrayList<>();
        List<Integer> toDelete = new ArrayList<>();
        List<Integer> toInsert = new ArrayList<>();

        for (User user : getUserFriends(userId)) {
            base.add(user.getId());
        }

        if (friends != null) {
            toInsert.addAll(friends);
        }

        // на удаление
        toDelete.removeAll(toInsert);
        // на вставку
        toInsert.removeAll(base);

        for (Integer friendId : toDelete) {
            delete(userId, friendId);
        }

        for (Integer friendId : toInsert) {
            create(userId, friendId);
        }
    }

    // друзья пользователи
    public List<User> getUserFriends(Integer userId) {
        List<User> result = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users " +
                "WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)", userId);

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
