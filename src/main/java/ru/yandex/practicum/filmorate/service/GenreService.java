package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dto.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    public Collection<GenreDto> findAll() {
        return genreStorage.findAll()
                .stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    public GenreDto findById(Integer id) {
        Genre genre = genreStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Жанр с id=" + id + " не найден"));

        return genreMapper.toDto(genre);
    }
}