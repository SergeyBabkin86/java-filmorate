package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int userId = 1;
    protected final Map<Integer, User> usersDataMap = new HashMap<>();

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        UserValidator.validateUser(user);
        user.setId(generateUserId());
        usersDataMap.put(user.getId(), user);
        log.info("Добавлен новый: {}.", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) throws IOException {
        UserValidator.validateUser(user);
        if (!usersDataMap.containsKey(user.getId())) {
            log.info("Пользователя с {} не существует.", user);
            throw new IOException();
        } else {
            usersDataMap.replace(user.getId(), user);
            log.info("Информация о {} обновлена.", user);
        }
        return user;
    }

    @GetMapping()
    public Set<User> getUsers() {
        return new HashSet<>(usersDataMap.values());
    }

    private int generateUserId() {
        return userId++;
    }
}
