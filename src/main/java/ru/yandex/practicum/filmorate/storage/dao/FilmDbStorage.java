package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.utilites.checker.Checkers;
import ru.yandex.practicum.filmorate.utilites.rowMappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.utilites.rowMappers.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@Repository
@Qualifier
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilm(long filmId) {
        Checkers.checkFilmExists(filmId, jdbcTemplate);
        final var sqlQuery = "SELECT * FROM FILMS F JOIN MPA M on F.MPA_ID = M.MPA_ID WHERE FILM_ID = ?";
        var film = jdbcTemplate.queryForObject(sqlQuery, new FilmRowMapper(), filmId);
        loadFilmGenre(film);
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        var keyHolder = new GeneratedKeyHolder();
        final var sqlQuery = "INSERT INTO FILMS (FILM_NAME, FILM_DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID) " +
                "values (?,?,?,?,?,?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            final LocalDate releaseDate = film.getReleaseDate();
            if (releaseDate == null) {
                stmt.setNull(3, Types.DATE);
            } else {
                stmt.setDate(3, Date.valueOf(releaseDate));
            }
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setLong(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        setFilmGenre(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Checkers.checkFilmExists(film.getId(), jdbcTemplate);
        final var sqlQuery = "UPDATE FILMS " +
                "SET FILM_NAME = ?, FILM_DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        setFilmGenre(film);
        loadFilmGenre(film);
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        var sqlRequest = "SELECT * FROM FILMS F JOIN MPA M on F.MPA_ID = M.MPA_ID";
        var films = jdbcTemplate.query(sqlRequest, new FilmRowMapper());
        loadFilmsGenres(films);
        return films;
    }

    private void setFilmGenre(Film film) {
        var sqlQuery = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRES VALUES (?,?)", film.getId(), genre.getId());
            }
        }
    }

    private void loadFilmGenre(Film film) {
        var sqlQuery = "SELECT * FROM GENRES " +
                "JOIN FILM_GENRES FG on GENRES.GENRE_ID = FG.GENRE_ID " +
                "JOIN FILMS F on F.FILM_ID = FG.FILM_ID " +
                "WHERE F.FILM_ID = ?" +
                "ORDER BY GENRES.GENRE_ID";
        var genreList = jdbcTemplate.query(sqlQuery, new GenreRowMapper(), film.getId());
        film.setGenres(new HashSet<>(genreList));
    }

    private void loadFilmsGenres(Collection<Film> films) {
        for (Film film : films) {
            loadFilmGenre(film);
        }
    }
}
