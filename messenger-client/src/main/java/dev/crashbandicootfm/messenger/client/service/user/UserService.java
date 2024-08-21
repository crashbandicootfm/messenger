package dev.crashbandicootfm.messenger.client.service.user;

import dev.crashbandicootfm.messenger.server.api.dto.request.user.UserRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.user.UserResponse;
import org.jetbrains.annotations.NotNull;

public interface UserService {

    UserResponse registerUser(@NotNull UserRequest userRequest);

    UserResponse loginUser(@NotNull UserRequest userRequest);

    void deleteUser(@NotNull Long id);
}
