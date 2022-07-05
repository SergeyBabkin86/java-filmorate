package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Builder
@Data
public class Film implements Model {
    private int id;
    @NotNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
}
