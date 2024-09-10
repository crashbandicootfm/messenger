package dev.crashbandicootfm.messenger.client.service.authentication;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import org.jetbrains.annotations.NotNull;

public interface AuthenticationService {

    TokenResponse authenticateUser(@NotNull AuthenticationRequest authenticationRequest);

    TokenResponse register(@NotNull RegistrationRequest registrationRequest);
}
