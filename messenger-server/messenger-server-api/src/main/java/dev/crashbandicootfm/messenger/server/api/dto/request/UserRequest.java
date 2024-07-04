package dev.crashbandicootfm.messenger.server.api.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    @NotNull String username;

    @NotNull String password;

}
