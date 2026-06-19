package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;


@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<Collection<GenreDto>> getGenres() {
        return ResponseEntity.ok(
                genreService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenre(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                genreService.findById(id)
        );
    }
}