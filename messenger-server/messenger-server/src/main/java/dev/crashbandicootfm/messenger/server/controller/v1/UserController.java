package dev.crashbandicootfm.messenger.server.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.messenger.server.entity.User;
import dev.crashbandicootfm.messenger.server.service.user.UserService;
import dev.crashbandicootfm.messenger.server.api.dto.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    @NotNull UserService userService;
    @NotNull Mapper mapper;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/", produces = "application/json", consumes = "application/json")
    public UserDto registerUser(@RequestBody UserDto userDto) {
            User user = mapper.map(userDto, User.class);
            User registeredUser = userService.registerUser(user);
            return mapper.map(registeredUser, UserDto.class);
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody User user) {
        return userService.loginUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}
