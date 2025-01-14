package dev.crashbandicootfm.messenger.service.service.chat;

import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.repository.ChatRepository;
import dev.crashbandicootfm.messenger.service.repository.UserRepository;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
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

  @NotNull UserService userService;

  @NotNull UserRepository userRepository;

  @Override
  public @NotNull ChatModel getById(Long id) throws ChatException {
    return chatRepository.findById(id).orElseThrow(() -> new ChatException("Chat not found!"));
  }

  @Override
  public @NotNull ChatModel save(@NotNull ChatModel chatModel) {
    return chatRepository.save(chatModel);
  }

  @Override
  public @NotNull ChatModel findByChatName(@NotNull String chatName) throws ChatException {
    return chatRepository.findByName(chatName).orElseThrow(() -> new ChatException("Chat not found!"));
  }

  @Override
  public @NotNull ChatModel joinChat(@NotNull String chatName, @NotNull Long userId) throws ChatException, UserException {
    ChatModel chat = chatRepository.findByName(chatName)
        .orElseThrow(() -> new ChatException("Chat not found!"));

    UserModel user = userService.getById(userId);
    if (user == null) {
      throw new UserException("User not found!");
    }

    if (chat.getUserIds().contains(userId)) {
      throw new ChatException("User is already in the chat!");
    }

    chat.getUserIds().add(userId);

    user.getChatIds().add(chat.getId());

    chatRepository.save(chat);
    userRepository.save(user);

    return chat;
  }
}
