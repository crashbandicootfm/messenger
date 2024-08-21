package dev.crashbandicootfm.messenger.server.controller;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.messenger.server.api.dto.request.message.MessageRequest;
import dev.crashbandicootfm.messenger.server.api.dto.request.user.UserRequest;
import dev.crashbandicootfm.messenger.server.api.dto.response.message.MessageResponse;
import dev.crashbandicootfm.messenger.server.entity.Message;
import dev.crashbandicootfm.messenger.server.service.message.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageController {

    @NotNull Mapper mapper;

    @NotNull MessageService messageService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public MessageResponse sendMessage(@RequestBody MessageRequest messageRequest) {
        Message message = mapper.map(messageRequest, Message.class);
        Message sentMessage = messageService.sendMessage(message);
        return mapper.map(sentMessage, MessageResponse.class);
    }

    @GetMapping("/chat/{id}")
    public List<MessageResponse> getMessages(@PathVariable UUID id) {
        List<Message> messages = messageService.getMessages(id);
        return messages.stream()
                .map(message -> mapper.map(message, MessageResponse.class))
                .collect(Collectors.toList());
    }
}
