package untitled.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import untitled.repositories.CustomerRepository;

/**
 * Глобальна конфігурація бінів (Beans) для підсистеми автентифікації Spring Security.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final CustomerRepository customerRepository;

    /**
     * Перевизначення стандартного сервісу пошуку користувачів.
     * Замість стандартної таблиці users ми використовуємо нашу таблицю customers та пошук за email.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> customerRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Користувача не знайдено"));
    }

    /**
     * Провайдер автентифікації, який з'єднує UserDetailsService та PasswordEncoder.
     * Використовується Spring Security для перевірки логіна та пароля при вході.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Менеджер автентифікації. Делегує процес перевірки налаштованому AuthenticationProvider.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Налаштування алгоритму хешування паролів (BCrypt).
     * Забезпечує незворотне шифрування паролів перед збереженням у базу даних.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}