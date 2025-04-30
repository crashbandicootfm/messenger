package dev.crashbandicootfm.messenger.service.service.message;

import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.repository.MessageRepository;
import dev.crashbandicootfm.messenger.service.repository.UserRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageServiceImpl implements MessageService {

  @NotNull MessageRepository messageRepository;

  @NotNull UserRepository userRepository;

  @Override
  public @NotNull Page<MessageModel> findByChatId(@NotNull Long chatId, @NotNull Pageable pageable) {
    return messageRepository.findAllByChatId(chatId, pageable);
  }

  @Override
  public @NotNull MessageModel save(@NotNull MessageModel messageModel) {
    return messageRepository.save(messageModel);
  }

  @Override
  public @NotNull List<MessageModel> getMessagesByChatId(@NotNull Long chatId) {
    return messageRepository.findAllByChatId(chatId);
  }

  @Override
  public void deleteMessageById(@NotNull Long id) {
    System.out.println("Attempting to delete message with ID: " + id);
    messageRepository.deleteById(id);
    System.out.println("Message deleted: " + id);
  }

  @Override
  public @NotNull UserModel getUserById(@NotNull Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
  }

  @Override
  public @NotNull String findLastMessageByChatId(@NotNull Long chatId) {
    return messageRepository.findTopByChatIdOrderBySentAtDesc(chatId)
        .map(MessageModel::getMessage)
        .orElse("No messages");
  }

  @Override
  public @NotNull String findLastMessageSenderByChatId(@NotNull Long chatId) {
    return messageRepository.findTopByChatIdOrderBySentAtDesc(chatId)
        .map(message -> {
          UserModel sender = userRepository.findById(message.getCreatedBy())
              .orElseThrow(() -> new RuntimeException("User not found"));
          return sender.getUsername();
        })
        .orElse("No sender");
  }

  @Override
  public void markMessageAsRead(Long messageId, Long userId) {
    MessageModel message = messageRepository.findById(messageId)
        .orElseThrow(() -> new RuntimeException("Message not found"));

    List<Long> readByUsers = message.getReadByUsers();
    if (!readByUsers.contains(userId)) {
      readByUsers.add(userId);
      message.setReadByUsers(readByUsers);
      messageRepository.save(message);
    }
  }

  @Override
  public @NotNull String saveFile(MultipartFile file, Long chatId, Long userId) {
    try {
      String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
      Path filePath = Path.of("uploads/" + fileName);

      Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

      MessageModel message = MessageModel.builder()
          .chatId(chatId)
          .createdBy(userId)
          .message("File: " + fileName)
          .sentAt(new Date())
          .build();

      messageRepository.save(message);

      return "/uploads/" + fileName;
    } catch (Exception e) {
      throw new RuntimeException("File save failed", e);
    }
  }
}
