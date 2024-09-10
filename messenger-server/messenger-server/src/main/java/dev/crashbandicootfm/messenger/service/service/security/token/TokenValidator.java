package dev.crashbandicootfm.messenger.service.service.security.token;

import org.jetbrains.annotations.NotNull;

public interface TokenValidator {

    boolean isValid(@NotNull String token);
}
