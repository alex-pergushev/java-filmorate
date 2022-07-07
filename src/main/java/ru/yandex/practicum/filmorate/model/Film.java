package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов") // максимальная длина 200 символов
    private String description;

    // не раньше 1895-12-28
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной") // должна быть >0
    private int duration;

    // рейтинг Ассоциации кинокомпаний (англ. Motion Picture Association, сокращённо МРА)
    private Rating rating;

    // жанры
    private List<Genre> genres;

    private Set<Integer> likes;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Rating rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rating = rating;
    }
}