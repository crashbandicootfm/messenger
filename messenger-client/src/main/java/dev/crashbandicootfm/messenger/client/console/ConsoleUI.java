package dev.crashbandicootfm.messenger.client.console;

import dev.crashbandicootfm.messenger.client.service.authentication.AuthenticationService;
import dev.crashbandicootfm.messenger.client.service.chat.ChatService;
import dev.crashbandicootfm.messenger.client.service.user.UserService;
import dev.crashbandicootfm.messenger.client.user.User;
import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ConsoleUI implements UserInterface {

    @NotNull Scanner scanner = new Scanner(System.in);

    @NotNull UserService userService;

    @NotNull AuthenticationService authenticationService;

    @NotNull ChatService chatService;

    @NotNull User user;

    @Override
    public void bootstrap() {
        while (true) {
            System.out.println("Choose an option");
            System.out.println("1: Register");
            System.out.println("2: Exit");
            System.out.print("> ");

            String action = scanner.nextLine();

            if (action.matches("[1-3]") && action.length() == 1) {
                int choice = Integer.parseInt(action);
                switch (choice) {
                    case 1 -> registerUser();
                    case 2 -> authenticateUser();
                    case 3 -> System.exit(1);
                }
            } else {
                System.out.println("Invalid input");
            }
        }
    }

    private void registerUser() {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        RegistrationRequest registrationRequest = new RegistrationRequest(username, password);
        log.info("Requested user: {}", registrationRequest);
        String jwt = authenticationService.register(registrationRequest).getToken();
        user.setJwt(jwt);
        log.info("JWT: {}", jwt);

        createChat();
    }

    private void authenticateUser() {
        System.out.print("Enter username");
        String username = scanner.nextLine();
        System.out.print("Enter password");
        String password = scanner.nextLine();

        authenticate(username, password);
    }

    private void authenticate(String username, String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        log.info("Requested authentication: {}", authenticationRequest);

        TokenResponse tokenResponse = authenticationService.authenticateUser(authenticationRequest);

        if (tokenResponse != null) {
            System.out.println("Authentication successful");
        } else {
            System.out.println("Authentication failed");
        }
    }

    private void createChat() {
        System.out.println("Enter the chat name: ");
        String chatName = scanner.nextLine();

        CreateChatRequest createChatRequest = new CreateChatRequest(chatName);
        log.info("Requested chat: {}", createChatRequest);
        chatService.create(createChatRequest);
    }
}
