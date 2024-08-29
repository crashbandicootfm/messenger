package dev.crashbandicootfm.messenger.client.console;

import dev.crashbandicootfm.messenger.client.service.user.UserService;
import dev.crashbandicootfm.messenger.service.api.request.UserRequest;
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

    @Autowired
    @NotNull UserService userService;

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
                    case 2 -> System.exit(1);
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

        UserRequest userRequest = new UserRequest(username, password);
        log.info("Requested user: {}", userRequest);
        userService.registerUser(userRequest);
        System.out.println("User registered successfully");
    }
}
