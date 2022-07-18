package ru.yandex.practicum.filmorate.dao;

import java.util.List;
// простые сущности с ключем
public interface EntityDao<E> {

    // обращение к сущности по идентификатору
    E get(Integer id);

    // создание сущности
    E create(E data);

    // удаление сущности
    void delete(Integer id);

    // обновление сущности
    E update(E data);

    // получение списка всех сущностей
    List<E> getAll();

}
