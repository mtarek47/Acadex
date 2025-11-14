package com.example.acadex.serviceImpl;


import com.example.acadex.model.UserDtls;
import com.example.acadex.repository.UserDtlsRepository;
import com.example.acadex.service.CustomUserDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService, UserDetailsService {

    private final UserDtlsRepository userDtlsRepository;

    public CustomUserDetailsServiceImpl(UserDtlsRepository userDtlsRepository) {
        this.userDtlsRepository = userDtlsRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Look for the user in the database by username
        UserDtls user = userDtlsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert user role to GrantedAuthority
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );

        // Return a Spring Security User object with username, password, active status, and role info
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)  // Now authorities is defined
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())  // Email verification check
                .build();
    }
}