package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// связь жанра с фильмом
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmGenre {
    private int filmId;
    private int genreId;

}
