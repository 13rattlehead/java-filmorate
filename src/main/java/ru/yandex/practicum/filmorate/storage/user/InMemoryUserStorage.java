package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Component
public class InMemoryUserStorage implements UserStorage {

    private Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {

        validateLogin(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Добавлен пользователь: {}", user);

        return user;
    }

    @Override
    public User updateUser(User user) {

        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Пользователь с id: " + user.getId() + " не найден");
            throw new NotFoundException("Пользователь не найден");
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

    @Override
    public Optional<User> findById(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }
        log.warn("Пользователь с id: " + id + " не найден");
        throw new NotFoundException("Пользователь не найден");
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
