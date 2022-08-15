package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Builder
@Data
public class Film {
    private long id;
    @NotNull
    private String name;
    private String description;
    @PastOrPresent(message = "Дата релиза фильма не может быть в будущем.")
    private LocalDate releaseDate;
    private int duration;
    private int rate;
    private MPA mpa;
    private Set<Genre> genres;
}
