package dev.crashbandicootfm.messenger.service.api.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateChatRequest {

  @NotNull String name;
}
