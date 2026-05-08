package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    void shouldCreateUser() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("test@test.com", createdUser.getEmail());
        assertEquals("testLogin", createdUser.getLogin());
        assertEquals("Test User", createdUser.getName());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("login");
        user.setName(" ");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.createUser(user);

        assertEquals("login", createdUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {

        User user = new User();
        user.setEmail("invalidEmail");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(
                ConditionsNotMetException.class,
                () -> userController.createUser(user)
        );
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("invalid login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(
                ConditionsNotMetException.class,
                () -> userController.createUser(user)
        );
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(
                ConditionsNotMetException.class,
                () -> userController.createUser(user)
        );
    }
}