package dev.crashbandicootfm.messengerclient.console;

import dev.crashbandicootfm.messengerserver.entity.User;
import dev.crashbandicootfm.messengerserver.exception.UserException;
import dev.crashbandicootfm.messengerserver.service.user.UserService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ConsoleUI implements UserInterface {

    @NotNull Scanner scanner = new Scanner(System.in);

    @Autowired
    @NotNull UserService userService;

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

    @SneakyThrows(UserException.class)
    private void registerUser() {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        User user = new User(username, password);
        userService.registerUser(user);
        System.out.println("User registered successfully");
    }

    private void loginUser() {

    }

    private void startMessaging() {
        System.out.println();
    }
}
