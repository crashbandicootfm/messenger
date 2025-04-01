package dev.crashbandicootfm.messenger.service.cqrs.command;

import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class CreateFileMessageCommand {

  @NotNull Long chatId;
  @NotNull Long userId;
  @NotNull String fileUrl;
  @NotNull Date sentAt;
  boolean isImage; // Новое поле

}
