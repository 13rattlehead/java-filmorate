package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
		UserDbStorage.class,
		FilmDbStorage.class,
		GenreDbStorage.class,
		MpaDbStorage.class
})
class FilmorateApplicationTests {

	@Autowired
	private UserDbStorage userStorage;

	@Autowired
	private FilmDbStorage filmStorage;

	@Autowired
	private GenreDbStorage genreStorage;

	@Autowired
	private MpaDbStorage mpaStorage;

	@Test
	void shouldCreateUser() {

		User user = createUser();

		User createdUser = userStorage.createUser(user);

		assertThat(createdUser.getId()).isNotNull();
	}

	@Test
	void shouldFindUserById() {

		User createdUser =
				userStorage.createUser(createUser());

		Optional<User> foundUser =
				userStorage.findById(createdUser.getId());

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getId())
				.isEqualTo(createdUser.getId());
	}

	@Test
	void shouldUpdateUser() {

		User createdUser =
				userStorage.createUser(createUser());

		createdUser.setName("Updated Name");

		userStorage.updateUser(createdUser);

		User updatedUser =
				userStorage.findById(createdUser.getId())
						.orElseThrow();

		assertThat(updatedUser.getName())
				.isEqualTo("Updated Name");
	}

	@Test
	void shouldReturnUsers() {

		userStorage.createUser(createUser());

		assertThat(userStorage.getUsers())
				.isNotEmpty();
	}

	@Test
	void shouldCreateFilm() {

		Film film = createFilm();

		Film createdFilm =
				filmStorage.createFilm(film);

		assertThat(createdFilm.getId())
				.isNotNull();
	}

	@Test
	void shouldFindFilmById() {

		Film createdFilm =
				filmStorage.createFilm(createFilm());

		Optional<Film> foundFilm =
				filmStorage.findById(createdFilm.getId());

		assertThat(foundFilm).isPresent();

		assertThat(foundFilm.get().getName())
				.isEqualTo("Film");
	}

	@Test
	void shouldUpdateFilm() {

		Film createdFilm =
				filmStorage.createFilm(createFilm());

		createdFilm.setName("Updated Film");

		filmStorage.updateFilm(createdFilm);

		Film updatedFilm =
				filmStorage.findById(createdFilm.getId())
						.orElseThrow();

		assertThat(updatedFilm.getName())
				.isEqualTo("Updated Film");
	}

	@Test
	void shouldReturnFilms() {

		filmStorage.createFilm(createFilm());

		assertThat(filmStorage.getFilms())
				.isNotEmpty();
	}

	@Test
	void shouldReturnAllGenres() {

		assertThat(genreStorage.findAll())
				.hasSize(6);
	}

	@Test
	void shouldFindGenreById() {

		assertThat(genreStorage.findById(1))
				.isPresent();
	}

	@Test
	void shouldReturnAllMpaRatings() {

		assertThat(mpaStorage.findAll())
				.hasSize(5);
	}

	@Test
	void shouldFindMpaById() {

		assertThat(mpaStorage.findById(1))
				.isPresent();
	}

	private User createUser() {

		User user = new User();

		user.setEmail("test@test.ru");
		user.setLogin("login");
		user.setName("Test User");
		user.setBirthday(LocalDate.of(2000, 1, 1));

		return user;
	}

	private Film createFilm() {

		Film film = new Film();

		film.setName("Film");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(100);

		film.setMpa(
				new Mpa(
						1,
						"G"
				)
		);

		return film;
	}
}