package dev.crashbandicootfm.messenger.service.cqrs.handler;

import dev.crashbandicootfm.messenger.service.cqrs.command.CreateUserCommand;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pw.qubique.mediatr.command.CommandHandler;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand, UserModel> {

    @NotNull UserService userService;

    @Override
    public @NotNull UserModel handle(@NotNull CreateUserCommand command) {
        UserModel model = UserModel.builder()
                .username(command.getUsername())
                .password(command.getPassword())
                .build();

        return userService.registerUser(model);
    }
}
