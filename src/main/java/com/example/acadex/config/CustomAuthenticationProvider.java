package com.example.acadex.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println("========================================");
        System.out.println("🔐 Thymeleaf Login Authentication");
        System.out.println("Username: " + username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // HYMELEAF LOGIN: NO EMAIL VERIFICATION CHECK
        // Just check password match
        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("❌ Invalid credentials");
            System.out.println("========================================");
            throw new BadCredentialsException("Invalid username or password");
        }

        System.out.println("✅ Thymeleaf login successful for: " + username);
        System.out.println("========================================");

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}