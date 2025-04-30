package dev.crashbandicootfm.messenger.service.controller.v1;

import com.github.dozermapper.core.Mapper;
import dev.crashbandicootfm.mediator.Mediatr;
import dev.crashbandicootfm.messenger.service.api.request.CreateChatRequest;
import dev.crashbandicootfm.messenger.service.api.request.CreateChatWithPasswordRequest;
import dev.crashbandicootfm.messenger.service.api.request.JoinChatRequest;
import dev.crashbandicootfm.messenger.service.api.response.ChatResponse;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateChatCommand;
import dev.crashbandicootfm.messenger.service.cqrs.command.CreateChatWithPasswordCommand;
import dev.crashbandicootfm.messenger.service.exception.user.ChatException;
import dev.crashbandicootfm.messenger.service.exception.user.UserException;
import dev.crashbandicootfm.messenger.service.model.ChatModel;
import dev.crashbandicootfm.messenger.service.model.MessageModel;
import dev.crashbandicootfm.messenger.service.model.UserModel;
import dev.crashbandicootfm.messenger.service.repository.ChatRepository;
import dev.crashbandicootfm.messenger.service.repository.MessageRepository;
import dev.crashbandicootfm.messenger.service.service.chat.ChatService;
import dev.crashbandicootfm.messenger.service.service.key.KeyService;
import dev.crashbandicootfm.messenger.service.service.message.MessageService;
import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @NotNull MessageService messageService;

  @NotNull KeyService keyService;

  @NotNull MessageRepository messageRepository;

  @NotNull UserService userService;

  @NotNull ChatRepository chatRepository;

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
  public ResponseEntity<?> joinChat(
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
    } catch (ChatException e) {
      log.error("Error joining chat: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          Map.of("message", e.getMessage())
      );
    } catch (Exception e) {
      log.error("Unexpected error joining chat: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          Map.of("message", "An unexpected error occurred. Please try again.")
      );
    }
  }

  @PostMapping(value = "/createWithPassword", produces = "application/json", consumes = "application/json")
  public ChatResponse createChatWithPassword(
      @RequestBody CreateChatWithPasswordRequest request,
      @AuthenticationPrincipal UserDetailsImpl principal) {
    CreateChatWithPasswordCommand command = new CreateChatWithPasswordCommand(principal.getId(), request.getName(), request.getPassword());
    ChatModel model = mediatr.dispatch(command, ChatModel.class);
    return mapper.map(model, ChatResponse.class);
  }

  @PostMapping(value = "/join-with-password", produces = "application/json", consumes = "application/json")
  public ResponseEntity<?> joinChatWithPassword(
      @RequestBody JoinChatRequest request,
      @RequestParam String password,
      @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    try {
      ChatModel chat = chatService.joinChatWithPassword(request.getName(), principal.getId(), password);

      ChatResponse response = new ChatResponse(
          chat.getId(),
          chat.getName(),
          chat.getCreatedAt()
      );
      response.setUserIds(chat.getUserIds());

      return ResponseEntity.ok(response);
    } catch (ChatException e) {
      log.error("Error joining chat: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
          Map.of("message", e.getMessage())
      );
    } catch (Exception e) {
      log.error("Unexpected error joining chat: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
          Map.of("message", "An unexpected error occurred. Please try again.")
      );
    }
  }

  @GetMapping("/find")
  public ResponseEntity<ChatResponse> findByName(@RequestParam String name) {
    ChatModel chat = chatService.findByChatName(name);
    ChatResponse response = new ChatResponse(chat.getId(), chat.getName(), chat.getCreatedAt());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/chats")
  public ResponseEntity<List<ChatResponse>> findUserChats(@AuthenticationPrincipal UserDetailsImpl principal) {
    List<ChatModel> chats = chatService.findAllByUserId(principal.getId());

    List<ChatResponse> responses = chats.stream()
        .map(chat -> {
          String lastMessage = messageService.findLastMessageByChatId(chat.getId());
          String lastMessageSender = messageService.findLastMessageSenderByChatId(chat.getId());
          Long unreadCount = chatService.getUnreadMessagesCount(chat.getId(), principal.getId());

          return new ChatResponse(
              chat.getId(),
              chat.getName(),
              chat.getCreatedAt(),
              lastMessage,
              lastMessageSender,
              unreadCount
          );
        })
        .toList();

    return ResponseEntity.ok(responses);
  }

  @PostMapping(value = "/start-private", produces = "application/json")
  public ResponseEntity<?> startPrivateChat(
      @RequestParam Long otherUserId,
      @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    try {
      ChatModel chat = chatService.startPrivateChat(principal.getId(), otherUserId);
      ChatResponse response = new ChatResponse(chat.getId(), chat.getName(), chat.getCreatedAt());
      response.setUserIds(chat.getUserIds());
      return ResponseEntity.ok(response);
    } catch (ChatException | UserException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error creating chat"));
    }
  }

  @DeleteMapping("/{chatId}")
  public ResponseEntity<?> deleteChat(
      @PathVariable Long chatId,
      @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    try {
      chatService.deleteChat(chatId, principal.getId());
      return ResponseEntity.ok(Map.of("message", "Chat deleted successfully"));
    } catch (ChatException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error deleting chat"));
    }
  }

  @PostMapping("/leave")
  public ResponseEntity<?> leaveChat(
      @RequestParam Long chatId,
      @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    try {
      chatService.leaveChat(chatId, principal.getId());
      return ResponseEntity.ok(Map.of("message", "Successfully left the chat"));
    } catch (ChatException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error leaving chat"));
    }
  }

  @GetMapping("/{chatId}/messages")
  public ResponseEntity<List<MessageModel>> getMessagesByChatId(@PathVariable Long chatId) {
    List<MessageModel> messages = messageService.getMessagesByChatId(chatId);

    if (messages.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(messages);
  }

  @GetMapping("/unread-count")
  public ResponseEntity<Long> getUnreadMessagesCount(@RequestParam Long chatId, @RequestParam Long userId) {
    return ResponseEntity.ok(chatService.getUnreadMessagesCount(chatId, userId));
  }

  @PostMapping("/{chatId}/mark-as-read")
  public ResponseEntity<Void> markChatAsRead(@PathVariable Long chatId, @RequestBody Map<String, Long> request) {
    Long userId = request.get("userId");
    chatService.markChatAsRead(chatId, userId, messageRepository.getLastMessageId(chatId));
    return ResponseEntity.ok().build();
  }

  @GetMapping("/two-user-chats")
  public ResponseEntity<List<ChatResponse>> findTwoUserChats(@AuthenticationPrincipal UserDetailsImpl principal) {
    try {
      List<ChatModel> chats = chatService.findTwoUserChats(principal.getId());

      List<ChatResponse> responses = chats.stream()
          .map(chat -> {
            String lastMessage = messageService.findLastMessageByChatId(chat.getId());
            String lastMessageSender = messageService.findLastMessageSenderByChatId(chat.getId());
            Long unreadCount = chatService.getUnreadMessagesCount(chat.getId(), principal.getId());

            return new ChatResponse(
                chat.getId(),
                chat.getName(),
                chat.getCreatedAt(),
                lastMessage,
                lastMessageSender,
                unreadCount
            );
          })
          .toList();

      return ResponseEntity.ok(responses);
    } catch (UserException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }
  }

  @GetMapping("/{chatId}/keys")
  public ResponseEntity<List<String>> getChatKeys(
      @PathVariable Long chatId,
      @AuthenticationPrincipal UserDetailsImpl principal) {

    if (!chatService.getById(chatId).getUserIds().contains(principal.getId())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return ResponseEntity.ok(keyService.getChatParticipantsKeys(chatId));
  }

  @GetMapping("/participants/ids")
  public ResponseEntity<List<Long>> getParticipantIdsByChatName(@RequestParam String name) {
    try {
      ChatModel chat = chatService.findByChatName(name);
      List<Long> participantIds = chatService.getChatParticipantIds(chat.getId());
      return ResponseEntity.ok(participantIds);
    } catch (ChatException e) {
      log.error("Error getting participant IDs: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    } catch (Exception e) {
      log.error("Unexpected error: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
    }
  }

  @GetMapping("/participants/count")
  public ResponseEntity<?> getParticipantCountByChatName(
      @RequestParam String name,
      @AuthenticationPrincipal UserDetailsImpl principal
  ) {
    try {
      // Проверка аутентификации
      if (principal == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      Optional<ChatModel> chatOpt = chatRepository.findByName(name);

      if (chatOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      ChatModel chat = chatOpt.get();

      // Проверка участия в чате
      if (!chat.getUserIds().contains(principal.getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      return ResponseEntity.ok(chat.getUserIds().size());

    } catch (Exception e) {
      log.error("Error getting participant count: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/{chatId}/participants")
  public ResponseEntity<List<UserModel>> getChatParticipants(@PathVariable Long chatId) {
    try {
      List<UserModel> participants = chatService.getChatParticipants(chatId);
      return ResponseEntity.ok(participants);
    } catch (ChatException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
