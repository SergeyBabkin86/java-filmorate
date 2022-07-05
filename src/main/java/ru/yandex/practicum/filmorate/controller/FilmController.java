package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends Controller {

    @PostMapping()
    public Model addFilm(@Valid @RequestBody Film film) {
        FilmValidator.validateFilm(film);
        return addModel(film);
    }

    @PutMapping()
    public Model updateFilm(@Valid @RequestBody Film film) throws IOException {
        FilmValidator.validateFilm(film);
        return updateModel(film);
    }
}
