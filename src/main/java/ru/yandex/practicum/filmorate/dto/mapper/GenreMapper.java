package ru.yandex.practicum.filmorate.dto.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class GenreMapper {

    public GenreDto toDto(Genre genre) {
        if (genre == null) {
            return null;
        }

        return new GenreDto(
                genre.getId(),
                genre.getName()
        );
    }

    public Genre toEntity(GenreDto dto) {
        if (dto == null) {
            return null;
        }

        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());

        return genre;
    }
}