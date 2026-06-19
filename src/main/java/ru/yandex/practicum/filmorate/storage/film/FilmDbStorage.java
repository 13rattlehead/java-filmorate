package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {

        String sql = """
                INSERT INTO films(
                    name,
                    description,
                    release_date,
                    duration,
                    mpa_id
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());

            if (film.getMpa() != null) {
                statement.setInt(5, film.getMpa().getId());
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            return statement;

        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        saveGenres(film);

        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Collection<Film> getFilms() {

        String sql = """
                SELECT f.*,
                       m.id as mpa_id,
                       m.name as mpa_name
                FROM films f
                LEFT JOIN mpa_rating m ON f.mpa_id = m.id
                ORDER BY f.id
                """;

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {

            Film film = new Film();

            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(
                    rs.getDate("release_date").toLocalDate()
            );
            film.setDuration(rs.getInt("duration"));

            film.setMpa(
                    new Mpa(
                            rs.getInt("mpa_id"),
                            rs.getString("mpa_name")
                    )
            );

            return film;
        });

        films.forEach(this::loadGenres);

        return films;
    }

    @Override
    public Film updateFilm(Film film) {

        String sql = """
                UPDATE films
                SET name = ?,
                    description = ?,
                    release_date = ?,
                    duration = ?,
                    mpa_id = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id = ?",
                film.getId()
        );

        saveGenres(film);

        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Optional<Film> findById(Long id) {

        String sql = """
                SELECT f.*,
                       m.id as mpa_id,
                       m.name as mpa_name
                FROM films f
                LEFT JOIN mpa_rating m ON f.mpa_id = m.id
                WHERE f.id = ?
                """;

        List<Film> films = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Film film = new Film();

                    film.setId(rs.getLong("id"));
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(
                            rs.getDate("release_date").toLocalDate()
                    );
                    film.setDuration(rs.getInt("duration"));

                    film.setMpa(
                            new Mpa(
                                    rs.getInt("mpa_id"),
                                    rs.getString("mpa_name")
                            )
                    );

                    return film;
                },
                id
        );

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);

        loadGenres(film);

        return Optional.of(film);
    }

    private void saveGenres(Film film) {

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Integer> uniqueGenres = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        String sql = """
                INSERT INTO film_genres(film_id, genre_id)
                VALUES (?, ?)
                """;

        uniqueGenres.forEach(genreId ->
                jdbcTemplate.update(
                        sql,
                        film.getId(),
                        genreId
                ));
    }

    private void loadGenres(Film film) {

        String sql = """
                SELECT g.id,
                       g.name
                FROM genres g
                JOIN film_genres fg
                ON g.id = fg.genre_id
                WHERE fg.film_id = ?
                ORDER BY g.id
                """;

        Set<Genre> genres = new LinkedHashSet<>(
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new Genre(
                                        rs.getInt("id"),
                                        rs.getString("name")
                                ),
                        film.getId()
                )
        );

        film.setGenres(genres);
    }

    @Override
    public void addLike(Long filmId, Long userId) {

        String sql = """
            INSERT INTO likes(film_id, user_id)
            VALUES (?, ?)
            """;

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {

        String sql = """
            DELETE FROM likes
            WHERE film_id = ?
            AND user_id = ?
            """;

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {

        String sql = """
            SELECT f.id
            FROM films f
            LEFT JOIN likes l
                ON f.id = l.film_id
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """;

        List<Long> ids = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("id"),
                count
        );

        return ids.stream()
                .map(id -> findById(id).orElseThrow())
                .toList();
    }
}