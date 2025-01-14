package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.mediator.Mediatr;
import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.request.JoinChatRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateChatCommand;
import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.service.chat.ChatService;
import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1/chats/")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChatController {


  @NotNull Mapper mapper;

  @NotNull Mediatr mediatr;

  @NotNull ChatService chatService;

  @PostMapping(value = "/", produces = "application/json", consumes = "application/json")
  public ChatResponse create(
          @NotNull @RequestBody CreateChatRequest request,
          @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    CreateChatCommand command = new CreateChatCommand(principal.getId(), request.getName());
    ChatModel model = mediatr.dispatch(command, ChatModel.class);
    return mapper.map(model, ChatResponse.class);
//    log.info("Received request to create a chat");
//    throw new NotImplementedException();
  }

  @PostMapping(value = "/join", produces = "application/json", consumes = "application/json")
  public ResponseEntity<ChatResponse> joinChat(
      @RequestBody JoinChatRequest request,
      @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    try {
      ChatModel chat = chatService.joinChat(request.getName(), principal.getId());

      ChatResponse response = new ChatResponse(
          chat.getId(),
          chat.getName(),
          chat.getCreatedAt()
      );

      response.setUserIds(chat.getUserIds());

      return ResponseEntity.ok(response);
    } catch (ChatException | UserException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          new ChatResponse(null, e.getMessage(), null)
      );
    }
  }

  @GetMapping("/find")
  public ResponseEntity<ChatResponse> findByName(@RequestParam String name) {
    ChatModel chat = chatService.findByChatName(name);
    ChatResponse response = new ChatResponse(chat.getId(), chat.getName(), chat.getCreatedAt());
    return ResponseEntity.ok(response);
  }
}
