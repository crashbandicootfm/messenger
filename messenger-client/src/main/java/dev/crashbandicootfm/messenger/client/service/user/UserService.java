package dev.crashbandicootfm.messenger.client.service.user;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import dev.crashbandicootfm.messenger.service.api.response.UserResponse;
import org.jetbrains.annotations.NotNull;

public interface UserService {

    UserResponse registerUser(@NotNull UserRequest userRequest);


}
