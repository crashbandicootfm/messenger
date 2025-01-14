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

  public ChatResponse(Long id, String name, Date createdAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
  }
}
