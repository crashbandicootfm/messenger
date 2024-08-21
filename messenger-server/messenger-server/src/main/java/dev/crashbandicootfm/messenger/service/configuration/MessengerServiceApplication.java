package dev.crashbandicootfm.messenger.service.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "dev.crashbandicootfm.messenger.service.model")
@EnableJpaRepositories(basePackages = "dev.crashbandicootfm.messenger.service.repository")
public class MessengerServiceApplication {
}
