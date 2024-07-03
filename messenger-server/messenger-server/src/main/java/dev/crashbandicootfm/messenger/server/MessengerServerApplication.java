package dev.crashbandicootfm.messenger.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "dev.crashbandicootfm.messenger.server")
public class MessengerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessengerServerApplication.class, args);
    }
}

/*TODO
    @RestControllerAdvice
    @Slf4j
    jakarta validation
    dto -> request, reponse
    MapperConfig -> MapperConfiguration
 */