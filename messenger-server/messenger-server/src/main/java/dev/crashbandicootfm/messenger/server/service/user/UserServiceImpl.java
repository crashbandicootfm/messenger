package dev.crashbandicootfm.messenger.server.service.user;

import dev.crashbandicootfm.messenger.server.entity.User;
import dev.crashbandicootfm.messenger.server.exception.user.UserAlreadyRegisteredException;
import dev.crashbandicootfm.messenger.server.exception.user.UserNotFoundException;
import dev.crashbandicootfm.messenger.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    @NotNull UserRepository userRepository;

//    @NotNull PasswordEncoder passwordEncoder;

    @Override
    public @NotNull User registerUser(@NotNull User user) throws UserAlreadyRegisteredException {
        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyRegisteredException(
                    "User with username % is already registered",
                    user.getUsername()
            );
        }
        log.info("Original password: {}", user.getPassword());
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public @NotNull User loginUser(@NotNull User user) throws UserNotFoundException {

        return userRepository.findUserByUsername(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
