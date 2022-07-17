package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {

    public static void validateFilm(Film film) {

        var isValidate = false;
        var message = "";

        if (film.getName().isBlank()) {
            message = "Не указано название фильма.";
        } else if (film.getDescription().length() > 200) {
            message = "Описание фильма более 200 символов.";
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            message = "Дата релиза не может быть ранее 28 декабря 1895";
        } else if (film.getDuration() < 0) {
            message = "Продолжительность фильма отрицательная.";
        } else {
            isValidate = true;
        }
        if (!isValidate) {
            log.info(message);
            throw new ValidationException(message);
        }
    }
}
