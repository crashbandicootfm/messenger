package dev.crashbandicootfm.messenger.server.exception.user;

import org.jetbrains.annotations.NotNull;

public class UserAlreadyRegisteredException extends UserException {
    public UserAlreadyRegisteredException(@NotNull String message, Object @NotNull ... args) {
        super(message, args);
    }
}
