package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.entities.RevokedToken;
import com.example.barbie_beauty_salon.repositories.RevokedTokenRepository;
import com.example.barbie_beauty_salon.security.JwtTokenProvider;
import com.example.barbie_beauty_salon.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public RevokedTokenService(RevokedTokenRepository revokedTokenRepository, JwtTokenProvider jwtTokenProvider) {
        this.revokedTokenRepository = revokedTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void revokeToken(String token) {
        if (!revokedTokenRepository.existsByToken(token) && jwtTokenProvider.validateToken(token)) {
            RevokedToken revokedToken = new RevokedToken(token, LocalDateTime.now());
            revokedTokenRepository.save(revokedToken);
        } else {
            throw new InvalidTokenException("The token has already been revoked or is invalid");
        }
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }
}
