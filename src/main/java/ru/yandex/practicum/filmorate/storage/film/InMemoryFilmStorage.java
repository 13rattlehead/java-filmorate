package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;


@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_VALIDATION_DATE = LocalDate
            .of(1895, 12, 28);

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {

        validateFilm(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм создан: {}", film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {

        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Фильм с id: {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);

        log.info("Фильм обновлен: {}", film);

        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {
        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        }
        log.info("Фильм с id: {} не найден", id);
        throw new NotFoundException("Фильм не найден");
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
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_VALIDATION_DATE)) {
            log.warn("У фильма должна быть дата релиза не раньше 28 декабря 1895 года");
            throw new ConditionsNotMetException("У фильма должна быть дата релиза " +
                    "не раньше 28 декабря 1895 года");

        }
    }

}
