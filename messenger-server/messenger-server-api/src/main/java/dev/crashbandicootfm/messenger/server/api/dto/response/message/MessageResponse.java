package dev.crashbandicootfm.messenger.server.api.dto.response.message;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {

    long id;

    @NotNull String message;
}
