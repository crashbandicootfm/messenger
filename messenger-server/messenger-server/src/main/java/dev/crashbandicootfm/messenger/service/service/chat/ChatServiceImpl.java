package dev.crashbandicootfm.messenger.service.service.chat;

import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.repository.ChatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatServiceImpl implements ChatService {

  @NotNull ChatRepository chatRepository;

  @Override
  public @NotNull ChatModel getById(Long id) throws ChatException {
    return chatRepository.findById(id).orElseThrow(() -> new ChatException("Chat not found!"));
  }

  @Override
  public @NotNull ChatModel save(@NotNull ChatModel chatModel) {
    return chatRepository.save(chatModel);
  }
}
