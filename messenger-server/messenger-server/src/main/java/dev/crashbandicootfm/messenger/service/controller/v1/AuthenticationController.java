package dev.crashbandicootfm.messenger.service.controller.v1;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import dev.crashbandicootfm.messenger.service.service.authentication.AuthenticationService;
import dev.crashbandicootfm.messenger.service.service.security.token.TokenFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationController {

    @NotNull AuthenticationService authenticationService;

    @NotNull TokenFactory tokenFactory;

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public TokenResponse authenticate(@NotNull @RequestBody AuthenticationRequest authenticationRequest) {
        authenticationService.authenticate(authenticationRequest);
        String token = tokenFactory.generateToken(authenticationRequest.getUsername());
        return new TokenResponse(token);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public TokenResponse register(@NotNull @RequestBody RegistrationRequest registrationRequest) {
        log.info("Registering user {}", registrationRequest.getUsername());
        authenticationService.register(registrationRequest);
        String token = tokenFactory.generateToken(registrationRequest.getUsername());
        return new TokenResponse(token);
    }
}
