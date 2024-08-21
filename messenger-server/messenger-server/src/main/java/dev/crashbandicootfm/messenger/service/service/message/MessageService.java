package dev.crashbandicootfm.messenger.service.service.message;

import dev.crashbandicootfm.messenger.service.model.MessageModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

  @NotNull Page<MessageModel> findByChatId(@NotNull Long chatId, @NotNull Pageable pageable);

  @NotNull MessageModel save(@NotNull MessageModel messageModel);
}
