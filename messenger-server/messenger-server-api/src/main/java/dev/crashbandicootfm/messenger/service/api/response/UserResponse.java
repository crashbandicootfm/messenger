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
public class UserResponse {

    @NotNull Long id;

    @NotNull String username;

    @NotNull String avatarUrl;

    public UserResponse(@NotNull Long id, @NotNull String username) {
        this.id = id;
        this.username = username;
    }
}
