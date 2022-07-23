package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@RestController
@RequestMapping(value = "/mpa")
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }


    @GetMapping(value = "/{id}")
    public Mpa findMpaById(@PathVariable("id") Integer mpaId) {
        return mpaService.get(mpaId);
    }

    @GetMapping
    public List<Mpa> findAll() {
        return mpaService.getAll();
    }
}
