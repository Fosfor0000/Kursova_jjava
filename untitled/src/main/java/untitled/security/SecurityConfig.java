package untitled.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Дозволяє використовувати анотації @PreAuthorize у контролерах
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Вимкнення CSRF захисту через використання Stateless JWT токенів
                .csrf(AbstractHttpConfigurer::disable)

                // Налаштування правил доступу до ресурсів
                .authorizeHttpRequests(auth -> auth
                        // Доступ до документації Swagger API
                        .requestMatchers(
                                "/v3/api-docs", "/v3/api-docs/**",
                                "/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()

                        // Публічні ендпоінти для реєстрації та входу
                        .requestMatchers("/api/auth/**").permitAll()

                        // Відкритий доступ (тільки для читання) до каталогу товарів, категорій та авторів
                        .requestMatchers(HttpMethod.GET,
                                "/api/books", "/api/books/**",
                                "/api/categories", "/api/categories/**",
                                "/api/authors", "/api/authors/**"
                        ).permitAll()

                        // Будь-які інші запити (зміна даних) вимагають авторизації
                        .anyRequest().authenticated()
                )

                // Використання Stateless режиму (без збереження сесій на сервері)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider)
                // Інтеграція власного фільтра для перевірки JWT перед стандартною перевіркою
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}