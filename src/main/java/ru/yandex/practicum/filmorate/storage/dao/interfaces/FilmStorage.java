package ru.yandex.practicum.filmorate.storage.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {

    Film getFilm(long filmId);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getFilms();
}
