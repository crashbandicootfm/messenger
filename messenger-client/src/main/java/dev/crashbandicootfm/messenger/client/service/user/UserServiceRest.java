package dev.crashbandicootfm.messenger.client.service.user;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import dev.crashbandicootfm.messenger.service.api.response.UserResponse;
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
public class UserServiceRest implements UserService {

    @NotNull RestTemplate restTemplate;

    private static final String URL = "http://localhost:8080/api/v1/users/";

    @Override
    public UserResponse registerUser(@NotNull UserRequest userRequest) {
        log.info("Registered user: {}", userRequest);
        return restTemplate.postForEntity(URL, userRequest, UserResponse.class).getBody();
    }

//    @Override
//    public TokenResponse authenticateUser(@NotNull AuthenticationRequest authenticationRequest) {
//        return restTemplate.postForObject(URL + "login", authenticationRequest, TokenResponse.class);
//    }
//
//    @Override
//    public TokenResponse register(@NotNull RegistrationRequest registrationRequest) {
//        return restTemplate.postForObject(URL + "register", registrationRequest, TokenResponse.class);
//    }
}
