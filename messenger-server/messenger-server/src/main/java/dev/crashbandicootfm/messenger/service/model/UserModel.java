package dev.crashbandicootfm.messenger.service.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@SuppressWarnings("JpaDataSourceORMInspection")
public class UserModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "username", nullable = false, updatable = false)
  String username;

  @Column(name = "password", nullable = false, updatable = false)
  String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "users_chats",
      joinColumns = @JoinColumn(name = "chat_id")
  )
//  @Column(name = "user_id")
  List<Long> chatIds = new ArrayList<>();

  @Column(name = "profile_image")
  @Lob
  byte[] profileImage;

  @Column(name = "email", unique = true)
  String email;

  @Builder.Default
  @Column(name = "is_two_factor_enable", nullable = false)
  Boolean isTwoFactorEnable = false;

  String secretKey;

  @Column(name = "public_key")
  String publicKey;

  public void setTwoFactorEnable(boolean b) {
    isTwoFactorEnable = b;
  }

  public boolean isTwoFactorEnable() {
    return Boolean.TRUE.equals(isTwoFactorEnable);
  }
}
