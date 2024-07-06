package dev.crashbandicootfm.messenger.server.service.user;

import dev.crashbandicootfm.messenger.server.entity.User;
import dev.crashbandicootfm.messenger.server.exception.user.UserException;
import org.jetbrains.annotations.NotNull;

public interface UserService {

    User registerUser(@NotNull User user) throws UserException;

    User loginUser(@NotNull User user);

    void deleteUser(Long id);
}
