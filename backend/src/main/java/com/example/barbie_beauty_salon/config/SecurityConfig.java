package com.example.barbie_beauty_salon.config;

import com.example.barbie_beauty_salon.security.JwtAuthenticationFilter;
import com.example.barbie_beauty_salon.security.JwtTokenProvider;
import com.example.barbie_beauty_salon.security.MyUserDetailsService;
import com.example.barbie_beauty_salon.security.TokenExtractor;
import com.example.barbie_beauty_salon.services.RevokedTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenExtractor tokenExtractor;
    private final MyUserDetailsService userDetailsService;
    private final RevokedTokenService revokedTokenService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider,
                          TokenExtractor tokenExtractor,
                          MyUserDetailsService userDetailsService,
                          RevokedTokenService revokedTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenExtractor = tokenExtractor;
        this.userDetailsService = userDetailsService;
        this.revokedTokenService = revokedTokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(auth -> auth
                        // Публичные эндпоинты (без токена)
                        .requestMatchers(
                                "/",
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()

                        // RBAC: строгий контроль по ролям
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/masters/**").hasRole("MASTER")
                        .requestMatchers("/api/clients/**").hasRole("CLIENT")

                        // Публичный каталог (только чтение)
                        .requestMatchers("/api/catalog/**").permitAll()

                        // Всё остальное — запрещено
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, tokenExtractor, revokedTokenService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
