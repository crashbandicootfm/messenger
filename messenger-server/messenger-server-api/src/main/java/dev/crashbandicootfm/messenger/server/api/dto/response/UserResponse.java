package dev.crashbandicootfm.messenger.server.api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    long id;

    @NotNull String username;

    @NotNull String password;
}
