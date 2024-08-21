package dev.crashbandicootfm.messenger.client.service.user;

import dev.crashbandicootfm.messenger.server.api.dto.request.user.UserRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.user.UserResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceRest implements UserService {

    @NotNull RestTemplate restTemplate;

    private static final String URL = "http://localhost:8080/api/v1/users/";

    @Override
    public UserResponse registerUser(@NotNull UserRequest userRequest) {
        log.info("Registered user: {}", userRequest);
        return restTemplate.postForEntity(
                URL + "register", userRequest, UserResponse.class
        ).getBody();
    }

    @Override
    public UserResponse loginUser(@NotNull UserRequest userRequest) {
        log.info("Logged in user: {}", userRequest);
        return restTemplate.postForEntity(
                URL + "login", userRequest, UserResponse.class
        ).getBody();
    }

    @Override
    public void deleteUser(@NotNull Long id) {
        restTemplate.delete(URL + "/" + id);
    }
}
