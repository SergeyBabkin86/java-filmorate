package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends Controller {

    @PostMapping()
    public Model addUser(@Valid @RequestBody User user) {
        UserValidator.validateUser(user);
        return addModel(user);
    }

    @PutMapping()
    public Model updateUser(@Valid @RequestBody User user) throws IOException {
        UserValidator.validateUser(user);
        return updateModel(user);
    }
}
