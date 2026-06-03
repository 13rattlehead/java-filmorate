package ru.yandex.practicum.filmorate.model;

public enum Genre {
    COMEDY, DRAMA, ACTION, THRILLER, DOCUMENTARY, CARTOON, FANTASY;

    public String toString() {
        return this.name().toLowerCase();
    }
}
