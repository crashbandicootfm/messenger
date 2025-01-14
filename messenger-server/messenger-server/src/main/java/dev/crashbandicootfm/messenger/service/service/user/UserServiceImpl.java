package dev.crashbandicootfm.messenger.service.service.user;

import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.repository.UserRepository;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

  @NotNull UserRepository userRepository;

  @Override
  public boolean existsById(@NotNull Long id) {
    return userRepository.existsById(id);
  }

  @Override
  public boolean existsByUsername(@NotNull String username) {
    return userRepository.existsByUsername(username);
  }

  @Override
  public @NotNull Optional<UserModel> findById(@NotNull Long id) {
    return userRepository.findById(id);
  }

  @Override
  public @NotNull Optional<UserModel> findByUsername(@NotNull String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public @NotNull Optional<UserModel> getUserOptional(@NotNull String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public @NotNull UserModel getById(@NotNull Long id) throws UserException {
    return userRepository.findById(id).orElseThrow(
        () -> new UserException("User not found!")
    );
  }

  @Override
  public @NotNull UserModel getByUsername(@NotNull String username) throws UserException {
    return userRepository.findByUsername(username).orElseThrow(
        () -> new UserException("User not found!")
    );
  }

  @Override
  public @NotNull UserModel registerUser(@NotNull UserModel user) {
    return userRepository.save(user);
  }

  @Override
  public @NotNull UserModel uploadAvatar(@NotNull Long userId, @NotNull MultipartFile file) throws UserException, IOException {
    UserModel user = userRepository.findById(userId).orElseThrow(
        () -> new UserException("User not found!")
    );

    if (file.isEmpty()) {
      throw new UserException("File is empty!");
    }

    user.setProfileImage(file.getBytes());
    return userRepository.save(user);
  }

  @Override
  public byte[] getUserAvatar(Long userId) {
    UserModel user = userRepository.findById(userId).orElseThrow(() -> new UserException("User not found!"));
    return user.getProfileImage();
  }
}
