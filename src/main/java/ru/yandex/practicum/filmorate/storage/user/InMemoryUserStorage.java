package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryDataStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryUserStorage extends InMemoryDataStorage<User> implements UserStorage {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public User create(User user) {
        if (data.containsKey(user.getId())) {
            log.error("Пользователь с Id {} уже зарегистрирован.", user.getId());
            throw new UserAlreadyExistException(String.format("Пользователь с Id %s уже зарегистрирован.", user.getId()));
        }

        for (User someUser : data.values()) {
            validateUser(user, someUser);
        }

        if (user.getId() <= 0) {
            user.setId(generateNewId());
        }

        data.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        log.info("Количество пользователей в текущий момент: {}", data.size());

        return user;
    }

    @Override
    public User update(User user) {
        if (data.containsKey(user.getId())) {
            for (User someUser : data.values()) {
                if (user.getId() != someUser.getId()) {
                    validateUser(user, someUser);
                }
            }

            data.replace(user.getId(), user);
            log.info("Изменен пользователь: {}", user);
            return user;

        } else {
            log.error("Пользователь с идентификатором {} не зарегистрирован", user.getId());
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %s не зарегистрирован.", user.getId()));
        }
    }

    private void validateUser(User user, User someUser) {
        if (someUser.getEmail().equals(user.getEmail())) {
            log.error("Пользователь с электронной почтой {} уже зарегистрирован.", user.getEmail());
            throw new UserAlreadyExistException(String
                    .format("Пользователь с электронной почтой %s уже зарегистрирован.", user.getEmail()));
        }
        if (someUser.getLogin().equals(user.getLogin())) {
            log.error("Логин {} уже занят.", user.getLogin());
            throw new UserAlreadyExistException(String.format("Логин %s уже занят.", user.getLogin()));
        }
    }

    @Override
    public void deleteUser(Integer id) {
        if (data.containsKey(id)) {
            data.remove(id);
            log.info("Удален пользователь с идентификатором {}", id);
        } else {
            log.error("Пользователь с идентификатором {} не зарегистрирован", id);
            throw new UserNotFoundException(String
                    .format("Пользователь с идентификатором %s не зарегистрирован.", id));
        }
    }

    @Override
    public User findUserById(Integer id) {
        if (data.containsKey(id)) {
            return data.get(id);
        } else {
            log.error(String.format("Пользователь с идентификатором %d не зарегистрирован", id));
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не зарегистрирован", id));
        }
    }

    @Override
    public List<User> getUserFriends(Integer userId) {

        List<User> result = new ArrayList<>();

        if (findUserById(userId).getFriends() != null) {
            for (Integer id : findUserById(userId).getFriends()) {
                result.add(findUserById(id));
            }
        }
        return result;
    }
}
