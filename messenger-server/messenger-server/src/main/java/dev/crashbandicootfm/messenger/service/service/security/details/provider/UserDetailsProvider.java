package dev.crashbandicootfm.messenger.service.service.security.details.provider;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsProvider {

  boolean supports(@NotNull String login);

  @NotNull UserDetails getByLogin(@NotNull String login) throws UsernameNotFoundException;
}
