package com.example.barbie_beauty_salon.repositories;

import com.example.barbie_beauty_salon.entities.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
    boolean existsByToken(String token);

    void deleteByRevokedAtBefore(LocalDateTime expirationTime);
}
