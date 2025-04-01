package dev.crashbandicootfm.messenger.service.api.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponse {

  @NotNull Long id;

  @NotNull String name;

  @NotNull Date createdAt;

  Long createdBy;

  List<Long> userIds;

  String lastMessage;

  String lastMessageSender;

  Long unreadCount;

  public ChatResponse(Long id, String name, Date createdAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
  }

  public ChatResponse(Long id, String name, Date createdAt, String lastMessage, String lastMessageSender, Long unreadCount) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
    this.lastMessage = lastMessage;
    this.lastMessageSender = lastMessageSender;
    this.unreadCount = unreadCount;
  }

}
