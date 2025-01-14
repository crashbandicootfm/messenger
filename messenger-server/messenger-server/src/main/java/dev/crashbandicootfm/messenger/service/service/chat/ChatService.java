package dev.crashbandicootfm.messenger.service.service.chat;

import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import org.jetbrains.annotations.NotNull;

public interface ChatService {

  @NotNull ChatModel getById(Long id) throws ChatException;

  @NotNull ChatModel save(@NotNull ChatModel chatModel);

  @NotNull ChatModel findByChatName(@NotNull String chatName) throws ChatException;

  @NotNull ChatModel joinChat(@NotNull String chatName, @NotNull Long userId) throws ChatException, UserException;
}
