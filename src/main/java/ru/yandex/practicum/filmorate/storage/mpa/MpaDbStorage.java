package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {

        String sql = """
                SELECT *
                FROM mpa_rating
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();

            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));

            return mpa;
        });
    }

    @Override
    public Optional<Mpa> findById(Integer id) {

        String sql = """
                SELECT *
                FROM mpa_rating
                WHERE id = ?
                """;

        List<Mpa> ratings = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Mpa mpa = new Mpa();

                    mpa.setId(rs.getInt("id"));
                    mpa.setName(rs.getString("name"));

                    return mpa;
                },
                id
        );

        return ratings.stream().findFirst();
    }
}