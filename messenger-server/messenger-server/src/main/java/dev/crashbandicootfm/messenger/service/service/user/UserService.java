package dev.crashbandicootfm.messenger.service.service.user;

import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface UserService {

  boolean existsById(@NotNull Long id);

  boolean existsByUsername(@NotNull String username);

  @NotNull Optional<UserModel> findById(@NotNull String id);

  @NotNull Optional<UserModel> findByUsername(@NotNull String username);

  @NotNull UserModel getById(@NotNull String id) throws UserException;

  @NotNull UserModel getByUsername(@NotNull String username) throws UserException;

  @NotNull UserModel save(@NotNull UserModel user);
}
