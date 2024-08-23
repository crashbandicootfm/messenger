package dev.crashbandicootfm.messenger.service.api.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponse {

  @NotNull Long id;

  @NotNull String name;

  @NotNull Date createdAt;

  @NotNull Long createdBy;

  @NotNull List<Long> userIds;

}
