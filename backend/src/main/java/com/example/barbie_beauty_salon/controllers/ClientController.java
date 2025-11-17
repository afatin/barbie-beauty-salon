package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.AppointmentDTO;
import com.example.barbie_beauty_salon.dto.UpdateClientProfileDTO;
import com.example.barbie_beauty_salon.dto.UserDTO;
import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import com.example.barbie_beauty_salon.security.UserPrincipal;
import com.example.barbie_beauty_salon.services.AppointmentRBACService;
import com.example.barbie_beauty_salon.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@PreAuthorize("hasRole('ROLE_CLIENT')")
public class ClientController {

    private final UserService userService;
    private final AppointmentRBACService appointmentRBACService;

    @Autowired
    public ClientController(UserService userService,
                            AppointmentRBACService appointmentRBACService) {
        this.userService = userService;
        this.appointmentRBACService = appointmentRBACService;
    }

    @GetMapping("/me")
    public UserDTO getMyProfile(Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return userService.getClientById(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    @PutMapping("/me")
    public UserDTO updateMyProfile(@Valid @RequestBody UpdateClientProfileDTO dto, Authentication authentication) {

        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return userService.updateClientFromSelf(userId, dto);
    }

    @PatchMapping("/me/change-password")
    public void changePassword(@RequestBody Map<String, String> passwords, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        userService.changePassword(userId, passwords.get("oldPassword"), passwords.get("newPassword"));
    }

    // READ
    @GetMapping("/appointments")
    public List<AppointmentDTO> getMyAppointments(Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return appointmentRBACService.getOwnAppointmentsAsClient(userId);
    }

    @GetMapping("/appointments/by-status/{status}")
    public List<AppointmentDTO> getMyAppointmentsByStatus(
            @PathVariable String status, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        AppointmentStatus enumStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRBACService.getOwnAppointmentsAsClientByStatus(userId, enumStatus);
    }

    // CREATE
    @PostMapping("/appointments")
    public AppointmentDTO createAppointment(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        Long beautyServiceId = Long.parseLong(payload.get("beautyServiceId"));
        Long masterId = Long.parseLong(payload.get("masterId"));
        LocalDate date = LocalDate.parse(payload.get("date"));
        LocalTime time = LocalTime.parse(payload.get("time"));
        return appointmentRBACService.createForClient(userId, beautyServiceId, masterId, date, time);
    }

    // UPDATE
    @PatchMapping("/appointments/{appointmentId}")
    public void updateMyAppointment(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        appointmentRBACService.updateAsClient(userId, appointmentId, updates);
    }

    @PostMapping("/appointments/{appointmentId}/cancel")
    public void cancelAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        appointmentRBACService.cancelAsClient(userId, appointmentId);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public void deleteMyAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        appointmentRBACService.cancelAsClient(userId, appointmentId);
    }
}
