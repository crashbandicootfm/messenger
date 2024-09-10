package dev.crashbandicootfm.messenger.service.exception.user;

import org.jetbrains.annotations.NotNull;

public class UserAlreadyRegisteredException extends UserException {

    public UserAlreadyRegisteredException(@NotNull String message, @NotNull Object... args) {
        super(message);
    }
}
