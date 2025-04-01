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

    @NotNull String username;

    boolean isRead;

    String fileUrl;

    boolean isImage;

    public MessageResponse(@NotNull Long id, @NotNull String message, Date sentAt, @NotNull Long createdBy, @NotNull Long chatId, @NotNull String username, boolean isRead, String fileUrl) {
        this.id = id;
        this.message = message;
        this.sentAt = sentAt;
        this.createdBy = createdBy;
        this.chatId = chatId;
        this.username = username;
        this.isRead = isRead;
        this.fileUrl = fileUrl;
    }

    public MessageResponse(@NotNull Long id, @NotNull String message, Date sentAt, @NotNull Long createdBy, @NotNull Long chatId, @NotNull String username, boolean isRead) {
        this.id = id;
        this.message = message;
        this.sentAt = sentAt;
        this.createdBy = createdBy;
        this.chatId = chatId;
        this.username = username;
        this.isRead = isRead;
    }

    public MessageResponse(@NotNull Long id, @NotNull String message, Date sentAt, @NotNull Long createdBy, @NotNull Long chatId, @NotNull String username, boolean isRead, String fileUrl, boolean isImage) {
        this.id = id;
        this.message = message;
        this.sentAt = sentAt;
        this.createdBy = createdBy;
        this.chatId = chatId;
        this.username = username;
        this.isRead = isRead;
        this.fileUrl = fileUrl;
        this.isImage = isImage;
    }
}
