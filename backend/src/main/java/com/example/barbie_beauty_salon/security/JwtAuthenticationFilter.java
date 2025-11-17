package com.example.barbie_beauty_salon.security;

import com.example.barbie_beauty_salon.dto.ResponseDTO;
import com.example.barbie_beauty_salon.services.RevokedTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenExtractor tokenExtractor;
    private final RevokedTokenService revokedTokenService;

    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            TokenExtractor tokenExtractor,
            RevokedTokenService revokedTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenExtractor = tokenExtractor;
        this.revokedTokenService = revokedTokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (token == null) {
            sendErrorResponse(response, "Unauthorized", "Authorization token is missing.", HttpStatus.UNAUTHORIZED);
            return;
        }

        if (!jwtTokenProvider.validateToken(token)) {
            sendErrorResponse(response, "Unauthorized", "Invalid or expired token.", HttpStatus.UNAUTHORIZED);
            return;
        }

        if (revokedTokenService.isTokenRevoked(token)) {
            sendErrorResponse(response, "Unauthorized", "Token has been revoked.", HttpStatus.UNAUTHORIZED);
            return;
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/catalog");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return tokenExtractor.extractToken(request);
    }

    private void sendErrorResponse(
            HttpServletResponse response,
            String error,
            String message,
            HttpStatus status
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseDTO errorResponse = new ResponseDTO(
                LocalDateTime.now().toString(),
                status.value(),
                message,
                error
        );

        String json = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
