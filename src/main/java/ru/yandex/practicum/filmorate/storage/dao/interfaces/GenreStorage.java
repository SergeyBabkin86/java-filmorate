package ru.yandex.practicum.filmorate.storage.dao.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {


    Genre getGenre(long genreId);

    Collection<Genre> getAllGenres();
}
