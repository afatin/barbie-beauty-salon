package com.example.barbie_beauty_salon.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @Column(name = "token", nullable = false, length = 512, unique = true)
    private String token;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    public RevokedToken() {}

    public RevokedToken(String token, LocalDateTime revokedAt) {
        this.token = token;
        this.revokedAt = revokedAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }
}