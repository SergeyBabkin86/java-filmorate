package ru.yandex.practicum.filmorate.storage.inMemoryStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.UserStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component ("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    protected final Map<Long, User> usersDataMap = new HashMap<>();

    @Override
    public User getUser(long id) {
        return null;
    }

    @Override
    public User addUser(User user) {
        usersDataMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        usersDataMap.replace(user.getId(), user);
        return user;
    }

    @Override
    public Set<User> getUsers() {
        return new HashSet<>(usersDataMap.values());
    }
}
