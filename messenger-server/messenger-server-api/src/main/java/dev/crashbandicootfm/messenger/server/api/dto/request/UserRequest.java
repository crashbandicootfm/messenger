package dev.crashbandicootfm.messenger.server.api.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    @NotNull
    String username;

    @NotNull
    String password;
}
