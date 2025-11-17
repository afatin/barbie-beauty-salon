package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.*;
import com.example.barbie_beauty_salon.entities.BeautyService;
import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.exceptions.ValidationException;
import com.example.barbie_beauty_salon.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final BeautyServiceService beautyServiceService;
    private final AppointmentService appointmentService;
    private final UserRegistrationService userRegistrationService;

    @Autowired
    public AdminController(UserService userService,
                           BeautyServiceService beautyServiceService,
                           AppointmentService appointmentService,
                           UserRegistrationService userRegistrationService) {
        this.userService = userService;
        this.beautyServiceService = beautyServiceService;
        this.appointmentService = appointmentService;
        this.userRegistrationService = userRegistrationService;
    }

    // USERS
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) {
        if (user.getRole() == null) {
            throw new ValidationException("Role is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ValidationException("Password is required");
        }

        UserDTO created;
        switch (user.getRole()) {
            case ROLE_CLIENT -> created = userRegistrationService.registerClient(toRegisterRequest(user));
            case ROLE_MASTER -> created = userRegistrationService.registerMaster(toRegisterRequest(user));
            case ROLE_ADMIN -> created = userService.registerAdmin(toRegisterRequest(user));
            default -> throw new ValidationException("Unsupported role: " + user.getRole());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private RegisterRequestDTO toRegisterRequest(User user) {
        RegisterRequestDTO r = new RegisterRequestDTO();
        r.setLogin(user.getLogin());
        r.setPassword(user.getPassword());
        r.setName(user.getName());
        r.setPhone(user.getPhone());
        return r;
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(
                userService.updateUser(id, updates)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // MASTERS (алиас)
    @GetMapping("/masters")
    public List<UserDTO> getAllMasters() {
        return userService.getAllMasters();
    }

    // BEAUTY SERVICES
    @GetMapping("/beauty-services")
    public List<BeautyServiceDTO> getAllBeautyServices() {
        return beautyServiceService.getAllBeautyServices();
    }

    @GetMapping("/beauty-services/{id}")
    public ResponseEntity<BeautyServiceDTO> getBeautyServiceById(@PathVariable Long id) {
        return beautyServiceService.getBeautyServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/beauty-services")
    public ResponseEntity<BeautyServiceDTO> createBeautyService(@Valid @RequestBody BeautyService service) {
        BeautyServiceDTO created = beautyServiceService.createBeautyService(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/beauty-services/{id}")
    public ResponseEntity<BeautyServiceDTO> updateBeautyService(
            @PathVariable Long id, @RequestBody BeautyService updates) {
        return ResponseEntity.ok(
                beautyServiceService.updateBeautyServiceFromAdmin(id, updates)
        );
    }

    @DeleteMapping("/beauty-services/{id}")
    public ResponseEntity<Void> deleteBeautyService(@PathVariable Long id) {
        beautyServiceService.deleteBeautyService(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/masters/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<UserDTO> assignBeautyServiceToMaster(
            @PathVariable Long masterId,
            @PathVariable Long beautyServiceId) {
        UserDTO updated = userService.addBeautyServiceToMaster(masterId, beautyServiceId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/masters/{masterId}/beauty-services/{beautyServiceId}")
    public ResponseEntity<UserDTO> removeBeautyServiceFromMaster(
            @PathVariable Long masterId,
            @PathVariable Long beautyServiceId) {
        UserDTO updated = userService.removeBeautyServiceFromMaster(masterId, beautyServiceId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/appointments")
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/appointments/{id}")
    public Optional<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
