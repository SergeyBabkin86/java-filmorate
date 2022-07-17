package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.util.Set;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user) throws IOException;

    Set<User> getUsers();
}
