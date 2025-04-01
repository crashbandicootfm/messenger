package dev.crashbandicootfm.messenger.service.service.chat;

import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ChatService {

  @NotNull ChatModel getById(Long id) throws ChatException;

  @NotNull ChatModel save(@NotNull ChatModel chatModel);

  @NotNull ChatModel createChat(@NotNull String chatName, @NotNull Long userId) throws ChatException, UserException;

  @NotNull ChatModel findByChatName(@NotNull String chatName) throws ChatException;

  @NotNull ChatModel joinChat(@NotNull String chatName, @NotNull Long userId) throws ChatException, UserException;

  @NotNull ChatModel createChatWithPassword(@NotNull String chatName, @NotNull String password) throws ChatException;

  @NotNull ChatModel joinChatWithPassword(@NotNull String chatName, @NotNull Long userId, @NotNull String password) throws ChatException, UserException;

  @NotNull List<ChatModel> findAllByUserId(@NotNull Long userId) throws UserException;

  @NotNull ChatModel startPrivateChat(@NotNull Long userId, @NotNull Long otherUserId) throws UserException, ChatException;

  void deleteChat(@NotNull Long chatId, @NotNull Long userId) throws ChatException;

  void leaveChat(@NotNull Long chatId, @NotNull Long userId) throws ChatException;

  @NotNull List<MessageModel> getMessagesByChatId(Long chatId);

  void markChatAsRead(Long chatId, Long userId, Long lastMessageId);

  Long getUnreadMessagesCount(Long chatId, Long userId);

  @NotNull List<ChatModel> getAllChatsByUserId(Long userId);
}
