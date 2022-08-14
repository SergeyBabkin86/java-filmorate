package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendDbStorage friendDbStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendDbStorage friendDbStorage) {
        this.userStorage = userStorage;
        this.friendDbStorage = friendDbStorage;
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    public User addUser(User user) {
        UserValidator.validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        UserValidator.validateUser(user);
        return userStorage.updateUser(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public boolean addFriend(long userId, long friendId) {
        return friendDbStorage.addFriend(userId, friendId);
    }

    public boolean deleteFriend(long userId, long friendId) {
        return friendDbStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        return friendDbStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(long userId, Long otherId) {
        return friendDbStorage.getCommonFriends(userId, otherId);
    }
}
