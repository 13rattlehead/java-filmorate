package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private Validator validator;

    @Test
    void shouldCreateFilm() {

        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("Great movie");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);

        Set<ConstraintViolation<Film>> violations =
                validator.validate(film);

        assertTrue(violations.isEmpty());

        Film createdFilm = filmController.createFilm(film);

        assertNotNull(createdFilm.getId());
        assertEquals("Interstellar", createdFilm.getName());
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {

        Film film = new Film();
        film.setName(" ");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations =
                validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsTooLong() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations =
                validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenDurationIsNegative() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(-10);

        Set<ConstraintViolation<Film>> violations =
                validator.validate(film);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsTooEarly() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");

        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        film.setDuration(100);

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldFailValidationWhenFilmIsEmpty() {

        Film film = new Film();

        Set<ConstraintViolation<Film>> violations =
                validator.validate(film);

        assertFalse(violations.isEmpty());
    }
}