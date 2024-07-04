package dev.crashbandicootfm.messenger.client.service;

import dev.crashbandicootfm.messenger.server.api.dto.request.UserRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.UserResponse;
import org.jetbrains.annotations.NotNull;

public interface UserService {

    UserResponse registerUser(@NotNull UserRequest userRequest);

    void deleteUser(@NotNull Long id);
}
