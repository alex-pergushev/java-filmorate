package ru.yandex.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InMemoryDataStorage<D> {

    protected Map<Integer, D> data = new HashMap<>();

    public List<D> findAll() {
        return new ArrayList<>(data.values());
    }

    public int generateNewId() {
        int resultate = 0;
        //Поиск первого незанятого идентификатора
        for (int i = 1; i <= (data.size() + 1); i++) {
            if (!data.containsKey(i)) {
                resultate = i;
                break;
            }
        }
        return resultate;
    }
}
