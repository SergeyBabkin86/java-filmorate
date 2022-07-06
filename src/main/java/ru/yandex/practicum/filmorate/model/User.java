package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Builder
@Data
public class User {
    private int id;
    @NotNull
    private String email;
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
}
