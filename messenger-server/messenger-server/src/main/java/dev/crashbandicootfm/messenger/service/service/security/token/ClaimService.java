package dev.crashbandicootfm.messenger.service.service.security.token;
import io.jsonwebtoken.Claims;

import jakarta.validation.constraints.NotNull;

public interface ClaimService {

    @NotNull String extractUsername(@NotNull String token);

    @NotNull Claims extractClaims(@NotNull String token);
}
