package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    private static final LocalDate CINEMA_BIRTHDAY =
            LocalDate.of(1895, 12, 28);

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaStorage mpaStorage,
            GenreStorage genreStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Фильм с ID " + id + " не найден"
                        ));
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ConditionsNotMetException("Фильм не может быть раньше даты: 28.12.1895");
        }
        validateMpa(film);
        validateGenres(film);

        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film.getId());
        validateMpa(film);
        validateGenres(film);

        return filmStorage.updateFilm(film);
    }

    public void addLike(Long filmId, Long userId) {
        validateFilm(filmId);
        validateUser(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilm(filmId);
        validateUser(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Long filmId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> {
                    log.warn("Фильм с ID {} не найден", filmId);
                    return new NotFoundException(
                            "Фильм с ID " + filmId + " не найден"
                    );
                });
    }

    private void validateUser(Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", userId);
                    return new NotFoundException(
                            "Пользователь с ID " + userId + " не найден"
                    );
                });
    }

    private void validateMpa(Film film) {

        if (film.getMpa() == null) {
            throw new NotFoundException(
                    "Рейтинг MPA не указан"
            );
        }

        mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() ->
                        new NotFoundException(
                                "Рейтинг MPA с ID "
                                        + film.getMpa().getId()
                                        + " не найден"
                        ));
    }

    private void validateGenres(Film film) {

        if (film.getGenres() == null
                || film.getGenres().isEmpty()) {
            return;
        }

        film.getGenres().forEach(genre ->
                genreStorage.findById(genre.getId())
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Жанр с ID "
                                                + genre.getId()
                                                + " не найден"
                                )));
    }
}