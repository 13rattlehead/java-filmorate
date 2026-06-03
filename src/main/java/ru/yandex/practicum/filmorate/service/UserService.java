package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FrienshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        validateUser(userId, friendId);
        User user = userStorage.findById(userId).get();
        User friend = userStorage.findById(friendId).get();
        user.getFriends().put(friendId, FrienshipStatus.CONFIRMED);
        log.info("Пользователь c ID " + userId + " добавлен в друзья пользователя c ID " + friendId);
        friend.getFriends().put(userId, FrienshipStatus.CONFIRMED);
        log.info("Пользователь c ID " + friendId + " добавлен в друзья пользователя c ID " + userId);

    }

    public void deleteFriend(Long userId, Long friendId) {
        validateUser(userId, friendId);
        User user = userStorage.findById(userId).get();
        User friend = userStorage.findById(friendId).get();
        user.getFriends().remove(friendId);
        log.info("Пользователь c ID " + userId + " удален из друзей пользователя c ID " + friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь c ID " + friendId + " удален из друзей пользователя c ID " + userId);
    }

    public List<User> getFriends(Long userId) {

        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        User user = userStorage.findById(userId).get();
        return user.getFriends().keySet().stream()
                .map(id -> userStorage.findById(id).get())
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        validateUser(userId, friendId);
        User user = userStorage.findById(userId).get();
        User friend = userStorage.findById(friendId).get();
        return user.getFriends().keySet().stream()
                .filter(friend.getFriends().keySet()::contains)
                .map(id -> userStorage.findById(id).get())
                .collect(Collectors.toList());
    }

    private void validateUser(Long userId, Long friendId) {
        userStorage.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь c ID " + userId + " не найден");
            return new NotFoundException("Пользователь с ID " + userId + " не найден");
        });

        userStorage.findById(friendId).orElseThrow(() -> {
            log.warn("Пользователь c ID " + friendId + " не найден");
            throw new NotFoundException("Пользователь с ID " + friendId + " не найден");
        });
    }
}
