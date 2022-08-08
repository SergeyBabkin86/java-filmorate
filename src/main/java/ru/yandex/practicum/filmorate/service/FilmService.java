package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private long filmId = 1;
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final UserService userService;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorageStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorageStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        FilmValidator.validateFilm(film);
        film.setId(generateFilmId());
        inMemoryFilmStorage.addFilm(film);
        log.debug("Добавлен новый пользователь с id: {}.", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        FilmValidator.validateFilm(film);
        getFilm(film.getId());
        inMemoryFilmStorage.updateFilm(film);
        log.debug("Информация о фильме с id: {} обновлена.", film.getId());
        return film;
    }

    public Set<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    public Film getFilm(long filmId) {
        return getFilms()
                .stream()
                .filter(p -> p.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException((String.format("Фильм с id: %s не найден.", filmId))));
    }

    public boolean addLike(Long filmId, Long userId) {
        var isLikeAdded = getFilm(filmId).getLikes().add(userService.getUser(userId).getId());
        if (!isLikeAdded) {
            throw new RuntimeException(String.format("Пользователь c id: %s уже ставил лайк фильму с id: %s.",
                    userId,
                    filmId));
        }
        log.debug("Пользователь c id: {} поставил лайк фильму с id: {}", userId, filmId);
        return true;
    }

    public boolean deleteLike(Long filmId, Long userId) {
        var isLikeDeleted = getFilm(filmId).getLikes().remove(userService.getUser(userId).getId());
        if (!isLikeDeleted) {
            throw new RuntimeException(String.format("Пользователь с id: %s не ставил лайк фильму с id: %s",
                    userId, filmId));
        }
        log.debug("Пользователь с id: {} удалил свой лайк фильму с id: {}.",
                userId, filmId);
        return true;
    }

    public List<Film> getPopular(int count) {
        var popularFilms = new ArrayList<>(getFilms());
        popularFilms.sort((o1, o2) -> o2.getLikes().size() - o1.getLikes().size());
        return popularFilms.stream().limit(count).collect(Collectors.toList());
    }

    private long generateFilmId() {
        return filmId++;
    }
}
