package dev.crashbandicootfm.messengerserver.service.user;

import dev.crashbandicootfm.messengerserver.entity.User;
import dev.crashbandicootfm.messengerserver.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    @Autowired
    @NotNull UserRepository userRepository;

    @Override
    public User registerUser(@NotNull User user) {
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
