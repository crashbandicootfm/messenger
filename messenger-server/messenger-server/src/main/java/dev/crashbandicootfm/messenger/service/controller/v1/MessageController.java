package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.messenger.service.api.request.MessageRequest;
import dev.crashbandicootfm.messenger.service.api.response.MessageResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateMessageCommand;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.qubique.mediatr.Mediatr;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageController {

    @NotNull Mediatr mediatr;

    @NotNull Mapper mapper;

    @PostMapping("/")
    public MessageResponse create(
            @NotNull @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        CreateMessageCommand command = new CreateMessageCommand(
                request.getMessage(),
                request.getChatId(),
                principal.getId()
        );
        MessageModel model = mediatr.dispatch(command, MessageModel.class);
        return mapper.map(model, MessageResponse.class);
    }
}
