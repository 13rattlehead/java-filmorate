package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        validateFilm(filmId);

        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        Film film = filmStorage.findById(filmId).get();
        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validateFilm(filmId);

        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        Film film = filmStorage.findById(filmId).get();
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size())
                        .reversed())
                .limit(count)
                .collect(Collectors.toList());
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
}
