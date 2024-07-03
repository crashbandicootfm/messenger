package dev.crashbandicootfm.messenger.client.console;

import dev.crashbandicootfm.messenger.client.service.UserService;
import dev.crashbandicootfm.messenger.server.api.dto.UserDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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

    private void registerUser() {
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();

        UserDto userDto = new UserDto(username, password);
        userService.registerUser(userDto);
        System.out.println("User registered successfully");
    }

    private void loginUser() {

    }

    private void startMessaging() {
        System.out.println();
    }
}
