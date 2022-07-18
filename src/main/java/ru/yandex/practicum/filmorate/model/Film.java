package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
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
    private long duration;

    private final Set<Long> likes = new HashSet<>();
}
