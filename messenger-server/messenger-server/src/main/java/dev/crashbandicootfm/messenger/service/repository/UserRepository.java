package dev.crashbandicootfm.messenger.service.repository;

import dev.crashbandicootfm.messenger.service.model.UserModel;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserModel, Long> {

  boolean existsByUsername(@NotNull String username);

  boolean existsByEmail(@NotNull String email);

  @NotNull Optional<UserModel> findByUsername(@NotNull String username);

  @NotNull List<UserModel> findByUsernameContainingIgnoreCase(String username);
}
