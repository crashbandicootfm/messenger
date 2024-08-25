package dev.crashbandicootfm.messenger.service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuppressWarnings("JpaDataSourceORMInspection")
public class UserModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "username", nullable = false, updatable = false)
  String username;

  @Column(name = "password", nullable = false, updatable = false)
  String password;

  @ElementCollection
  @CollectionTable(
      name = "users_chats",
      joinColumns = @JoinColumn(name = "chat_id")
  )
  List<Long> chatIds = new ArrayList<>();
}
