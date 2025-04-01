package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.mediator.Mediatr;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
import dev.crashbandicootfm.messenger.service.api.request.VerifyTwoFactorRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import dev.crashbandicootfm.messenger.service.api.response.UserResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateUserCommand;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.security.token.JwtService;
import dev.crashbandicootfm.messenger.service.service.security.token.TokenFactory;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @NotNull TokenFactory tokenFactory;

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

        String avatarUrl = (userModel.getProfileImage() != null)
            ? "/api/v1/users/avatar/" + userModel.getId()
            : "/assets/avatar.png";
        log.info("Avatar URL: {}", avatarUrl);

        return new UserResponse(userModel.getId(), userModel.getUsername(), avatarUrl);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/avatar/{userId}", produces = "image/jpeg")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long userId) {
        byte[] image = userService.getUserAvatar(userId);
        log.info("Avatar: {}", Arrays.toString(image));

        if (image == null || image.length == 0) {
            log.warn("Аватар для userId={} не найден, отправляем 404", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Отдаем аватар для userId={} размером {} байт", userId, image.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.add("Cache-Control", "public, max-age=86400");

        return ResponseEntity.ok()
            .headers(headers)
            .body(image);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/upload-avatar/{userId}")
    public ResponseEntity<?> uploadAvatar(@PathVariable Long userId, @RequestParam("file") MultipartFile file)
        throws IOException {
//        try {
//            String token = request.getHeader("Authorization");
//            if (token != null && token.startsWith("Bearer ")) {
//                token = token.substring(7);
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or invalid");
//            }
//
            String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

            UserModel user = userService.getByUsername(username);
            if (!user.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to modify this user's avatar");
            }

            UserModel updatedUser = userService.uploadAvatar(userId, file);

            UserResponse response = new UserResponse(updatedUser.getId(), updatedUser.getUsername());
            return ResponseEntity.ok(response);

//        } catch (UserException | IOException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/update-public-key")
    public ResponseEntity<Void> updatePublicKey(@RequestBody String publicKey, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtService.extractUsername(token);
        try {
            userService.savePublicKey(username, publicKey);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}/public-key")
    public ResponseEntity<String> getPublicKey(@PathVariable String username) {
        String publicKey = userService.getPublicKey(username);
        if (publicKey == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Public key not found");
        }
        return ResponseEntity.ok(publicKey);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public List<UserResponse> searchUsers(@RequestParam String username) {
        List<UserModel> users = userService.searchUserByUserName(username);
        return users.stream()
            .map(user -> mapper.map(user, UserResponse.class))
            .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/enable-2fa")
    public ResponseEntity<Map<String, String>> enableTwoFactorAuth(HttpServletRequest request) throws UserException {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authorization token is missing or invalid"));
        }

        token = token.substring(7);
        String username = jwtService.extractUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid token"));
        }

        UserModel user = userService.getByUsername(username);
        String qrCodeUrl = userService.enableTwoFactorAuth(user.getId());
        if (qrCodeUrl == null || qrCodeUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate QR code"));
        }

        return ResponseEntity.ok(Map.of("qrCodeUrl", qrCodeUrl));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorCode(@RequestBody VerifyTwoFactorRequest verifyTwoFactorRequest, HttpServletRequest request) throws UserException {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            log.info("No token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or invalid");
        }

        token = token.substring(7);
        String username = jwtService.extractUsername(token);
        UserModel user = userService.getByUsername(username);

        int code = verifyTwoFactorRequest.getCode();

        if (!userService.verifyTwoFactorCode(user.getId(), code)) {
            log.info("Invalid two-factor code");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code.");
        }

        String newAccessToken = tokenFactory.generateToken(username);
        String refreshToken = tokenFactory.generateRefreshToken(username);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken, false));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/update-email")
    public ResponseEntity<?> updateEmail(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or invalid");
        }

        String username = jwtService.extractUsername(token);
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }

        try {
            UserModel user = userService.getByUsername(username);
            UserModel updatedUser = userService.updateEmail(user.getId(), email);
            return ResponseEntity.ok(mapper.map(updatedUser, UserResponse.class));
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
