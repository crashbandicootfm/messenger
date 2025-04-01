package dev.crashbandicootfm.messenger.service.service.authentication;

import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import dev.crashbandicootfm.messenger.service.exception.user.UserAlreadyRegisteredException;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.security.token.TokenFactory;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationServiceImpl implements AuthenticationService {

    @NotNull UserService userService;

    @NotNull AuthenticationManager authenticationManager;

    @NotNull PasswordEncoder passwordEncoder;

    @NotNull TokenFactory tokenFactory;

//    public void authenticate(@NotNull AuthenticationRequest authenticationRequest) {
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//            authenticationRequest.getUsername(),
//            authenticationRequest.getPassword());
//
//        authenticationManager.authenticate(authentication);
//    }


    @Override
    public TokenResponse authenticate(@NotNull AuthenticationRequest authenticationRequest) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            authenticationRequest.getUsername(),
            authenticationRequest.getPassword()
        );
        authenticationManager.authenticate(authentication);

        UserModel user = userService.getByUsername(authenticationRequest.getUsername());

        String token = tokenFactory.generateToken(user.getUsername());

        String refreshToken = tokenFactory.generateRefreshToken(user.getUsername());

        if (Boolean.TRUE.equals(user.isTwoFactorEnable())) {
            return new TokenResponse(token, refreshToken, true);
        }

        return new TokenResponse(token, refreshToken, false);
    }

    @Override
    public void register(@NotNull RegistrationRequest registrationRequest) {
        userService.getUserOptional(registrationRequest.getUsername())
        .ifPresent(user -> {
            throw new UserAlreadyRegisteredException(
                    "User with username %s already registered",
                    registrationRequest.getUsername()
            );
        });

        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        UserModel userModel = userService.registerUser(
                UserModel.builder()
                        .username(registrationRequest.getUsername())
                        .password(encodedPassword)
                        .build()
        );

        log.info("Registered user: {}", userModel.getUsername());
    }
}
