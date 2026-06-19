package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testFindUserById() {

        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test");
        user.setBirthday(java.time.LocalDate.of(2000, 1, 1));

        User created = userStorage.createUser(user);

        Optional<User> userOptional = userStorage.findById(created.getId());

        assertThat(userOptional).isPresent();
        assertThat(userOptional.get().getId()).isEqualTo(created.getId());
    }

    @Test
    void testCreateAndFindUser() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.createUser(user);

        Optional<User> found = userStorage.findById(created.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void testGetUsers() {
        User user1 = new User();
        user1.setEmail("a@a.com");
        user1.setLogin("a");
        user1.setName("A");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("b@b.com");
        user2.setLogin("b");
        user2.setName("B");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        Collection<User> users = userStorage.getUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("old@mail.com");
        user.setLogin("old");
        user.setName("old");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.createUser(user);

        created.setName("new");

        User updated = userStorage.updateUser(created);

        assertThat(updated.getName()).isEqualTo("new");

        Optional<User> fromDb = userStorage.findById(created.getId());
        assertThat(fromDb.get().getName()).isEqualTo("new");
    }


}