package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.utilites.rowMappers.UserRowMapper;
import ru.yandex.practicum.filmorate.utilites.checker.Checkers;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Collection;


@Repository("userDbStorage")
@Qualifier
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(long userId) {
        final var sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        Checkers.checkUserExists(userId, jdbcTemplate);
        return jdbcTemplate.queryForObject(sqlQuery, new UserRowMapper(), userId);
    }

    @Override
    public User addUser(User user) {
        var keyHolder = new GeneratedKeyHolder();
        final var sqlQuery = "INSERT INTO users (email, login, user_name, birthdate) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, Date.valueOf(birthday));
            }
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        log.debug("Добавлен новый пользователь с id: {}.", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        Checkers.checkUserExists(user.getId(), jdbcTemplate);
        var sqlQuery = "UPDATE users SET email = ?, login = ?, user_name = ?, birthdate = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.debug("Информация о пользователе с id: {} обновлена.", user.getId());
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        final var sqlQuery = "SELECT user_id, email, login, user_name, birthdate FROM users GROUP BY user_id";
        return jdbcTemplate.query(sqlQuery, new UserRowMapper());
    }
}
