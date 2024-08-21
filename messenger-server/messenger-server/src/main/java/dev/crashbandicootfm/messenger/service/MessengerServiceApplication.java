package dev.crashbandicootfm.messenger.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
        "pw.qubique.mediatr",
        "dev.crashbandicootfm.messenger.service"
    }
)
public class MessengerServiceApplication {

  public static void main(String @NotNull [] args) {
    SpringApplication.run(MessengerServiceApplication.class, args);
  }
}
