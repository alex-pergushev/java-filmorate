package ru.yandex.practicum.filmorate.dao;

import java.util.List;

// сущности связи
public interface EntityLinkDao <L> {

    // добавление связи
    void create(Integer entityOneId, Integer entityTwoId);

    // удаление связи
    void delete(Integer entityOneId, Integer entityTwoId);

    // приведение списка связей
    void merge(Integer entityOneId, List<L> entityTwoList);

}
