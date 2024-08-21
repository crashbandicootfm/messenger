package dev.crashbandicootfm.messenger.client.console;

import dev.crashbandicootfm.messenger.client.service.message.MessageService;
import dev.crashbandicootfm.messenger.client.service.user.UserService;
import dev.crashbandicootfm.messenger.server.api.dto.request.message.MessageRequest;
import dev.crashbandicootfm.messenger.server.api.dto.request.user.UserRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.message.MessageResponse;
import dev.crashbandicootfm.messenger.server.api.dto.response.user.UserResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ConsoleUI implements UserInterface {

    @NotNull Scanner scanner = new Scanner(System.in);

    @NonFinal
    @NotNull UUID uuid;

    @Autowired
    @NotNull UserService userService;

    @Autowired
    @NotNull MessageService messageService;

    @Override
    public void bootstrap() {
        while (true) {
            System.out.println("Choose an option");
            System.out.println("1: Register");
            System.out.println("2: Login");
            System.out.print("> ");

            String action = scanner.nextLine();

            if (action.matches("[1-3]") && action.length() == 1) {
                int choice = Integer.parseInt(action);
                switch (choice) {
                    case 1 -> registerUser();
                    case 2 -> loginUser();
                    case 3 -> System.exit(1);
                }
            } else {
                System.out.println("Invalid input");
            }
        }
    }

    private void registerUser() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        UserRequest user = new UserRequest(username, password);
        userService.registerUser(user);
        System.out.println("User registered successfully");

        menu();
    }

    private void loginUser() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        UserRequest user = new UserRequest(username, password);
        try {
            UserResponse response = userService.loginUser(user);
            System.out.println("Login successful. Welcome " + response.getUsername());
            menu();
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void startMessaging(String id) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> fetchMessages(id), 0, 2, TimeUnit.SECONDS);

        while (true) {
            System.out.print("Enter the message: ");
            String message = scanner.nextLine();

            if ("exit".equalsIgnoreCase(message)) {
                break;
            }

            MessageRequest messageRequest = new MessageRequest(id, message);
            messageService.sendMessage(messageRequest);
        }

        service.shutdown();
    }

    private void fetchMessages(String id) {
        try {
            List<MessageResponse> messageResponses = messageService.getMessages(id);
            for (MessageResponse message : messageResponses) {
                System.out.println("[" + message.getMessage() + "]");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void menu() {
        System.out.println("Press 1 to create chat");
        System.out.println("Press 2 to join chat");
        System.out.print("> ");

        String action = scanner.nextLine();

        if (action.matches("[1-2]") && action.length() == 1) {
            int choice = Integer.parseInt(action);

            switch (choice) {
                case 1 -> createChat();
                case 2 -> joinChat();
            }
        } else {
            System.out.println("Invalid input");
        }
    }

    private void createChat() {
        uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        System.out.print("Chat uuid is: " + uuidAsString);
        startMessaging(uuidAsString);
    }

    private void joinChat() {
        System.out.print("Enter chat UUID: ");

        String enteredUuid = scanner.nextLine();
        uuid = UUID.fromString(enteredUuid);
        startMessaging(enteredUuid);
    }
}
