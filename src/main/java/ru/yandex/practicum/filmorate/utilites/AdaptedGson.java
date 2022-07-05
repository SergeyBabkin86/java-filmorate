package ru.yandex.practicum.filmorate.utilites;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

abstract public class AdaptedGson {

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }
}