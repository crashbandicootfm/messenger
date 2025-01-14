package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.mediator.Mediatr;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
import dev.crashbandicootfm.messenger.service.api.response.UserResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateUserCommand;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.security.token.JwtService;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

    @NotNull Mapper mapper;

    @NotNull Mediatr mediatr;

    @NotNull UserService userService;

    @NotNull JwtService jwtService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/", produces = "application/json", consumes = "application/json")
    public UserResponse registerUser(@RequestBody UserRequest userRequest) {
        CreateUserCommand command = new CreateUserCommand(userRequest.getUsername(), userRequest.getPassword());

        UserModel model = mediatr.dispatch(command, UserModel.class);
        return mapper.map(model, UserResponse.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = "application/json")
    public UserResponse getUserById(@PathVariable Long id) throws UserException {
        UserModel userModel = userService.getById(id);
        return mapper.map(userModel, UserResponse.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/username/{username}", produces = "application/json")
    public UserResponse getUserByUsername(@PathVariable String username) throws UserException {
        UserModel userModel = userService.getByUsername(username);
        return mapper.map(userModel, UserResponse.class);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/profile", produces = "application/json")
    public UserResponse getUserProfile(HttpServletRequest request) throws UserException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new UserException("Authorization token is missing or invalid");
        }

        String username = jwtService.extractUsername(token);
        UserModel userModel = userService.getByUsername(username);

        String avatarUrl = userModel.getProfileImage() != null
            ? "/api/v1/users/avatar/" + userModel.getId()
            : null;

      return new UserResponse(userModel.getId(), userModel.getUsername(), avatarUrl);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/avatar/{userId}", produces = "image/jpeg")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long userId) {
        byte[] image = userService.getUserAvatar(userId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "public, max-age=86400");
        headers.add("Expires", "Wed, 21 Oct 2024 07:28:00 GMT");

        return ResponseEntity.ok()
            .headers(headers)
            .body(image);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/upload-avatar/{userId}")
    public ResponseEntity<?> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or invalid");
            }

            String username = jwtService.extractUsername(token);

            UserModel user = userService.getByUsername(username);
            if (!user.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to modify this user's avatar");
            }

            UserModel updatedUser = userService.uploadAvatar(userId, file);

            UserResponse response = new UserResponse(updatedUser.getId(), updatedUser.getUsername());
            return ResponseEntity.ok(response);

        } catch (UserException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
