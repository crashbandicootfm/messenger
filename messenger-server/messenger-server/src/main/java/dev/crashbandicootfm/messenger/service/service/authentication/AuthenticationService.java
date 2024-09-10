package dev.crashbandicootfm.messenger.service.service.authentication;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import org.jetbrains.annotations.NotNull;

public interface AuthenticationService {

    void authenticate(@NotNull AuthenticationRequest authenticationRequest);

    void register(@NotNull RegistrationRequest registrationRequest);
}
