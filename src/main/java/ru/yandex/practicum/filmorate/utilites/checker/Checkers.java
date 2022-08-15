package ru.yandex.practicum.filmorate.utilites.checker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

@Slf4j
public class Checkers {
    public static void checkUserExists(Long userId, JdbcTemplate jdbcTemplate) {
        var sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, userId).next()) {
            log.debug("Пользователь с id: {} не найден.", userId);
            throw new UserNotFoundException((String.format("Пользователь с id: %s не найден.",
                    userId)));
        }
    }

   public static void checkFilmExists(Long filmId, JdbcTemplate jdbcTemplate) {
        var sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, filmId).next()) {
            log.debug("Фильм с id: {} не найден.", filmId);
            throw new FilmNotFoundException((String.format("Фильм с id: %s не найден.",
                    filmId)));
        }
    }
}
