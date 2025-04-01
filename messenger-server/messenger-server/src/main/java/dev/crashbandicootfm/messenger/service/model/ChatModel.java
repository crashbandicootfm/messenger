package dev.crashbandicootfm.messenger.service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "chats")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@SuppressWarnings("JpaDataSourceORMInspection")
public class ChatModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "name", updatable = false, nullable = false)
  String name;

  @Column(name = "created_at", updatable = false, nullable = false)
  Date createdAt;

  @Column(name = "created_by", updatable = false, nullable = false)
  Long createdBy;

  @Column(name = "password")
  String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "chats_users",
      joinColumns = @JoinColumn(name = "user_id")
  )
  List<Long> userIds = new ArrayList<>();

  @Column(name = "unread_count")
  Long unreadCount;
}
