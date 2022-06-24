package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    public User findUserById(Integer id) {
        return userStorage.findUserById(id);
    }

    public void addToFriends(Integer userId, Integer friendId) {
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

    public void deleteFromFriends(Integer userId, Integer friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user.getFriends() != null && user.getFriends().contains(friend.getId())) {
            user.getFriends().remove(friend.getId());
            if (friend.getFriends() != null && friend.getFriends().contains(user.getId())) {
                friend.getFriends().remove(user.getId());
            }
        }
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getUserFriends(userId);
    }

    public List<User> getFriendsMutual(Integer userId, Integer otherId) {
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherId);

        List<User> result = new ArrayList<>();

        if (user.getFriends() != null && otherUser.getFriends() != null) {

            for (Integer id : user.getFriends()) {
                if (otherUser.getFriends().contains(id)) {
                    result.add(userStorage.findUserById(id));
                }
            }
        }
        return result;
    }
}
