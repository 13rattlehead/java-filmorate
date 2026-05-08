package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_VALIDATION_DATE = LocalDate
            .of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {

        validateFilm(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм создан: {}", film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {

        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Фильм с id: {} не найден", film.getId());
            throw new ConditionsNotMetException("Фильм не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);

        log.info("Фильм обновлен: {}", film);

        return film;
    }

    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return currentMaxId + 1;
    }

    public void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("У фильма должно быть название");
            throw new ConditionsNotMetException("У фильма должно быть название");
        }

        if (film.getDescription() != null || film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("У фильма должно быть описание не более 200 символов");
            throw new ConditionsNotMetException("У фильма должно быть описание не более 200 символов");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_VALIDATION_DATE)) {
            log.warn("У фильма должна быть дата релиза не раньше 28 декабря 1895 года");
            throw new ConditionsNotMetException("У фильма должна быть дата релиза " +
                    "не раньше 28 декабря 1895 года");

        }
        if (film.getDuration()== null || film.getDuration() <= 0) {
            log.warn("У фильма должна быть положительная продолжительность");
            throw new ConditionsNotMetException("У фильма должна быть положительная продолжительность");
        }
    }
}
