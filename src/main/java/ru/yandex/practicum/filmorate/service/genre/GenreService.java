package ru.yandex.practicum.filmorate.service.genre;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {

    private final GenreDaoImpl genreStorage;

    public GenreService(GenreDaoImpl genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre get(int id) {
        return genreStorage.get(id);
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }
}
