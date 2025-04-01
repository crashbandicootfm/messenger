package dev.crashbandicootfm.messenger.service.api.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {

    @NotNull String username;

    @NotNull String password;

    Integer twoFactorCode;
}
