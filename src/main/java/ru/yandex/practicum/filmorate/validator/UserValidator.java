package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    public static void validateUser(User user) {

        var isValidate = false;
        String message = "";

        if (user.getEmail().isBlank()) {
            message = "Указан пустой email.";
        } else if (!user.getEmail().contains("@")) {
            message = String.format("Email '%s' не содержит символ '@'.", user.getEmail());
        } else if (user.getLogin().isBlank()) {
            message = "Логин не указан.";
        } else if (user.getLogin().contains(" ")) {
            message = "Логин содержит пробелы.";
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            message = "Дата рождения не может быть в будущем.";
        } else {
            isValidate = true;
        }

        if (!isValidate) {
            log.info(message);
            throw new ValidationException(message);
        }
        validateUserName(user);
    }

    private static void validateUserName(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
