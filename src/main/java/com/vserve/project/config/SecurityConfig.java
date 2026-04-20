package com.vserve.project.config;

import com.vserve.project.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
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

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/public/**", "/api/enums/**", "/api/countries/**", "/swagger-ui/**", "v3/api-docs", "/swagger-ui.html").permitAll()
                                .requestMatchers(
                                        "api/organization/participations/approve","api/organization/participations/reject","api/organization/participations/attendance","api/organization/participations/service/**",
                                        "api/user/participations/approve","api/user/participations/reject" ,"api/user/participations/service/**","api/user/participations/attendance"
                                ).hasAnyRole("USER","BOTH", "ORGANIZATION")
                        .requestMatchers("/api/organizations/profile/**")
                        .hasAnyRole("USER", "VOLUNTEER", "BOTH", "ORGANIZATION_MEMBER", "ORGANIZATION", "ADMIN")
                        .requestMatchers("/api/user/**", "/api/users/**")
                        .hasAnyRole("USER", "VOLUNTEER", "BOTH",
                                "ORGANIZATION_MEMBER", "ADMIN")
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")
//                        .requestMatchers("/api/profile/**")
//                        .hasAnyRole("USER", "VOLUNTEER", "BOTH", "ORGANIZATION_MEMBER", "ADMIN")
                        .requestMatchers("/api/history/user/**").hasAnyRole("USER", "VOLUNTEER", "BOTH", "ORGANIZATION_MEMBER", "ADMIN")
                        .requestMatchers("/api/history/organization/**").hasAnyRole("ORGANIZATION", "ADMIN","USER", "VOLUNTEER", "BOTH", "ORGANIZATION_MEMBER")
                        .requestMatchers("/api/requests/**")
                        .hasAnyRole("USER", "BOTH", "VOLUNTEER", "ORGANIZATION_MEMBER", "ADMIN", "ORGANIZATION")
                        .requestMatchers("/api/organization/**", "/api/organizations/**")
                        .hasAnyRole("ORGANIZATION", "ADMIN")




                        .anyRequest().authenticated()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((req, res, ex) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedHandler((req, res, ex) ->
                                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}