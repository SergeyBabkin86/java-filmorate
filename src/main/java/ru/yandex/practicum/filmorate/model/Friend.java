package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    private Long userId;
    private Long friendId;
}
