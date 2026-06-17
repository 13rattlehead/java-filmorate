package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {

    private static final LocalDate CINEMA_BIRTHDAY =
            LocalDate.of(1895, 12, 28);

    @NotNull
    private LocalDate releaseDate;

    @AssertTrue(message = "Дата релиза не может быть раньше 28.12.1895")
    public boolean isReleaseDateValid() {
        return releaseDate == null ||
                !releaseDate.isBefore(CINEMA_BIRTHDAY);
    }

    private static final Integer MAX_DESCRIPTION_LENGTH = 200;

    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;


    @NotNull(message = "Продолжительность фильма не может быть пустой")
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    private Mpa mpa;

    private Set<Genre> genres = new HashSet<>();

    private Set<Long> likes = new HashSet<>();

}
