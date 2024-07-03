package dev.crashbandicootfm.messenger.client.service;

import dev.crashbandicootfm.messenger.server.api.dto.UserDto;
import org.jetbrains.annotations.NotNull;

public interface UserService {

    UserDto registerUser(@NotNull UserDto userDto);

    void deleteUser(@NotNull Long id);
}
