package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;


@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @GetMapping
    public Collection<Film> getFilms() {
        log.debug("Запрос на получение всех фильмов");
        Collection<Film> films = filmStorage.getFilms();
        log.info("Получено {} фильмов", films.size());
        return films;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Запрос на создание фильма: {}", film);
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Фильм создан: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Запрос на обновление фильма: {}", film);
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("Фильм обновлен: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Запрос на добавление лайка фильму {} от пользователя {}", id, userId);
        filmService.addLike(id, userId);
        log.info("Лайк добавлен фильму {} от пользователя {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Запрос на удаление лайка фильму {} от пользователя {}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Лайк удален фильму {} от пользователя {}", id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Запрос на получение {} популярных фильмов", count);
        Collection<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Получены {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }

}
