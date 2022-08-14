package ru.yandex.practicum.filmorate.storage.dao.interfaces;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

public interface MpaStorage {
    MPA getMpa(long mpaId);

    Collection<MPA> getAllMpa();
}
