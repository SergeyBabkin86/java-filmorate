package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Builder
@Data
public class Genre {
    private long id;
    private String name;
}
