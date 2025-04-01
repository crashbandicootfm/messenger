package dev.crashbandicootfm.messenger.service.service.message;

import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  @NotNull Page<MessageModel> findByChatId(@NotNull Long chatId, @NotNull Pageable pageable);

  @NotNull MessageModel save(@NotNull MessageModel messageModel);

  @NotNull List<MessageModel> getMessagesByChatId(@NotNull Long chatId);

  void deleteMessageById(@NotNull Long id);

  @NotNull UserModel getUserById(@NotNull Long userId);

  @NotNull String findLastMessageByChatId(@NotNull Long chatId);

  @NotNull String findLastMessageSenderByChatId(@NotNull Long chatId);

  void markMessageAsRead(Long messageId, Long userId);

  @NotNull String saveFile(MultipartFile file, Long chatId, Long userId);
}
