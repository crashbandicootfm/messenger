package dev.crashbandicootfm.messengerclient;

import dev.crashbandicootfm.messengerclient.console.UserInterface;
import dev.crashbandicootfm.messengerserver.service.user.UserService;
import dev.crashbandicootfm.messengerserver.service.user.UserRestClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class MessengerClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessengerClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserService userService(@NotNull RestTemplate restTemplate) {
        return new UserRestClient(restTemplate);
    }

    @Bean
    public CommandLineRunner run(UserInterface userInterface) {
        return args -> userInterface.bootstrap();
    }
}
