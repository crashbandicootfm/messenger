package dev.crashbandicootfm.messenger.service.api.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {

    @NotNull Long id;

    @NotNull String message;

    @NotNull Date sentAt;

    @NotNull Long createdBy;

    @NotNull Long chatId;
}
