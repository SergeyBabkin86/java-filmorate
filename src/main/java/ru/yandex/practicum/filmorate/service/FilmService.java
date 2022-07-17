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
        log.trace("Добавлен новый пользователь с id: {}.", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        FilmValidator.validateFilm(film);
        getFilm(film.getId()); // Throw FilmNotFoundException if absent.
        inMemoryFilmStorage.updateFilm(film);
        log.trace("Информация о фильме с id: {} обновлена.", film.getId());
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

    public String addLike(Long filmId, Long userId) {
        var film = getFilm(filmId); // Throw FilmNotFoundException if absent.
        var user = userService.getUser(userId); // Throw UserNotFoundException if absent.
        film.getLikes().add(user.getId());
        log.trace("Пользователь c id: {} поставил лайк фильму с id: {}", user.getId(), film.getId());
        return String.format("Всего лайков у фильма c id: %s : %s.", filmId, film.getLikes().size());
    }

    public String deleteLike(Long filmId, Long userId) {
        var film = getFilm(filmId); // Throw FilmNotFoundException if absent.
        var user = userService.getUser(userId); // Throw UserNotFoundException if absent.
        if (!film.getLikes().remove(user.getId())) {
            throw new RuntimeException(String.format("Пользователь с id: %s не ставил лайк фильму с id: %s",
                    user.getId(), film.getId()));
        }
        return String.format("Пользователь с id: %s удалил свой лайк фильму с id: %s",
                user.getId(), film.getId());
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
