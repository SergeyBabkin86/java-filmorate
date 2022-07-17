package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) throws IOException {
        return userService.updateUser(user);
    }

    @GetMapping()
    public Set<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUser(Long.parseLong(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public String addFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.addFriend(Long.parseLong(id), Long.parseLong(friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public String deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.deleteFriend(Long.parseLong(id), Long.parseLong(friendId));
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable String id) {
        return userService.getFriends(Long.parseLong(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.getCommonFriends(Long.parseLong(id), Long.parseLong(otherId));
    }
}
