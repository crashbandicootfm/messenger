package dev.crashbandicootfm.messenger.service.cqrs.handler;

import dev.crashbandicootfm.mediator.model.CommandHandler;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateMessageCommand;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.service.message.MessageService;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;


import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateMessageCommandHandler implements CommandHandler<CreateMessageCommand, MessageModel> {

  @NotNull MessageService messageService;

  @NotNull UserService userService;

  @Override
  public @NotNull MessageModel handle(@NotNull CreateMessageCommand command) {
    if (!userService.existsById(command.getUserId())) {
      throw new UserException("User %s not found!".formatted(command.getUserId()));
    }
    MessageModel model = MessageModel.builder()
        .createdBy(command.getUserId())
        .sentAt(new Date())
        .chatId(command.getChatId())
        .message(command.getMessage())
        .build();
    return messageService.save(model);
  }
}
