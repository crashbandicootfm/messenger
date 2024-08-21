package dev.crashbandicootfm.messenger.server.service.user;

import dev.crashbandicootfm.messenger.server.entity.User;
import dev.crashbandicootfm.messenger.server.exception.user.UserException;
import org.jetbrains.annotations.NotNull;

public interface UserService {

    @NotNull User registerUser(@NotNull User user) throws UserException;

    @NotNull User loginUser(@NotNull User user) throws UserException;

    void deleteUser(long id);
}
