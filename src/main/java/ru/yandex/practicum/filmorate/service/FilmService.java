package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, LikeDbStorage likeDbStorage) {
        this.filmStorage = filmStorage;
        this.likeDbStorage = likeDbStorage;
    }

    public Film getFilm(long filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Film addFilm(Film film) {
        FilmValidator.validateFilm(film);
        filmStorage.addFilm(film);
        log.debug("Добавлен новый пользователь с id: {}.", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        FilmValidator.validateFilm(film);
        filmStorage.updateFilm(film);
        log.debug("Информация о фильме с id: {} обновлена.", film.getId());
        return film;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public boolean addLike(Long filmId, Long userId) {
        return likeDbStorage.addLike(filmId, userId);
    }

    public boolean deleteLike(Long filmId, Long userId) {
        return likeDbStorage.deleteLike(filmId, userId);
    }

    public Collection<Film> getPopular(int count) {
        return likeDbStorage.getPopularFilms(count);
    }
}
