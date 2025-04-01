package dev.crashbandicootfm.messenger.service.cqrs.handler;

import dev.crashbandicootfm.mediator.model.CommandHandler;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateChatWithPasswordCommand;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.service.chat.ChatService;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import java.util.Date;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class CreateChatWithPasswordCommandHandler implements CommandHandler<CreateChatWithPasswordCommand, ChatModel> {

  @NotNull UserService userService;

  @NotNull ChatService chatService;


  @Override
  public @NotNull ChatModel handle(@NotNull CreateChatWithPasswordCommand command) {
    if (!userService.existsById(command.getUserId())) {
      throw new UserException("User %s not found!".formatted(command.getUserId()));
    }
    ChatModel chatModel = ChatModel.builder()
        .createdAt(new Date())
        .createdBy(command.getUserId())
        .name(command.getChatName())
        .password(command.getPassword())
        .build();

    return chatService.save(chatModel);
  }
}
