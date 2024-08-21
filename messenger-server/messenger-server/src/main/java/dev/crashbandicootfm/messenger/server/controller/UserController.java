package dev.crashbandicootfm.messenger.server.controller;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.messenger.server.api.dto.request.user.UserRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.user.UserResponse;
import dev.crashbandicootfm.messenger.server.entity.User;
import dev.crashbandicootfm.messenger.server.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    @NotNull Mapper mapper;

    @NotNull UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    public UserResponse registerUser(@RequestBody UserRequest userRequest) {
        log.info("Requested user: {}", userRequest);
        User user = mapper.map(userRequest, User.class);
        log.info("User: {}", user);
        User registeredUser = userService.registerUser(user);
        log.info("Registered user: {}", registeredUser);
        return mapper.map(registeredUser, UserResponse.class);
    }

    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public UserResponse loginUser(@RequestBody UserRequest userRequest) {
        log.info("Login requested for user: {}", userRequest);
        User user = mapper.map(userRequest, User.class);
        User authenticatedUser = userService.loginUser(user);
        log.info("Authenticated user: {}", authenticatedUser);
        return mapper.map(authenticatedUser, UserResponse.class);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}
