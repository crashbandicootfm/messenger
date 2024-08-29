package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
import dev.crashbandicootfm.messenger.service.api.response.UserResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateUserCommand;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pw.qubique.mediatr.Mediatr;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    @NotNull Mapper mapper;

    @NotNull Mediatr mediatr;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/", produces = "application/json", consumes = "application/json")
    public UserResponse registerUser(@RequestBody UserRequest userRequest) {
        CreateUserCommand command = new CreateUserCommand(userRequest.getUsername(), userRequest.getPassword());

        UserModel model = mediatr.dispatch(command, UserModel.class);
        return mapper.map(model, UserResponse.class);
    }
}
