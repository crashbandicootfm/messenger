package dev.crashbandicootfm.messenger.service.cqrs.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class CreateChatWithPasswordCommand {

  @NotNull Long userId;

  @NotNull String chatName;

  @NotNull String password;
}
