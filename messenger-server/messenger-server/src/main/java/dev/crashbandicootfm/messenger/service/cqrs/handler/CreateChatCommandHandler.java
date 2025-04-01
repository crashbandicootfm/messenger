package dev.crashbandicootfm.messenger.service.cqrs.handler;

import dev.crashbandicootfm.mediator.model.CommandHandler;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateChatCommand;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.service.chat.ChatService;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class CreateChatCommandHandler implements CommandHandler<CreateChatCommand, ChatModel> {

  @NotNull UserService userService;

  @NotNull ChatService chatService;

  @Override
  public @NotNull ChatModel handle(@NotNull CreateChatCommand command) {
    if (!userService.existsById(command.getUserId())) {
      throw new UserException("User %s not found!".formatted(command.getUserId()));
    }
//    ChatModel chatModel = ChatModel.builder()
//        .createdAt(new Date())
//        .createdBy(command.getUserId())
//        .name(command.getChatName())
//        .build();
    return chatService.createChat(command.getChatName(), command.getUserId());
  }
}
