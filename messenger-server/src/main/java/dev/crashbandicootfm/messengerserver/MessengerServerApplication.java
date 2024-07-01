package dev.crashbandicootfm.messengerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dev.crashbandicootfm.messengerserver")
public class MessengerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessengerServerApplication.class, args);
    }
}
