package ru.yandex.practicum.filmorate.storage.dao.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendStorage {
    boolean addFriend(long userId, long friendId);

    boolean deleteFriend(long userId, long friendId);

    Collection<User> getFriends(long userId);
}
