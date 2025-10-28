package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.AppointmentDTO;
import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import com.example.barbie_beauty_salon.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{appointmentId}")
    public Optional<AppointmentDTO> getAppointmentById(@PathVariable Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping("/client/{clientId}")
    public List<AppointmentDTO> getAppointmentsByClient(@PathVariable Long clientId) {
        return appointmentService.getAppointmentsByClientId(clientId);
    }

    @GetMapping("/master/{masterId}")
    public List<AppointmentDTO> getAppointmentsByMaster(@PathVariable Long masterId) {
        return appointmentService.getAppointmentsByMasterId(masterId);
    }

    @GetMapping("/beautyService/{beautyServiceId}")
    public List<AppointmentDTO> getAppointmentsByBeautyServiceId(@PathVariable Long beautyServiceId) {
        return appointmentService.getAppointmentsByBeautyServiceId(beautyServiceId);
    }

    @GetMapping("/by-dateAndTime")
    public List<AppointmentDTO> getAppointmentsByDateAndTime(@RequestParam LocalDate date, @RequestParam LocalTime time) {
        return appointmentService.getAppointmentsByDateAndTime(date, time);
    }

    @GetMapping("/by-status")
    public List<AppointmentDTO> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    @PostMapping("/create")
    public AppointmentDTO createAppointment(@RequestBody Map<String, String> payload) {
        Long beautyServiceId = Long.parseLong(payload.get("beautyServiceId"));
        Long masterId = Long.parseLong(payload.get("masterId"));
        Long clientId = Long.parseLong(payload.get("clientId"));
        LocalDate date = LocalDate.parse(payload.get("date"));
        LocalTime time = LocalTime.parse(payload.get("time"));
        return appointmentService.createAppointment(beautyServiceId, masterId, clientId, date, time);
    }

    @PatchMapping("/{appointmentId}")
    public void updateAppointment(@PathVariable Long appointmentId, @RequestBody Map<String, Object> updates) {
        appointmentService.updateAppointment(appointmentId, updates);
    }

    @PostMapping("/update-status")
    public void updateAppointmentStatus(@RequestParam Long appointmentId, @RequestParam String status) {
        appointmentService.updateAppointmentStatus(appointmentId, status);
    }

    @DeleteMapping("/{appointmentId}")
    public void deleteAppointment(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
    }
}
