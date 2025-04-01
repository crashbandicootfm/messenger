package dev.crashbandicootfm.messenger.service.api.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {

    @NotNull String message;

    @NotNull Long chatId;

    String fileUrl;


    public MessageRequest(@NotNull String message, @NotNull Long chatId, String fileUrl) {
        this.message = message;
        this.chatId = chatId;
        this.fileUrl = fileUrl;
    }
}
