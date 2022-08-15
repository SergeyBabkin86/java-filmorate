package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.LikeStorage;
import ru.yandex.practicum.filmorate.utilites.checker.Checkers;
import ru.yandex.practicum.filmorate.utilites.rowMappers.FilmRowMapper;

import java.util.Collection;

@Repository
@Qualifier
@Slf4j
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        Checkers.checkFilmExists(filmId, jdbcTemplate);
        Checkers.checkUserExists(userId, jdbcTemplate);

        if (isLikeAdded(filmId, userId)) {
            throw new RuntimeException(String.format("Пользователь c id: %s уже ставил лайк фильму с id: %s.",
                    userId,
                    filmId));
        }
        jdbcTemplate.update("INSERT INTO LIKES VALUES (?, ?)", filmId, userId);
        jdbcTemplate.update("UPDATE FILMS SET RATE = RATE + 1 WHERE FILM_ID = ?", filmId);
        log.debug("Пользователь c id: {} поставил лайк фильму с id: {}", userId, filmId);
        return true;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        Checkers.checkFilmExists(filmId, jdbcTemplate);
        Checkers.checkUserExists(userId, jdbcTemplate);
        if (!isLikeAdded(filmId, userId)) {
            throw new RuntimeException(String.format("Пользователь с id: %s не ставил лайк фильму с id: %s",
                    userId, filmId));
        }
        jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?", filmId, userId);
        jdbcTemplate.update("UPDATE FILMS SET RATE = RATE - 1 WHERE FILM_ID = ?", filmId);
        log.debug("Пользователь с id: {} удалил свой лайк фильму с id: {}.", userId, filmId);
        return true;
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        var sqlQuery = "SELECT * FROM FILMS " +
                "JOIN MPA M on M.MPA_ID = FILMS.MPA_ID " +
                "ORDER BY RATE DESC " +
                "LIMIT(?)";
        return jdbcTemplate.query(sqlQuery, new FilmRowMapper(), count);
    }

    private boolean isLikeAdded(Long filmId, Long userId) {
        var sqlQuery = "SELECT * FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId).next();
    }
}
