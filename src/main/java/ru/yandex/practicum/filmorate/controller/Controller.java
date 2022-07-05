package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.filmorate.model.Model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class Controller {

    protected int id = 1;
    protected final Map<Integer, Model> dataMap = new HashMap<>();

    @GetMapping
    public Set<Model> getModels() {
        return new HashSet<>(dataMap.values());
    }

    public Model addModel(Model model) {
        model.setId(generateId());
        dataMap.put(model.getId(), model);
        log.info("Добавлен новый: {}.", model);
        return model;
    }

    public Model updateModel(Model model) throws IOException {
        if (!dataMap.containsKey(model.getId())) {
            log.info("Пользователя с {} не существует.", model);
            throw new IOException();
        } else {
            dataMap.replace(model.getId(), model);
            log.info("Информация о {} обновлена.", model);
        }
        return model;
    }

    protected int generateId() {
        return id++;
    }
}

