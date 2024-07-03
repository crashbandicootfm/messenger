package dev.crashbandicootfm.messenger.server.exception.handler;

import dev.crashbandicootfm.messenger.server.exception.user.UserException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @NotNull ExceptionResponse handle(@NotNull UserException userException) {
        return new ExceptionResponse(
                userException.getClass(),
                userException.getMessage()
        );
    }
}
