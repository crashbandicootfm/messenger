package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
import dev.crashbandicootfm.messenger.service.api.response.UserResponse;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    @NotNull UserService userService;

    @NotNull Mapper mapper;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/", produces = "application/json", consumes = "application/json")
    public @NotNull UserResponse saveUser(@RequestBody UserRequest userRequest) {
        UserModel user = mapper.map(userRequest, UserModel.class);
        UserModel registeredUser = userService.save(user);
        return mapper.map(registeredUser, UserResponse.class);
    }
}
