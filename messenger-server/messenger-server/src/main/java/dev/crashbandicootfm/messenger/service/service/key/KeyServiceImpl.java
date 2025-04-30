package dev.crashbandicootfm.messenger.service.service.key;

import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.chat.ChatService;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KeyServiceImpl implements KeyService {

  @NotNull ConcurrentHashMap<Long, String> keys = new ConcurrentHashMap<>();

  @NotNull ChatService chatService;

  @NotNull UserService userService;

  @Override
  public void storeClientPublicKey(Long clientId, @NotNull String publicKey) {
    keys.put(clientId, publicKey);
  }

  @Override
  public @NotNull String getClientPublicKey(@NotNull Long clientId) {
    UserModel user = userService.getById(clientId);
    String publicKey = user.getPublicKey();
    if (publicKey == null) {
      log.error("Public key not found for client: {}", clientId);
      throw new ChatException("Public key not found for client");
    }
    return publicKey;
  }

  @Override
  public List<String> getChatParticipantsKeys(Long chatId) {
    try {
      List<Long> participantIds = chatService.getChatParticipantIds(chatId);
      return participantIds.stream()
          .map(this::getClientPublicKey)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    } catch (ChatException e) {
      log.error("Failed to get chat participants", e);
      return Collections.emptyList();
    }
  }
}
