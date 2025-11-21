package com.example.acadex.config;


import com.example.acadex.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> customUserDetailsService.loadUserByUsername(username);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:5173",
                            "http://10.190.69.5173"
                    ));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true); // Important for session cookies
                    config.setExposedHeaders(List.of("Set-Cookie")); // Expose cookie header
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ✅ Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()

                        // ✅ Public auth endpoints
                        .requestMatchers("/login", "/auth/register", "/auth/react-login",
                                "/auth/verify", "/auth/resend-verification",
                                "/auth/captcha").permitAll()

                        // OTP endpoints permit করুন
                        .requestMatchers("/auth/otp/**").permitAll()

                        // Role-based access
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")

                        // Employee endpoints - Now requires authentication
                        .requestMatchers(HttpMethod.POST, "/employee/save").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/employee/update").authenticated()
                        .requestMatchers("/employee/**").permitAll()

                        // Other public endpoints
                        .requestMatchers("/circulars/**", "/uploads/**", "/address/**").permitAll()
                        .requestMatchers("/api/**").permitAll()

                        .requestMatchers("/").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/auth/login")  // ✅ Thymeleaf login - NO CAPTCHA, NO EMAIL CHECK
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/dashboard/index");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/login?error=true");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            boolean isJson = request.getHeader("Accept") != null &&
                                    request.getHeader("Accept").contains("application/json");
                            if (isJson) {
                                response.setStatus(HttpServletResponse.SC_OK);
                            } else {
                                response.sendRedirect("/login?logout=true");
                            }
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        return new ProviderManager(List.of(new CustomAuthenticationProvider(userDetailsService, passwordEncoder())));
    }
}