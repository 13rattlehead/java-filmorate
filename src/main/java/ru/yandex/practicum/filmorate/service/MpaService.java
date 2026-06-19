package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;
    private final MpaMapper mpaMapper;

    public Collection<MpaDto> findAll() {
        return mpaStorage.findAll()
                .stream()
                .map(mpaMapper::toDto)
                .toList();
    }

    public MpaDto findById(Integer id) {
        return mpaMapper.toDto(
                mpaStorage.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException("Рейтинг не найден"))
        );
    }
}