package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    // создать пользователя
    User create(User user);

    // обновить пользователя
    User update(User user);

    // удалить пользователя
    void deleteUser(Integer id);

    // найти всех пользователей
    List<User> findAll();

    // найти пользователя по идентификатору
    User findUserById(Integer id);

    // получить список друзей пользователя
    List<User> getUserFriends(Integer userId);

    // добавить в друзья
    void addFriend(Integer userId, Integer friendId);

    // удалить из друзей
    void deleteFriend(Integer userId, Integer friendId);

    // список общих друзей
    List<User> getCommonFriends(Integer userId, Integer otherId);



}
