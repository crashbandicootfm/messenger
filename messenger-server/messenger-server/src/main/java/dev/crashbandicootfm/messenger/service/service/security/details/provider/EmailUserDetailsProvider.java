package dev.crashbandicootfm.messenger.service.service.security.details.provider;

import dev.crashbandicootfm.messenger.service.service.security.details.UserDetailsImpl;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmailUserDetailsProvider implements UserDetailsProvider {

  @NotNull UserService userService;

  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

  @Override
  public boolean supports(@NotNull String login) {
    return EMAIL_PATTERN.matcher(login).matches();
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
