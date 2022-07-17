package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private long userId = 1;
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        UserValidator.validateUser(user); // Throw UserNotFoundException.
        user.setId(generateUserId());
        inMemoryUserStorage.addUser(user);
        log.trace("Добавлен новый пользователь с id: {}.", user.getId());
        return user;
    }

    public User updateUser(User user) {
        UserValidator.validateUser(user); // Throw UserNotFoundException.
        getUser(user.getId()); // Throw UserNotFoundException.
        inMemoryUserStorage.updateUser(user);
        log.trace("Информация о пользователе с id: {} обновлена.", user.getId());
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

    public String addFriend(long userId, long friendId) {
        var user = getUser(userId); // Throw UserNotFoundException if absent.
        var friend = getUser(friendId); // Throw UserNotFoundException if absent.
        user.getFriendsIdSet().add(friendId);
        log.trace("Пользователь c id: {} добавил нового друга с id: {}", userId, friendId);
        friend.getFriendsIdSet().add(userId);
        log.trace("Пользователю c id: {} добавлен новый друг с id: {}", friendId, userId);
        return String.format("Пользователи c id: %s и %s теперь друзья.", userId, friendId);
    }

    public String deleteFriend(long userId, long friendId) {
        var user = getUser(userId); // Throw UserNotFoundException if absent.
        var friend = getUser(friendId); // Throw UserNotFoundException if absent.
        user.getFriendsIdSet().remove(friendId);
        log.trace("Пользователь c id: {} удалил друга с id: {}", userId, friendId);
        friend.getFriendsIdSet().remove(userId);
        log.trace("У пользователя c id: {} удален друг с id: {}", friendId, userId);
        return String.format("Пользователи c id: %s и %s больше не друзья.", userId, friendId);
    }

    public Set<User> getFriends(long userId) {
        Set<User> friendsSet = new HashSet<>();
        var friendsIdSet = getUser(userId).getFriendsIdSet(); // Throw UserNotFoundException if absent.

        if (friendsIdSet.isEmpty()) {
            log.info("У пользователя c id: {} нет друзей =(.", userId);
        } else {
            for (Long friendId : friendsIdSet) {
                friendsSet.add(getUser(friendId));
            }
        }
        return friendsSet;
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
