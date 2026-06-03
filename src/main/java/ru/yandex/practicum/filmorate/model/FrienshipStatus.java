package ru.yandex.practicum.filmorate.model;

public enum FrienshipStatus {
    CONFIRMED, UNCONFIRMED;

    public String toString() {
        return this.name().toLowerCase();
    }

}

