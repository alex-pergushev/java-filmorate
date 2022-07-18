package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный адрес электронной почты")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S*", message = "Логин не должен содержать пробелы")// не должен содержать пробелы
    private String login;

    // имя для отображения может быть пустым — в таком случае будет использован логин
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Integer> friends;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User() {
    }

    public String getName() {
        // Если имя не заданно, имя равно логин
        return (name == null || name.isEmpty()) ? login : name;
    }


}