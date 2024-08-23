package dev.crashbandicootfm.messenger.service.service.security.details.provider;

import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Order(Integer.MIN_VALUE)
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UsernameUserDetailsProvider implements UserDetailsProvider {

  @NotNull UserService userService;

  @Override
  public boolean supports(@NotNull String login) {
    return true;
  }

  @Override
  public @NotNull UserDetails getByLogin(@NotNull String login) throws UsernameNotFoundException {
    return userService.findByUsername(login)
        .map(UserDetailsImpl::new)
        .orElseThrow(
            () -> new UsernameNotFoundException(String.format("User with username %s not found", login))
        );
  }
}
