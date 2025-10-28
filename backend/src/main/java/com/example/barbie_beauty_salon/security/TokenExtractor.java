package com.example.barbie_beauty_salon.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {

    public String extractToken(HttpServletRequest request) {
        // ТА ЖЕ ЛОГИКА, что в getTokenFromRequest()
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.trim().isEmpty()) {
                        return value;
                    }
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }

        return null;
    }
}
