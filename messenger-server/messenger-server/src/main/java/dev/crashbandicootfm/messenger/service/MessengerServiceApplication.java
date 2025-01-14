package dev.crashbandicootfm.messenger.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "dev.crashbandicootfm"
    }
)
@EntityScan(basePackages = "dev.crashbandicootfm.messenger.service.model")
@EnableJpaRepositories(basePackages = "dev.crashbandicootfm.messenger.service.repository")
public class MessengerServiceApplication {

  public static void main(String @NotNull [] args) {
    SpringApplication.run(MessengerServiceApplication.class, args);
  }
}
