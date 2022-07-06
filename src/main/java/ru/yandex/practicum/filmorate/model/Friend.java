package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// класс для связи «дружба» между двумя пользователями
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    private int userId;
    private int friendId;
}
