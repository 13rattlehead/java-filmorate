package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Test
    void shouldCreateFilm() {

        Film film = new Film();
        film.setName("Interstellar");
        film.setDescription("Great movie");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);

        Film createdFilm = filmController.createFilm(film);

        assertNotNull(createdFilm.getId());
        assertEquals("Interstellar", createdFilm.getName());
        assertEquals(169, createdFilm.getDuration());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {

        Film film = new Film();
        film.setName(" ");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(100);

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {

        Film film = new Film();
        film.setName("Film");

        film.setDescription("A".repeat(201));

        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(100);

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.createFilm(film)
        );
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
    void shouldThrowExceptionWhenDurationIsNegative() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(-10);

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2010, 1, 1));
        film.setDuration(0);

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.createFilm(film)
        );
    }

    @Test
    void shouldThrowExceptionWhenFilmIsEmpty() {

        Film film = new Film();

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.createFilm(film)
        );
    }
}