package dev.crashbandicootfm.messenger.service.exception.user;

import org.jetbrains.annotations.NotNull;

public class UserException extends RuntimeException {

  public UserException(@NotNull String message) {
    super(message);
  }
}
