package dev.crashbandicootfm.messenger.service.service.key;

import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class KeyServiceImpl implements KeyService {

  @NotNull ConcurrentHashMap<Long, String> keys = new ConcurrentHashMap<>();

  @Override
  public void storeClientPublicKey(@NotNull Long clientId, @NotNull String publicKey) {
    keys.put(clientId, publicKey);
  }

  @Override
  public @NotNull String getClientPublicKey(@NotNull Long clientId) {
    return keys.get(clientId);
  }
}
