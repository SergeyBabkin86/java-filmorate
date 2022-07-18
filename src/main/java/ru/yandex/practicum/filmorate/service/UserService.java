package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private long userId = 1;
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        UserValidator.validateUser(user);
        user.setId(generateUserId());
        inMemoryUserStorage.addUser(user);
        log.debug("Добавлен новый пользователь с id: {}.", user.getId());
        return user;
    }

    public User updateUser(User user) {
        UserValidator.validateUser(user);
        getUser(user.getId());
        inMemoryUserStorage.updateUser(user);
        log.debug("Информация о пользователе с id: {} обновлена.", user.getId());
        return user;
    }

    public User getUser(long userId) {
        return getUsers()
                .stream()
                .filter(p -> p.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException((String.format("Пользователь с id: %s не найден.",
                        userId))));
    }

    public Set<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public boolean addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new RuntimeException("Невозможно добавить в друзья самого себя.");
        }
        var isFriendAdded = getUser(userId).getFriendsIdSet().add(getUser(friendId).getId());
        var isUserAdded = getUser(friendId).getFriendsIdSet().add(userId);
        if (!isFriendAdded && !isUserAdded) {
            throw new RuntimeException(String.format("Пользователи c id: %s и %s уже друзья.", userId, friendId));
        }
        log.debug("Пользователи c id: {} и {} теперь друзья.", userId, friendId);
        return true;
    }

    public boolean deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new RuntimeException("Невозможно удалить из друзей самого себя.");
        }
        var isFriendRemoved = getUser(userId).getFriendsIdSet().remove(getUser(friendId).getId());
        var isUserRemoved = getUser(friendId).getFriendsIdSet().remove(userId);
        if (!isFriendRemoved && !isUserRemoved) {
            throw new RuntimeException(String.format("Пользователи c id: %s и %s не друзья.", userId, friendId));
        }
        log.debug("Пользователи c id: {} и {} больше не друзья.", userId, friendId);
        return true;
    }

    public Set<User> getFriends(long userId) {
        var friendsIdSet = getUser(userId).getFriendsIdSet();
        return friendsIdSet.stream().map(this::getUser).collect(Collectors.toCollection(HashSet::new));
    }

    public Set<User> getCommonFriends(long userId, Long otherId) {
        var commonFriendsSet = getFriends(userId);
        var notCommonFriendsSet = getFriends(userId);

        notCommonFriendsSet.removeAll(getFriends(otherId));
        commonFriendsSet.removeAll(notCommonFriendsSet);
        return commonFriendsSet;
    }

    private long generateUserId() {
        return userId++;
    }
}
