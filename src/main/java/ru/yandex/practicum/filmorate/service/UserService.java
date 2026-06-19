package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(
            @Qualifier("userDbStorage")
            UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        log.debug("Создание пользователя {}", user.getEmail());

        User createdUser = userStorage.createUser(user);

        log.info("Пользователь с id = {} успешно создан", createdUser.getId());
        return createdUser;
    }

    public User updateUser(User user) {

        userStorage.findById(user.getId())
                .orElseThrow(() -> {
                    log.warn("Попытка обновить несуществующего пользователя {}", user.getId());
                    return new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
                });

        return userStorage.updateUser(user);
    }

    public User getUser(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с ID " + id + " не найден"
                ));
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        validateUser(userId, friendId);

        userStorage.addFriend(userId, friendId);

        log.info("Пользователь {} добавил в друзья пользователя {}",
                userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        validateUser(userId, friendId);

        userStorage.removeFriend(userId, friendId);

        log.info("Пользователь {} удалил из друзей пользователя {}",
                userId, friendId);
    }

    public List<User> getFriends(Long userId) {

        userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Пользователь с ID " + userId + " не найден"
                        ));

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        validateUser(userId, friendId);

        return userStorage.getCommonFriends(userId, friendId);
    }

    private void validateUser(Long userId, Long friendId) {

        userStorage.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", userId);
                    return new NotFoundException(
                            "Пользователь с ID " + userId + " не найден"
                    );
                });

        userStorage.findById(friendId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", friendId);
                    return new NotFoundException(
                            "Пользователь с ID " + friendId + " не найден"
                    );
                });
    }
}