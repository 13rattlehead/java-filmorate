package ru.yandex.practicum.filmorate.dto.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaMapper {

    public MpaDto toDto(Mpa mpa) {
        if (mpa == null) {
            return null;
        }

        return new MpaDto(
                mpa.getId(),
                mpa.getName()
        );
    }

    public Mpa toEntity(MpaDto dto) {
        if (dto == null) {
            return null;
        }

        Mpa mpa = new Mpa();
        mpa.setId(dto.getId());
        mpa.setName(dto.getName());

        return mpa;
    }
}
