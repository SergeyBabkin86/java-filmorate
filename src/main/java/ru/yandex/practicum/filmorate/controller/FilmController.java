package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int filmId = 1;
    protected final Map<Integer, Film> filmsDataMap = new HashMap<>();

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        FilmValidator.validateFilm(film);
        film.setId(generateFilmId());
        filmsDataMap.put(film.getId(), film);
        log.info("Добавлен новый: {}.", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) throws IOException {
        FilmValidator.validateFilm(film);
        if (!filmsDataMap.containsKey(film.getId())) {
            log.info("Пользователя с {} не существует.", film);
            throw new IOException();
        } else {
            filmsDataMap.replace(film.getId(), film);
            log.info("Информация о {} обновлена.", film);
        }
        return film;
    }

    @GetMapping()
    public Set<Film> getFilms() {
        return new HashSet<>(filmsDataMap.values());
    }

    private int generateFilmId() {
        return filmId++;
    }
}
