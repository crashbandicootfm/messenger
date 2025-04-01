package dev.crashbandicootfm.messenger.service.controller.v1;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.request.VerifyTwoFactorRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.authentication.AuthenticationService;
import dev.crashbandicootfm.messenger.service.service.security.token.ClaimService;
import dev.crashbandicootfm.messenger.service.service.security.token.JwtService;
import dev.crashbandicootfm.messenger.service.service.security.token.TokenFactory;
import dev.crashbandicootfm.messenger.service.service.security.token.TokenValidator;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationController {

    @NotNull AuthenticationService authenticationService;

    @NotNull TokenFactory tokenFactory;

    @NotNull TokenValidator tokenValidator;

    @NotNull ClaimService claimService;

    @NotNull UserService userService;

    @NotNull JwtService jwtService;

//    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
//    public TokenResponse authenticate(@NotNull @RequestBody AuthenticationRequest authenticationRequest) {
//        authenticationService.authenticate(authenticationRequest);
//        String token = tokenFactory.generateToken(authenticationRequest.getUsername());
//        return new TokenResponse(token);
//    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public TokenResponse authenticate(@NotNull @RequestBody AuthenticationRequest authenticationRequest) {
        TokenResponse response = authenticationService.authenticate(authenticationRequest);
        String token = tokenFactory.generateToken(authenticationRequest.getUsername());


        if (response.isTwoFactorRequired()) {
            return new TokenResponse(token, "", true);
        }

        String refreshToken = tokenFactory.generateRefreshToken(authenticationRequest.getUsername());
        return new TokenResponse(token, refreshToken, false);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public TokenResponse register(@NotNull @RequestBody RegistrationRequest registrationRequest) {
        log.info("Registering user {}", registrationRequest.getUsername());
        authenticationService.register(registrationRequest);
        String token = tokenFactory.generateToken(registrationRequest.getUsername());
        String refreshToken = tokenFactory.generateRefreshToken(registrationRequest.getUsername());
        return new TokenResponse(token, refreshToken);
    }

    @PostMapping(value = "/refresh", consumes = "application/json", produces = "application/json")
    public TokenResponse refresh(@NotNull @RequestBody TokenResponse tokenResponse) {
        String refreshToken = tokenResponse.getRefreshToken();
        if (tokenValidator.isRefreshTokenValid(refreshToken)) {
            String username = claimService.extractUsername(refreshToken);
            String newAccessToken = tokenFactory.generateRefreshToken(username);
            return new TokenResponse(newAccessToken, refreshToken);
        } else {
            throw new RuntimeException("Refresh token is invalid or expired");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyTwoFactorCode(@RequestBody VerifyTwoFactorRequest verifyTwoFactorRequest, HttpServletRequest request) throws UserException {
        String token = request.getHeader("Authorization");
        log.info("Token: {}", token);

        int code = verifyTwoFactorRequest.getCode();

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization token is missing or invalid");
        }

        token = token.substring(7);
        String username = jwtService.extractUsername(token);
        UserModel user = userService.getByUsername(username);

        if (!userService.verifyTwoFactorCode(user.getId(), code)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code.");
        }

        String newAccessToken = tokenFactory.generateToken(username);
        String refreshToken = tokenFactory.generateRefreshToken(username);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken, false));
    }
}
