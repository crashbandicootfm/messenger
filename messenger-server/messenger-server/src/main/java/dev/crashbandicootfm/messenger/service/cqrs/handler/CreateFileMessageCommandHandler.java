package dev.crashbandicootfm.messenger.service.cqrs.handler;

import dev.crashbandicootfm.mediator.model.CommandHandler;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateFileMessageCommand;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.service.message.MessageService;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateFileMessageCommandHandler implements CommandHandler<CreateFileMessageCommand, MessageModel> {

  @NotNull MessageService messageService;

  @NotNull UserService userService;

  @Override
  public @NotNull MessageModel handle(@NotNull CreateFileMessageCommand command) {
    if (!userService.existsById(command.getUserId())) {
      throw new UserException("User %s not found!".formatted(command.getUserId()));
    }

    // Формируем текст сообщения в зависимости от типа файла
    String messageText;
    if (command.isImage()) {
      // Если это изображение, используем тег <img>
      messageText = "<img src=\"" + command.getFileUrl() + "\" alt=\"Image\" style=\"max-width: 100%; height: auto;\">";
    } else {
      // Если это не изображение, используем ссылку
      messageText = "📎 Файл: <a href=\"" + command.getFileUrl() + "\" target=\"_blank\">Скачать</a>";
    }

    MessageModel message = MessageModel.builder()
        .chatId(command.getChatId())
        .createdBy(command.getUserId())
        .sentAt(command.getSentAt())
        .message(messageText) // Устанавливаем текст сообщения
        .fileUrl(command.getFileUrl()) // Устанавливаем URL файла
        .readByUsers(new ArrayList<>()) // Изначально сообщение никто не прочитал
        .build();

    return messageService.save(message);
  }
}
