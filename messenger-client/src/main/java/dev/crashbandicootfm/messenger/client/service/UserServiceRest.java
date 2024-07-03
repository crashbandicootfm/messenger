package dev.crashbandicootfm.messenger.client.service;

import dev.crashbandicootfm.messenger.server.api.dto.UserDto;
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
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceRest implements UserService {

    @NotNull RestTemplate restTemplate;

    private static final String URL = "http://localhost:8080/api/v1/users";

    @Override
    public UserDto registerUser(@NotNull UserDto userDto) {
        try {
            log.info("Registering user: {}", userDto);
            return restTemplate.postForObject(URL, userDto, UserDto.class);
        } catch (HttpClientErrorException e) {
            log.error("Error registering user", e);
            throw e;
        }
    }

    @Override
    public void deleteUser(@NotNull Long id) {
        restTemplate.delete(URL + "/" + id);
    }
}
