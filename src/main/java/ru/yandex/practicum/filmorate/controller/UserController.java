package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    // добавление в друзья
    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        userService.addToFriends(userId, friendId);
    }

    // удаление из друзей
    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("id") Integer userId, @PathVariable Integer friendId) {
        userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    // получать данные о пользователях по их уникальному идентификатору
    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable("id") Integer userId) {
        return userService.findUserById(userId);
    }

    // возвращаем список пользователей, являющихся его друзьями.
    @GetMapping(value = "/{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") Integer userId) {
        return userService.getUserFriends(userId);
    }

    // список друзей, общих с другим пользователем
    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable Integer otherId) {
        return userService.getCommonFriends(userId, otherId);
    }
}
