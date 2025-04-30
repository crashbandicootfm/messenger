package dev.crashbandicootfm.messenger.service.service.user;

import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import java.io.IOException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  boolean existsById(@NotNull Long id);

  boolean existsByUsername(@NotNull String username);

  @NotNull Optional<UserModel> findById(@NotNull Long id);

  @NotNull Optional<UserModel> findByUsername(@NotNull String username);

  @NotNull Optional<UserModel> getUserOptional(@NotNull String username);

  @NotNull UserModel getById(@NotNull Long id) throws UserException;

  @NotNull UserModel getByUsername(@NotNull String username) throws UserException;

  @NotNull UserModel registerUser(@NotNull UserModel user);

  @NotNull UserModel uploadAvatar(@NotNull Long userId, @NotNull MultipartFile file) throws UserException, IOException;

  byte[] getUserAvatar(Long userId);

  @NotNull List<UserModel> searchUserByUserName(@NotNull String username);

  UserModel savePublicKey(@NotNull String username, @NotNull String publicKey) throws UserException;

  @NotNull String getPublicKey(@NotNull String username);

  @NotNull String enableTwoFactorAuth(@NotNull Long userId) throws UserException;

  boolean verifyTwoFactorCode(@NotNull Long userId, int code) throws UserException;

  @NotNull UserModel updateEmail(Long userId, String email) throws UserException;
}
