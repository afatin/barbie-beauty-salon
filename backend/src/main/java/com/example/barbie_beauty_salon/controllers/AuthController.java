package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.LoginRequestDTO;
import com.example.barbie_beauty_salon.dto.RegisterRequestDTO;
import com.example.barbie_beauty_salon.dto.ResponseDTO;
import com.example.barbie_beauty_salon.dto.TokenDTO;
import com.example.barbie_beauty_salon.exceptions.ValidationException;
import com.example.barbie_beauty_salon.security.JwtTokenProvider;
import com.example.barbie_beauty_salon.security.TokenExtractor;
import com.example.barbie_beauty_salon.services.RevokedTokenService;
import com.example.barbie_beauty_salon.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenExtractor tokenExtractor;
    private final AuthenticationManager authenticationManager;
    private final RevokedTokenService revokedTokenService;

    public AuthController(
            UserService userService,
            JwtTokenProvider jwtTokenProvider,
            TokenExtractor tokenExtractor,
            AuthenticationManager authenticationManager,
            RevokedTokenService revokedTokenService
    ) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenExtractor = tokenExtractor;
        this.authenticationManager = authenticationManager;
        this.revokedTokenService = revokedTokenService;
    }

    /**
     * Публичная регистрация — ТОЛЬКО для клиентов
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            userService.registerClient(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CREATED.value(),
                    "Client registered successfully",
                    "Success"
            ));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error",
                    e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CONFLICT.value(),
                    "Registration failed",
                    "User with this login or phone already exists"
            ));
        }
    }

    /**
     * Регистрация для мастера - позже УБРАТЬ
     */
    @PostMapping("/register/master")
    public ResponseEntity<ResponseDTO> registerMaster(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            userService.registerMaster(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CREATED.value(),
                    "Master registered successfully",
                    "Success"
            ));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Validation error",
                    e.getMessage()
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.CONFLICT.value(),
                    "Registration failed",
                    "User with this login or phone already exists"
            ));
        }
    }

    /**
     * Вход для ЛЮБОЙ роли (клиент, мастер, админ)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLogin(),
                            loginRequest.getPassword()
                    )
            );
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new TokenDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(
                    LocalDateTime.now().toString(),
                    HttpStatus.UNAUTHORIZED.value(),
                    "Authentication failed",
                    "Invalid login or password"
            ));
        }
    }

    /**
     * Выход — отзываем токен
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(HttpServletRequest request) {
        String token = tokenExtractor.extractToken(request);

        revokedTokenService.revokeToken(token);

        return ResponseEntity.ok(new ResponseDTO(
                LocalDateTime.now().toString(),
                HttpStatus.OK.value(),
                "Logout successful",
                "Success"
        ));
    }
}
