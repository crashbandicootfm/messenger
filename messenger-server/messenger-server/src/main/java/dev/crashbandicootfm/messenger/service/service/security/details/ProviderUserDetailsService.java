package dev.crashbandicootfm.messenger.service.service.security.details;

import dev.crashbandicootfm.messenger.service.service.security.details.provider.UserDetailsProvider;
import dev.crashbandicootfm.messenger.service.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProviderUserDetailsService implements UserDetailsService {

  @NotNull List<UserDetailsProvider> providers;

  @Override
  public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
    UserDetailsProvider provider = providers.stream()
        .filter(p -> p.supports(username))
        .min(Comparator.comparingInt(
            p -> Optional.ofNullable(
                    p.getClass().getAnnotation(Order.class))
                .map(Order::value)
                .orElse(0)
        ))
        .orElseThrow(() -> new UsernameNotFoundException("No supported providers found!"));
    return provider.getByLogin(username);
  }

}