package dev.crashbandicootfm.messenger.service.cqrs.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class CreateMessageCommand {

  @NotNull String message;

  @NotNull Long chatId;

  @NotNull Long userId;
}
