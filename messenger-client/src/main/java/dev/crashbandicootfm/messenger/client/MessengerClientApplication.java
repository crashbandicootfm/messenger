package dev.crashbandicootfm.messenger.client;

import dev.crashbandicootfm.messenger.client.console.UserInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MessengerClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessengerClientApplication.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandLineRunner commandLineRunner(@NotNull UserInterface userInterface) {
        return args -> userInterface.bootstrap();
    }
}
