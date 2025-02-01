package dev.crashbandicootfm.messenger.service.service.message;

import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.repository.MessageRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageServiceImpl implements MessageService {

  @NotNull MessageRepository messageRepository;

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
}
