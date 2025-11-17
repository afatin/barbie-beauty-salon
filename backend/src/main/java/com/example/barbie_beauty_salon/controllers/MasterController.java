package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.AppointmentDTO;
import com.example.barbie_beauty_salon.dto.AvailableTimeSlotDTO;
import com.example.barbie_beauty_salon.dto.UpdateMasterProfileDTO;
import com.example.barbie_beauty_salon.dto.UserDTO;
import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import com.example.barbie_beauty_salon.security.UserPrincipal;
import com.example.barbie_beauty_salon.services.AppointmentRBACService;
import com.example.barbie_beauty_salon.services.ScheduleService;
import com.example.barbie_beauty_salon.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters")
@PreAuthorize("hasRole('ROLE_MASTER')")
public class MasterController {

    private final UserService userService;
    private final ScheduleService scheduleService;
    private final AppointmentRBACService appointmentRBACService;

    @Autowired
    public MasterController(UserService userService,
                            ScheduleService scheduleService,
                            AppointmentRBACService appointmentRBACService) {
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.appointmentRBACService = appointmentRBACService;
    }

    @GetMapping("/me")
    public UserDTO getMyProfile(Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return userService.getMasterById(userId)
                .orElseThrow(() -> new RuntimeException("Master not found"));
    }

    @PutMapping("/me")
    public UserDTO updateMyProfile(@Valid @RequestBody UpdateMasterProfileDTO dto, Authentication authentication) {

        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return userService.updateMasterFromSelf(userId, dto);
    }

    @GetMapping("/appointments")
    public List<AppointmentDTO> getMyAppointments(Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return appointmentRBACService.getOwnAppointmentsAsMaster(userId);
    }

    @GetMapping("/appointments/by-status/{status}")
    public List<AppointmentDTO> getMyAppointmentsByStatus(
            @PathVariable String status, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        AppointmentStatus enumStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return appointmentRBACService.getOwnAppointmentsByStatusAsMaster(userId, enumStatus);
    }

    @PostMapping("/appointments/{appointmentId}/confirm")
    public void confirmAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        appointmentRBACService.confirmAsMaster(userId, appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/cancel")
    public void cancelAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        appointmentRBACService.cancelAsMaster(userId, appointmentId);
    }

    @PostMapping("/appointments/{appointmentId}/complete")
    public void completeAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        appointmentRBACService.completeAsMaster(userId, appointmentId);
    }

    @GetMapping("/schedule")
    public Map<LocalDate, List<AvailableTimeSlotDTO>> getMySchedule(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {
        Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        return scheduleService.getAvailableTimeSlots(userId, startDate, endDate);
    }
}
