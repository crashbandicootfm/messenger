package dev.crashbandicootfm.messenger.client.service;

import dev.crashbandicootfm.messenger.server.api.dto.request.UserRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.UserResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceRest implements UserService {

    @NotNull RestTemplate restTemplate;

    private static final String URL = "http://localhost:8080/api/users/";

    @Override
    public UserResponse registerUser(@NotNull UserRequest userRequest) {
        log.info("Registered user: {}", userRequest);
        return restTemplate.postForEntity(URL, userRequest, UserResponse.class).getBody();
    }

    @Override
    public void deleteUser(@NotNull Long id) {
        restTemplate.delete(URL + "/" + id);
    }
}
