package dev.crashbandicootfm.messenger.service.configuration;

import dev.crashbandicootfm.messenger.service.service.security.filter.JwtAuthenticationFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationProvider authenticationProvider(@NotNull UserDetailsService userDetailsService,
                                                  @NotNull PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(passwordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            @NotNull AuthenticationProvider authenticationProvider) {
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
        @NotNull HttpSecurity http,
        @NotNull AuthenticationManager authenticationManager,
        @NotNull JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
      return http
          .sessionManagement(session ->
              session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .formLogin(AbstractHttpConfigurer::disable)
          .httpBasic(Customizer.withDefaults())
          .csrf(AbstractHttpConfigurer::disable)
          .cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOrigin("http://localhost:4200"); // Specify frontend domain, e.g. Angular
            configuration.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, PUT, etc.)
            configuration.addAllowedHeader("*"); // Allow all headers
            configuration.setAllowCredentials(true); // Allow cookies and credentials like Authorization headers

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);

            httpSecurityCorsConfigurer.configurationSource(source);
          })
          .authorizeHttpRequests(registry -> registry
              .requestMatchers("/api/v1/authentication/**")
              .permitAll()
              .anyRequest().authenticated())
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .authenticationManager(authenticationManager)
          .build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // Разрешаем все домены или указываем конкретные
//        configuration.addAllowedOrigin("*"); // Разрешаем доступ с любых доменов (можно заменить на конкретные)
//        configuration.addAllowedMethod("*"); // Разрешаем все HTTP-методы (GET, POST, PUT и т.д.)
//        configuration.addAllowedHeader("*"); // Разрешаем все заголовки
//        configuration.setAllowCredentials(true); // Разрешаем отправку cookies
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration); // Применяем конфигурацию ко всем маршрутам
//
//        return source;
//    }

}
