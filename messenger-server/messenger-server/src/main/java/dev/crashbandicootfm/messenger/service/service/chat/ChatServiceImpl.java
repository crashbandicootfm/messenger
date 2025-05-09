package dev.crashbandicootfm.messenger.service.service.chat;

import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.model.ChatUserStatusModel;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.repository.ChatRepository;
import dev.crashbandicootfm.messenger.service.repository.ChatUserStatusRepository;
import dev.crashbandicootfm.messenger.service.repository.MessageRepository;
import dev.crashbandicootfm.messenger.service.repository.UserRepository;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatServiceImpl implements ChatService {

  @NotNull ChatRepository chatRepository;

  @NotNull UserService userService;

  @NotNull UserRepository userRepository;

  @NotNull MessageRepository messageRepository;

  @NotNull ChatUserStatusRepository chatUserStatusRepository;

  @Override
  public @NotNull ChatModel getById(Long id) throws ChatException {
    return chatRepository.findById(id).orElseThrow(() -> new ChatException("Chat not found!"));
  }

  @Override
  public @NotNull ChatModel save(@NotNull ChatModel chatModel) {
    ChatModel savedChat = chatRepository.save(chatModel);

    UserModel creator = userService.getById(savedChat.getCreatedBy());
    creator.getChatIds().add(savedChat.getId());
    userRepository.save(creator);

    return savedChat;
  }

  @Override
  public @NotNull ChatModel createChat(@NotNull String chatName, @NotNull Long userId) throws ChatException, UserException {
    if (chatRepository.findByName(chatName).isPresent()) {
      throw new ChatException("Chat already exists!");
    }

    UserModel user = userService.getById(userId);
    if (user == null) {
      throw new UserException("User not found!");
    }

    ChatModel chat = new ChatModel();
    chat.setName(chatName);
    chat.setCreatedBy(userId);
    chat.getUserIds().add(userId);
    chat.setCreatedAt(new Date());

    ChatModel savedChat = chatRepository.save(chat);
    savedChat.getUserIds().add(userId);

    user.getChatIds().add(savedChat.getId());
    userRepository.save(user);

    return savedChat;
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


    try {
      chat.getUserIds().add(userId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      user.getChatIds().add(chat.getId());


    chatRepository.save(chat);
    userRepository.save(user);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return chat;
  }

  @Override
  public @NotNull ChatModel createChatWithPassword(@NotNull String chatName, @NotNull String password) throws ChatException {
    if (chatRepository.findByName(chatName).isPresent()) {
      throw new ChatException("Chat already exists!");
    }
    ChatModel chatModel = new ChatModel();
    chatModel.setName(chatName);
    chatModel.setPassword(password);

    return chatRepository.save(chatModel);
  }

  @Override
  public @NotNull ChatModel joinChatWithPassword(@NotNull String chatName, @NotNull Long userId, @NotNull String password)
      throws ChatException, UserException {
    ChatModel chat = chatRepository.findByName(chatName)
        .orElseThrow(() -> new ChatException("Chat not found!"));

    if (!chat.getPassword().equals(password)) {
      throw new ChatException("Incorrect password!");
    }

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

  @Override
  public @NotNull List<ChatModel> findAllByUserId(@NotNull Long userId) throws UserException {
    UserModel user = userService.getById(userId);
    if (user == null) {
      throw new UserException("User not found!");
    }

    List<ChatModel> chats = chatRepository.findAllByIdIn(user.getChatIds());

    List<ChatModel> filteredChats = chats.stream()
        .filter(chat -> !"private_chat".equals(chat.getName()))
        .toList();

    return filteredChats;
  }

  @Override
  public @NotNull ChatModel startPrivateChat(@NotNull Long userId, @NotNull Long otherUserId) throws UserException, ChatException {
    if (userId.equals(otherUserId)) {
      throw new ChatException("Error creating chat");
    }

    UserModel user = userService.getById(userId);
    UserModel otherUser = userService.getById(otherUserId);

    if (user == null || otherUser == null) {
      throw new UserException("User not found");
    }

    ChatModel privateChat = new ChatModel();
    privateChat.setName("private_chat");
    privateChat.setUserIds(List.of(userId, otherUserId));
    privateChat.setCreatedAt(new Date());
    privateChat.setCreatedBy(userId);

    ChatModel savedChat = chatRepository.save(privateChat);

    user.getChatIds().add(savedChat.getId());
    otherUser.getChatIds().add(savedChat.getId());

    userRepository.save(user);
    userRepository.save(otherUser);

    return savedChat;
  }

  @Override
  public void deleteChat(@NotNull Long chatId, Long userId) throws ChatException {
    ChatModel chat = chatRepository.findById(chatId)
        .orElseThrow(() -> new ChatException("Chat not found!"));

    if (!chat.getCreatedBy().equals(userId)) {
      throw new ChatException("You do not have permission to delete this chat!");
    }

    List<Long> userIds = chat.getUserIds();
    for (Long id : userIds) {
      UserModel user = userService.getById(id);
      user.getChatIds().remove(chatId);
      userRepository.save(user);
    }

    chatRepository.deleteById(chatId);
  }

  @Override
  public void leaveChat(@NotNull Long chatId, @NotNull Long userId) throws ChatException {
    ChatModel chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatException("Chat not found!"));

    chat.getUserIds().remove(userId);

    if (chat.getUserIds().isEmpty()) {
      chatRepository.delete(chat);
    } else {
      chatRepository.save(chat);
    }
  }

  @Override
  public @NotNull List<MessageModel> getMessagesByChatId(Long chatId) {
    return messageRepository.findAllByChatId(chatId);
  }

  @Override
  public void markChatAsRead(Long chatId, Long userId, Long lastMessageId) {
    ChatUserStatusModel status = chatUserStatusRepository.findByChatIdAndUserId(chatId, userId)
        .orElseGet(() -> {
          ChatUserStatusModel newStatus = new ChatUserStatusModel();
          newStatus.setChatId(chatId);
          newStatus.setUserId(userId);
          return newStatus;
        });

    status.setLastReadMessageId(lastMessageId);
    chatUserStatusRepository.save(status);
  }

  @Override
  public Long getUnreadMessagesCount(Long chatId, Long userId) {
    return messageRepository.countUnreadMessages(chatId, userId);
  }

  @Override
  public @NotNull List<ChatModel> getAllChatsByUserId(Long userId) {
    return chatRepository.findUserChats(userId);
  }

  @Override
  public @NotNull List<ChatModel> findTwoUserChats(@NotNull Long userId) throws UserException {
    UserModel user = userService.getById(userId);
    if (user == null) {
      throw new UserException("User not found!");
    }

    List<ChatModel> userChats = chatRepository.findAllByIdIn(user.getChatIds());

    List<ChatModel> twoUserChats = new ArrayList<>();
    for (ChatModel chat : userChats) {
      if (chat.getUserIds().size() == 2) {
        twoUserChats.add(chat);
      }
    }

    for (ChatModel chat : twoUserChats) {
      if ("private_chat".equals(chat.getName())) {
        Long otherUserId = chat.getUserIds().stream()
            .filter(id -> !id.equals(userId))
            .findFirst()
            .orElse(null);

        if (otherUserId != null) {
          UserModel otherUser = userService.getById(otherUserId);
          chat.setName(otherUser != null ? otherUser.getUsername() : "A");
        }
      }
    }

    return twoUserChats;
  }

  @Override
  public List<Long> getChatParticipantIds(Long chatId) throws ChatException {
    ChatModel chat = getById(chatId);
    return new ArrayList<>(chat.getUserIds());
  }

  @Override
  public List<UserModel> getChatParticipants(Long chatId) throws ChatException {
    List<Long> participantIds = getChatParticipantIds(chatId);
    return (List<UserModel>) userRepository.findAllById(participantIds);
  }

  @Override
  public int getParticipantCountByChatName(@NotNull String chatName) throws ChatException {
    Optional<ChatModel> optionalChat = chatRepository.findByName(chatName);

    if (optionalChat.isPresent()) {
      return optionalChat.get().getUserIds().size();
    }

    // If not found, check if it might be a private chat with a display name
    // Find all private chats where the user is a participant
    List<ChatModel> privateChats = chatRepository.findAllByName("private_chat");

    for (ChatModel chat : privateChats) {
      // For private chats, we need to check if the display name matches
      if (chat.getUserIds().size() == 2) {
        try {
          // Get the other participant's username
          Long otherUserId = chat.getUserIds().stream()
              .filter(id -> !id.equals(chat.getCreatedBy()))
              .findFirst()
              .orElse(null);

          if (otherUserId != null) {
            UserModel otherUser = userService.getById(otherUserId);
            if (otherUser != null && chatName.equals(otherUser.getUsername())) {
              return 2;
            }
          }
        } catch (UserException e) {
          log.warn("Error getting user info for private chat participant", e);
          continue;
        }
      }
    }

    throw new ChatException("Chat not found!");
  }
}
