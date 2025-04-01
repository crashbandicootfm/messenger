package dev.crashbandicootfm.messenger.service.api.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenResponse {

    @NotNull String token;

    @NotNull String refreshToken;

    boolean twoFactorRequired;

    Long id;

    public TokenResponse(@NotNull String token, @NotNull String refreshToken, boolean twoFactorRequired) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.twoFactorRequired = twoFactorRequired;
    }

    public TokenResponse(@NotNull String token, @NotNull String refreshToken, boolean twoFactorRequired, Long id) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.twoFactorRequired = twoFactorRequired;
        this.id = id;
    }
}
