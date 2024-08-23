package dev.crashbandicootfm.messenger.service.exception.user;

import org.jetbrains.annotations.NotNull;

public class ChatException extends RuntimeException {
  public ChatException(@NotNull String message) {
    super(message);
  }
}
