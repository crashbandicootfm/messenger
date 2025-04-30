package dev.crashbandicootfm.messenger.service.service.key;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface KeyService {
  void storeClientPublicKey(Long clientId, @NotNull String publicKey);

  @NotNull String getClientPublicKey(@NotNull Long clientId);

  List<String> getChatParticipantsKeys(Long chatId);
}
