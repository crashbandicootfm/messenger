package dev.crashbandicootfm.messengerserver.service.user;

import dev.crashbandicootfm.messengerserver.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.client.RestTemplate;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserRestClient implements UserService {

    private static final String URL = "http://localhost:8080/api/users";

    @NotNull RestTemplate restTemplate;


    @Override
    public User registerUser(@NotNull User user) {
        return restTemplate.postForEntity(URL, user, User.class).getBody();
    }

    @Override
    public User loginUser(@NotNull User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
