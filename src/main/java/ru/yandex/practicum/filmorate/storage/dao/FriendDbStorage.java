package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.FriendStorage;
import ru.yandex.practicum.filmorate.utilites.rowMappers.UserRowMapper;
import ru.yandex.practicum.filmorate.utilites.checker.Checkers;

import java.util.Collection;

import static java.lang.String.*;

@Repository
@Slf4j
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        Checkers.checkUserExists(userId, jdbcTemplate);
        Checkers.checkUserExists(friendId, jdbcTemplate);

        if (isFriendExist(userId, friendId)) {
            throw new RuntimeException(format("Пользователь c id: %s уже является другом пользователя с id: %s.",
                    userId,
                    friendId));
        }

        final var updateFriendQuery = "UPDATE friends SET user_id = ?, friend_id = ?, is_friends = ? WHERE user_id = ?";
        final var addFriendQuery = "INSERT INTO friends (user_id, friend_id, is_friends) VALUES (?, ?, ?)";

        if (isFriendExist(friendId, userId)) {
            jdbcTemplate.update(addFriendQuery, userId, friendId, true);
            jdbcTemplate.update(updateFriendQuery, friendId, userId, true, friendId);
            log.debug("Пользователи c id: {} и {} теперь друзья.", userId, friendId);
        } else {
            jdbcTemplate.update(addFriendQuery, userId, friendId, false);
            log.debug("Пользователь c id: {} добавил друга с id: {}. Пока не друзья", userId, friendId);
        }
        return true;
    }
    @Override
    public boolean deleteFriend(long userId, long friendId) {
        Checkers.checkUserExists(userId, jdbcTemplate);
        Checkers.checkUserExists(friendId, jdbcTemplate);

        if (!isFriendExist(userId, friendId)) {
            throw new RuntimeException(format("Пользователь c id: %s не друг пользователя с id: %s.",
                    friendId,
                    userId));
        }

        final var deleteFriendQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        final var updateFriendQuery = "UPDATE friends SET user_id = ?, friend_id = ?, is_friends = ? WHERE user_id = ?";

        jdbcTemplate.update(deleteFriendQuery, userId, friendId);
        log.debug("Пользователи c id: {} и {} больше не друзья.", userId, friendId);

        if (isFriendExist(friendId, userId)) {
            jdbcTemplate.update(updateFriendQuery, friendId, userId, false, friendId);
            log.debug("Статус дружбы пользователй c id: {} и {} изменён.", friendId, userId);
        }
        return true;
    }
    @Override
    public Collection<User> getFriends(long userId) {
        Checkers.checkUserExists(userId, jdbcTemplate);

        final var sqlQuery = "SELECT U.USER_ID, EMAIL, LOGIN, USER_NAME, BIRTHDATE\n" +
                "FROM FRIENDS AS F\n" +
                "LEFT OUTER JOIN USERS AS U ON F.FRIEND_ID = U.USER_ID\n" +
                "WHERE F.USER_ID=?";
        return jdbcTemplate.query(sqlQuery, new UserRowMapper(), userId);
    }

    public Collection<User> getCommonFriends(long userId, Long otherId) {
        Checkers.checkUserExists(userId, jdbcTemplate);
        Checkers.checkUserExists(otherId, jdbcTemplate);

        final var sqlQuery = "SELECT u.user_id, email, login, user_name, birthdate\n" +
                "FROM (SELECT * FROM friends WHERE user_id = ?) AS f1 \n" +
                "INNER JOIN (SELECT * FROM friends WHERE user_id = ?) AS f2 ON f1.friend_id = f2.friend_id\n" +
                "JOIN users AS U ON f2.friend_id = u.user_id";
        return jdbcTemplate.query(sqlQuery, new UserRowMapper(), userId, otherId);
    }

    private boolean isFriendExist(long userId, long friendId) {
        if (userId == friendId) {
            throw new RuntimeException("Невозможно быть другом самого себя.");
        }
        final var sqlQuery = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId).next();
    }
}
