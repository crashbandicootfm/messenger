package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.mediator.Mediatr;
import dev.crashbandicootfm.messenger.service.api.request.MessageRequest;
import dev.crashbandicootfm.messenger.service.api.response.MessageResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateMessageCommand;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.service.message.MessageService;
import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageController {

    @NotNull Mediatr mediatr;

    @NotNull Mapper mapper;

    @NotNull MessageService messageService;

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

    @GetMapping("/chat/{chatId}")
    public List<MessageResponse> getChatMessages(@PathVariable Long chatId) {
        List<MessageModel> messages = messageService.getMessagesByChatId(chatId);

        return messages.stream()
            .map(message -> new MessageResponse(
                message.getId(),
                message.getMessage(),
                message.getSentAt(),
                message.getCreatedBy(),
                message.getChatId()
            )).collect(Collectors.toList());
    }

    @DeleteMapping("/mess/{messageId}")
    public void deleteChatMessages(@PathVariable Long messageId) {
        System.out.println("AAAAAAAAAAAA");
        messageService.deleteMessageById(messageId);
        System.out.println("Deleted message: " + messageId);
    }

//    @GetMapping("/chat/{chatId}")
//    public Page<MessageResponse> getMessages(@PathVariable Long chatId, Pageable pageable) {
//        Page<MessageModel> messages = messageService.findByChatId(chatId, pageable);
//
//        return messages.map(message -> new MessageResponse(
//            message.getId(),
//            message.getMessage(),
//            message.getSentAt(),
//            message.getCreatedBy(),
//            message.getChatId()
//        ));
//    }
}
