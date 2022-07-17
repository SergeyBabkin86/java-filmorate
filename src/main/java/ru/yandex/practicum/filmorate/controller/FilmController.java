package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) throws IOException {
        return filmService.updateFilm(film);
    }

    @GetMapping()
    public Set<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable String id) {
        return filmService.getFilm(Long.parseLong(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public String addLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.addLike(Long.parseLong(id), Long.parseLong(userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String deleteLike(@PathVariable String id, @PathVariable String userId) {
        return filmService.deleteLike(Long.parseLong(id), Long.parseLong(userId));
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopular(count);
    }
}
