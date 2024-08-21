package dev.crashbandicootfm.messenger.service.service.chat;

import dev.crashbandicootfm.messenger.service.model.ChatModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

  @NotNull Page<ChatModel> getByUserId(@NotNull Long userId, @NotNull Pageable pageable);

  @NotNull ChatModel save(@NotNull ChatModel chatModel);
}
