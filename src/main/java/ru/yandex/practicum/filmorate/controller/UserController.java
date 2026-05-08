package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {

        validateUser(user);

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Добавлен пользователь: {}", user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {

        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Пользователь с id: " + user.getId() + " не найден");
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        validateUser(user);
        users.put(user.getId(), user);

        log.info("Пользователь обновлен: {}", user);

        return user;
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }

    public void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() ||
                !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: некорректный email {} ", user.getEmail());
            throw new ConditionsNotMetException("Email is invalid");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: некорректный логин {} ", user.getLogin());
            throw new ConditionsNotMetException("Login is invalid");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя не указано, будет использоваться логин {} ", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: некорректная дата рождения {} ", user.getBirthday());
            throw new ConditionsNotMetException("Birthday is invalid");
        }
    }
}
