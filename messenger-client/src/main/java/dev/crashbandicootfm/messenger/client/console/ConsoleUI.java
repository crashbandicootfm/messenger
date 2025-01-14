package dev.crashbandicootfm.messenger.client.console;

import dev.crashbandicootfm.messenger.client.service.authentication.AuthenticationService;
import dev.crashbandicootfm.messenger.client.service.chat.ChatService;
import dev.crashbandicootfm.messenger.client.service.message.MessageService;
import dev.crashbandicootfm.messenger.client.service.user.UserService;
import dev.crashbandicootfm.messenger.client.user.User;
import dev.crashbandicootfm.messenger.service.api.request.AuthenticationRequest;
import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.request.MessageRequest;
import dev.crashbandicootfm.messenger.service.api.request.RegistrationRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import dev.crashbandicootfm.messenger.service.api.response.MessageResponse;
import dev.crashbandicootfm.messenger.service.api.response.TokenResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    @NotNull MessageService messageService;
    @NotNull User user;

    @NonFinal
    Long currentChatId = null;

    @Override
    public void bootstrap() {
        while (true) {
            System.out.println("Choose an option");
            System.out.println("1: Register");
            System.out.println("2: Login");
            System.out.println("3: Create Chat");
            System.out.println("4: Join Chat by Name");
            System.out.println("5: Send Message");
            System.out.println("6: View messages");
            System.out.println("7: Exit");
            System.out.print("> ");

            String action = scanner.nextLine();

            if (action.matches("[1-7]") && action.length() == 1) {
                int choice = Integer.parseInt(action);
                switch (choice) {
                    case 1 -> registerUser();
                    case 2 -> authenticateUser();
                    case 3 -> createChat();
                    case 4 -> joinChatByName();
                    case 5 -> sendMessage();
                    case 6 -> viewMessages();
                    case 7 -> System.exit(1);
                }
            } else {
                System.out.println("Invalid input");
            }
        }
    }

    private void createChat() {
        if (isAuthenticated()) {
            System.out.println("You need to be logged in to create a chat.");
            return;
        }

        System.out.println("Enter the chat name: ");
        String chatName = scanner.nextLine();

        CreateChatRequest createChatRequest = new CreateChatRequest(chatName);
        ChatResponse chat = chatService.create(createChatRequest, user.getJwt());

        if (chat != null) {
            System.out.println("Chat created with ID: " + chat.getId());
            currentChatId = chat.getId();
        } else {
            System.out.println("Failed to create chat");
        }
    }

    private void joinChatByName() {
        if (isAuthenticated()) {
            System.out.println("You need to be logged in to join a chat.");
            return;
        }

        System.out.println("Enter the chat name to join: ");
        String chatName = scanner.nextLine();

        ChatResponse chat = chatService.findByName(chatName, user.getJwt());

        if (chat != null) {
            System.out.println("Joined chat with ID: " + chat.getId());
            currentChatId = chat.getId();
        } else {
            System.out.println("Chat not found");
        }
    }

    private void sendMessage() {
        if (isAuthenticated()) {
            System.out.println("You need to be logged in to send messages.");
            return;
        }

        if (currentChatId == null) {
            System.out.println("No chat selected. Please create or join a chat first.");
            return;
        }

        System.out.println("Enter your message: ");
        String message = scanner.nextLine();

        MessageRequest messageRequest = new MessageRequest(message, currentChatId);
        messageService.sendMessage(messageRequest, user.getJwt());
    }

    private boolean isAuthenticated() {
        return user.getJwt().isEmpty();
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
      System.out.println("Registration successful");
    }

    private void authenticateUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        authenticate(username, password);
    }

    private void authenticate(String username, String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        log.info("Requested authentication: {}", authenticationRequest);

        TokenResponse tokenResponse = authenticationService.authenticateUser(authenticationRequest);

        if (tokenResponse != null) {
            user.setJwt(tokenResponse.getToken());
            System.out.println("Authentication successful");
        } else {
            System.out.println("Authentication failed");
        }
    }

    private void viewMessages() {
        if (currentChatId == null) {
            System.out.println("No chat selected. Please create or join a chat first.");
        }
        List<MessageResponse> messages = messageService.getMessagesByChatId(currentChatId, user.getJwt());

        if (messages != null && !messages.isEmpty()) {
            System.out.println("Messages: ");
            for (MessageResponse message : messages) {
                System.out.printf("[%s] %s: %s%n", message.getSentAt(), message.getCreatedBy(), message.getMessage());
            }
        } else {
            System.out.println("No messages found");
        }
    }
}
