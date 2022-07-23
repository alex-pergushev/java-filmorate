package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {

    private final MpaDaoImpl mpaStorage;

    public MpaService(MpaDaoImpl mpaStorage) {
        this.mpaStorage = mpaStorage;
    }


    public Mpa get(int id) {
        return mpaStorage.get(id);
    }

    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }


}
