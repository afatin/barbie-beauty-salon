package com.example.barbie_beauty_salon.security;

import com.example.barbie_beauty_salon.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY_BASE64 = "1QxkT8o6Bz1gRGylRG3C/qhdL8FvjgIE3wJ3nSC9E2vO7aCZXtkxN03RJ1rU2kYUbA6UQ9e7T4XT8zJghPIuBA==";

    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));

    private static final long JWT_EXPIRATION_MS = 86400000;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("name", userPrincipal.getName())
                .claim("phone", userPrincipal.getPhone())
                .claim("role", userPrincipal.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Long id = claims.get("id", Long.class);
        String login = claims.getSubject();
        String name = claims.get("name", String.class);
        String phone = claims.get("phone", String.class);
        String roleStr = claims.get("role", String.class);
        UserRole role = UserRole.valueOf(roleStr);

        UserPrincipal userPrincipal = new UserPrincipal(id, login, "", name, phone, role);

        var authorities = List.of(new SimpleGrantedAuthority(role.name()));

        return new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities);
    }

    public String getLoginFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
