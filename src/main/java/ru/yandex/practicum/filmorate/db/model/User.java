package ru.yandex.practicum.filmorate.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
	private int id;
	@NotBlank(message = "Электронная почта не может быть пустой")
	@Email(message = "Некорректный адрес электронной почты")
	private final String email;
	@NotBlank(message = "Логин не может быть пустым")
	@Pattern(regexp = "\\S*", message = "Логин не должен содержать пробелы")// не должен содержать пробелы
	private String login;
	// имя для отображения может быть пустым — в таком случае будет использован логин
	private String name;
	@PastOrPresent(message = "Дата рождения не может быть в будущем")
	private LocalDate birthday;
	public String getName() {
		// Если имя не заданно, имя равно логин
		return (name == null || name.isEmpty()) ? login : name;
	}
}