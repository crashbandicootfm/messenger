package dev.crashbandicootfm.messenger.service.service.key;

import org.jetbrains.annotations.NotNull;

public interface KeyService {
  void storeClientPublicKey(@NotNull Long clientId, @NotNull String publicKey);

  @NotNull String getClientPublicKey(@NotNull Long clientId);
}
