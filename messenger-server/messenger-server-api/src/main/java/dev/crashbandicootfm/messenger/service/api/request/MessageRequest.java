package dev.crashbandicootfm.messenger.service.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    boolean isEncrypted;

    @JsonProperty(value = "isEncrypted")
        public void setIsEncrypted(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    int recipientId;
}
