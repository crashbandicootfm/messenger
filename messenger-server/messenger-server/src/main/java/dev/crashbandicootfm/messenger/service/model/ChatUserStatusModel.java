package dev.crashbandicootfm.messenger.service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "chat_user_status")
public class ChatUserStatusModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "chat_id", nullable = false)
    Long chatId;

    @Column(name = "last_read_message_id")
    Long lastReadMessageId;
}
