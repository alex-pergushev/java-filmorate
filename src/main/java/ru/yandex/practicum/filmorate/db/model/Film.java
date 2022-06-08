package ru.yandex.practicum.filmorate.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
	private int id;
	@NotBlank(message = "Название фильма не может быть пустым")
	private final String name;
	@Size(max = 200, message = "Максимальная длина описания фильма — 200 символов") // максимальная длина 200 символов
	private String description;
	// не раньше 1895-12-28
	private LocalDate releaseDate;
	@Positive(message = "Продолжительность фильма должна быть положительной") // должна быть >0
	private int duration;
}