package dev.crashbandicootfm.messengerserver.service.user;

import dev.crashbandicootfm.messengerserver.entity.User;
import org.jetbrains.annotations.NotNull;

public interface UserService{

    User registerUser(@NotNull User user);

    User loginUser(@NotNull User user);

    void deleteUser(Long id);
}
