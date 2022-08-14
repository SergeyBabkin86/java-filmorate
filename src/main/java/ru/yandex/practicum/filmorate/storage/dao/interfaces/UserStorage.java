package ru.yandex.practicum.filmorate.storage.dao.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User getUser(long id);

    User addUser(User user);

    User updateUser(User user);

    Collection<User> getUsers();
}
