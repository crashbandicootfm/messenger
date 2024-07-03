package dev.crashbandicootfm.messenger.server.api.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDto {

    @NotNull
    String name;

    @NotNull
    String password;
}
