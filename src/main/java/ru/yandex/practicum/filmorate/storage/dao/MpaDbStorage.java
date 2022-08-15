package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.dao.interfaces.MpaStorage;
import ru.yandex.practicum.filmorate.utilites.rowMappers.MPARowMapper;

import java.util.Collection;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MPA getMpa(long mpaId) {
        var sqlQuery = "SELECT * FROM MPA WHERE MPA_ID = ?";
        if (!jdbcTemplate.queryForRowSet(sqlQuery, mpaId).next()) {
            throw new EntityNotFoundException(String.format("MPA с id: %s не существует", mpaId));
        }
        return jdbcTemplate.queryForObject(sqlQuery, new MPARowMapper(), mpaId);
    }

    @Override
    public Collection<MPA> getAllMpa() {
        var sqlQuery = "SELECT * FROM MPA";
        return jdbcTemplate.query(sqlQuery, new MPARowMapper());
    }
}
