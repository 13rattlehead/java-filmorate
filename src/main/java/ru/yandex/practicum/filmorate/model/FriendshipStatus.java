package ru.yandex.practicum.filmorate.model;

public enum FriendshipStatus {
    CONFIRMED, UNCONFIRMED;

    public String toString() {
        return this.name().toLowerCase();
    }

}

