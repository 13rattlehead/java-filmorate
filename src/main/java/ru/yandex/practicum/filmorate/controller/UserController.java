package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;


@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStorage userStorage;

    @GetMapping
    public Collection<User> getUsers() {
        log.debug("Запрос на получение всех пользователей");
        Collection<User> allUsers = userStorage.getUsers();
        log.info("Получено {} пользователей", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.debug("Запрос на получение пользователя с id={}", id);
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Получен пользователь с id = {}", user.getId());
        return user;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос на создание пользователя");
        User newUser = userStorage.createUser(user);
        log.info("Создан новый пользователь с id = {}", newUser.getId());
        return newUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Получен запрос на обновление пользователя");
        User updatedUser =  userService.updateUser(user);
        log.info("Пользователь с id = {} был обновлен", updatedUser.getId());
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("Пользователь {} отправил запрос на добавление в друзья пользователя {}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("Получен запрос от {} на удаление из друзей пользователя {}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Пользователь {} удалил из друзей пользователя {}", id, friendId);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("Получен запрос на получение общих друзей пользователей {} и {}", id, friendId);
        Collection<User> commonFriends = userService.getCommonFriends(id, friendId);
        log.info("Получено {} общих друзей", commonFriends.size());
        return commonFriends;
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        log.debug("Получен запрос на получение списка друзей пользователя {}", id);
        Collection<User> friends = userService.getFriends(id);
        log.info("Получено {} друзей пользователя {}", friends.size(), id);
        return friends;
    }
}
