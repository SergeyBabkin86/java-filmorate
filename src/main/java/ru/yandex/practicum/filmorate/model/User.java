package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
public class User {
    private long id;
    @NotNull
    private String email;
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;

    private final Set<Long> friendsIdSet = new HashSet<>();
}
