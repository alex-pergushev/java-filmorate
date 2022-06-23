package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

/*
который будет отвечать за такие операции с пользователями, как добавление в друзья,
удаление из друзей, вывод списка общих друзей. Пока пользователям не надо одобрять заявки
в друзья — добавляем сразу. То есть если Лена стала другом Саши, то это значит, что Саша
теперь друг Лены.
*/

@Service
public class UserService {

    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id);
    }

    public void addToFriends(int userId, int friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }

        if (!user.getFriends().contains(friend.getId())) {
            user.getFriends().add(friend.getId());
            if (!friend.getFriends().contains(user.getId())) {
                friend.getFriends().add(user.getId());
            }
        }
    }

    public void deleteFromFriends(int userId, int friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user.getFriends() != null && user.getFriends().contains(friend.getId())) {
            user.getFriends().remove(friend.getId());
            if (friend.getFriends() != null && friend.getFriends().contains(user.getId())) {
                friend.getFriends().remove(user.getId());
            }
        }
    }

    public List<User> getUserFriends(int userId) {
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherId);

        List<User> result = new ArrayList<>();

        if (user.getFriends() != null && otherUser.getFriends() != null) {
            Set<Integer> common =  user.getFriends();
            common.retainAll(otherUser.getFriends());
            for (Integer id : common) {
                result.add(userStorage.findUserById(id));
            }
        }
        return result;
    }
}
