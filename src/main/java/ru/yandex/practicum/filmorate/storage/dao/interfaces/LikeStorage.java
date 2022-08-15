package ru.yandex.practicum.filmorate.storage.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikeStorage {
    boolean addLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    Collection<Film> getPopularFilms(Integer count);
}