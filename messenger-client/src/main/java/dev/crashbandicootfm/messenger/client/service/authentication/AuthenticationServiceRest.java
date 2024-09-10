package dev.crashbandicootfm.messenger.client.service.authentication;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationServiceRest implements AuthenticationService {

    private static final String URL = "http://localhost:8080/api/v1/authentication";

    @NotNull RestTemplate restTemplate;

    @Override
    public TokenResponse authenticateUser(@NotNull AuthenticationRequest authenticationRequest) {
        return restTemplate.postForEntity(URL + "/login", authenticationRequest, TokenResponse.class).getBody();
    }

    @Override
    public TokenResponse register(@NotNull RegistrationRequest registrationRequest) {
        log.info("Sending request to register: {}", registrationRequest);
        return restTemplate.postForEntity(URL + "/register", registrationRequest, TokenResponse.class).getBody();
    }
}
