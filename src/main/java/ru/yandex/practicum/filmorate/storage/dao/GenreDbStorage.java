package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.utilites.rowMappers.GenreRowMapper;

import java.util.Collection;

@Repository
@Qualifier
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre(long genreId) {
        var sqlQuery = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, genreId).next()) {
            throw new ObjectNotFoundException(String.format("Жанра с id: %s не существует", genreId));
        }
        return jdbcTemplate.queryForObject(sqlQuery, new GenreRowMapper(), genreId);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        var sqlQuery = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sqlQuery, new GenreRowMapper());
    }
}