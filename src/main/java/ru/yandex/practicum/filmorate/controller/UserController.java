package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

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
    public User createUser(@Valid @RequestBody User user) {

        validateLogin(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

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

        validateLogin(user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пользователя пустое. Используется login: {}", user.getLogin());

            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);

        log.info("Пользователь обновлен: {}", user);

        return user;
    }

    public void validateLogin(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин не может содержать пробелы: {}", user.getLogin());
            throw new ConditionsNotMetException("Логин не может содержать пробелы");
        }
    }

    private long getNextId() {
        return users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }

}
