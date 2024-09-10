package dev.crashbandicootfm.messenger.service.service.security.token;

import org.jetbrains.annotations.NotNull;

public interface TokenFactory {

    @NotNull String generateToken(@NotNull String username);
}
