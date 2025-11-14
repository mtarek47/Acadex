package com.example.acadex.repository;



import com.example.acadex.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDtlsRepository extends JpaRepository<UserDtls, Long> {


    // It returns an Optional which means it might find the user or might not
    Optional<UserDtls> findByUsername(String username);

    Optional<UserDtls> findByEmail(String email);

   // Optional<UserDtls> findByVerificationToken(String verificationToken);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
