package dev.crashbandicootfm.messenger.service.service.user;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

  @NotNull UserRepository userRepository;

  @NotNull Map<String, String> publicKeyStore = new HashMap<>();

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

    if (user.getProfileImage() != null) {
      log.info("User has profile image");
      try {
        user.setProfileImage(null);
        log.info("User profile image has been removed");
      } catch (UserException e) {
        log.error("Failed to remove profile image", e);
      }
    }

    try {
      user.setProfileImage(file.getBytes());
      log.info("User profile image has been saved");
    } catch (UserException e) {
      log.error("Failed to set profile image", e);
    }
    return userRepository.save(user);
  }

  @Override
  public byte[] getUserAvatar(Long userId) {
    UserModel user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException("User not found!"));

    if (user.getProfileImage() != null && user.getProfileImage().length > 0) {
      log.info("Loading avatar");
      return user.getProfileImage();
    }

    log.warn("Пользователь userId={} не имеет аватара, загружаем дефолтный", userId);
    return getDefaultAvatar();
  }

  private byte[] getDefaultAvatar() {
    try (InputStream inputStream = getClass().getResourceAsStream("http://localhost:8080/assets/avatar.png")) {
      if (inputStream == null) {
        log.error("Файл default-avatar.jpg не найден!");
        throw new RuntimeException("Дефолтный аватар отсутствует");
      }
      byte[] imageBytes = inputStream.readAllBytes();
      log.info("Дефолтный аватар успешно загружен, размер: {} байт", imageBytes.length);
      return imageBytes;
    } catch (IOException e) {
      log.error("Ошибка загрузки дефолтного аватара", e);
      throw new RuntimeException("Ошибка загрузки дефолтного аватара", e);
    }
  }

  @Override
  public @NotNull List<UserModel> searchUserByUserName(@NotNull String username) {
    return userRepository.findByUsernameContainingIgnoreCase(username);
  }

  @Override
  public void savePublicKey(@NotNull String username, @NotNull String publicKey) throws UserException {
    publicKeyStore.put(username, publicKey);
  }

  @Override
  public @NotNull String getPublicKey(@NotNull String username) {
    return publicKeyStore.get(username);
  }

  @Override
  public @NotNull String enableTwoFactorAuth(@NotNull Long userId) throws UserException {
    UserModel user = getById(userId);

    if (user.getEmail() == null || user.getEmail().isEmpty()) {
      throw new UserException("Email is required to enable 2FA.");
    }

    GoogleAuthenticator gAuth = new GoogleAuthenticator();
    GoogleAuthenticatorKey key = gAuth.createCredentials();

    user.setSecretKey(key.getKey());
    user.setTwoFactorEnable(true);
    userRepository.save(user);

    return GoogleAuthenticatorQRGenerator.getOtpAuthURL("MessengerServiceApplication", user.getEmail(), key);
  }

  @Override
  public boolean verifyTwoFactorCode(@NotNull Long userId, int code) throws UserException {
    UserModel user = getById(userId);

    if (!user.isTwoFactorEnable()) {
      throw new UserException("2FA is not enabled for this user.");
    }

    GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
        .setWindowSize(10) // Учитываем коды из 5 временных интервалов
        .build();

    GoogleAuthenticator gAuth = new GoogleAuthenticator(config);

    // Декодируем secretKey из Base32
    Base32 base32 = new Base32();
    byte[] decodedKey = base32.decode(user.getSecretKey());

    // Преобразуем обратно в строку Base32
    String fixedSecretKey = base32.encodeToString(decodedKey);

    log.info("Original Secret Key: {}", user.getSecretKey());
    log.info("Decoded Key: {}", new String(decodedKey));
    log.info("Re-Encoded Key: {}", fixedSecretKey);
    log.info("Current Time (ms): {}", System.currentTimeMillis());
    log.info("Received 2FA Code: {}", code);

    // Validate the code
    boolean isValid = gAuth.authorize(fixedSecretKey, code);
    log.info("Code Validation Result: {}", isValid);

    return isValid;
  }

  @Override
  public @NotNull UserModel updateEmail(Long userId, String email) throws UserException {
    UserModel user = getById(userId);

    if (userRepository.existsByEmail(email)) {
      throw new UserException("Email is already in use!");
    }

    user.setEmail(email);
    return userRepository.save(user);
  }
}
