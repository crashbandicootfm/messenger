package dev.crashbandicootfm.messenger.server.exception.user;

import org.jetbrains.annotations.NotNull;

public class UserException extends RuntimeException {

    public UserException(@NotNull String message) {
        super(message);
    }

    public UserException(@NotNull String message, Object @NotNull ... args) {
        super();
    }
}
