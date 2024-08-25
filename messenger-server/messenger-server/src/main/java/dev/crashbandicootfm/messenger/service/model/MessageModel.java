package dev.crashbandicootfm.messenger.service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@Slf4j
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "messages")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("JpaDataSourceORMInspection")
public class MessageModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "sent_at", updatable = false, nullable = false)
  Date sentAt;

  @Column(name = "created_by", updatable = false, nullable = false)
  Long createdBy;

  @Column(name = "chat_id", updatable = false, nullable = false)
  Long chatId;

  @Column(name = "message", updatable = false, nullable = false)
  String message;
}
