package dev.crashbandicootfm.messenger.server.service.user;

import dev.crashbandicootfm.messenger.server.entity.User;
import dev.crashbandicootfm.messenger.server.exception.user.UserAlreadyRegisteredException;
import dev.crashbandicootfm.messenger.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    @NotNull UserRepository userRepository;

    @NotNull PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(@NotNull User user) throws UserAlreadyRegisteredException {
        if (userRepository.findUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyRegisteredException(
                    "User with username % is already registered",
                    user.getUsername()
            );
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User loginUser(@NotNull User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
