package ru.yandex.practicum.filmorate.storage.inMemoryStorage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.FilmStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    protected final Map<Long, Film> filmsDataMap = new HashMap<>();

    @Override
    public Film getFilm(long filmId) {
        return null;
    }

    @Override
    public Film addFilm(Film film) {
        filmsDataMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmsDataMap.replace(film.getId(), film);
        return film;
    }

    @Override
    public Set<Film> getFilms() {
        return new HashSet<>(filmsDataMap.values());
    }
}
