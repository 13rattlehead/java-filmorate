package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;


public interface FilmStorage {

    Film createFilm(Film film);

    Collection<Film> getFilms();

    Film updateFilm(Film film);

    Optional<Film> findById(Long id);

}
