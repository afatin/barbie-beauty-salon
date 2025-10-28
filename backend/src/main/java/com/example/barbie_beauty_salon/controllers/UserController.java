package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.*;
import com.example.barbie_beauty_salon.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* ========== ОБЩИЕ CRUD МЕТОДЫ ========== */

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/by-phone")
    public Optional<UserDTO> getUserByPhone(@RequestParam String phone) {
        return userService.getUserByPhone(phone);
    }

    @GetMapping("/by-login")
    public Optional<UserDTO> getUserByLogin(@RequestParam String login) {
        return userService.getUserByLogin(login);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        userService.updateUser(id, updates);
    }

    @PatchMapping("/{id}/change-password")
    public void changePassword(@PathVariable Long id,
                               @RequestBody Map<String, String> passwords) {
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        userService.changePassword(id, oldPassword, newPassword);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    /* ========== КЛИЕНТСКИЕ МЕТОДЫ ========== */

    @GetMapping("/clients")
    public List<UserDTO> getAllClients() {
        return userService.getAllClients();
    }

    @GetMapping("/clients/{id}")
    public Optional<UserDTO> getClientById(@PathVariable Long id) {
        return userService.getClientById(id);
    }

    @GetMapping("/clients/by-name")
    public Optional<UserDTO> getClientByName(@RequestParam String name) {
        return userService.getClientByName(name);
    }

    @GetMapping("/clients/by-phone")
    public Optional<UserDTO> getClientByPhone(@RequestParam String phone) {
        return userService.getClientByPhone(phone);
    }

    @GetMapping("/clients/by-login")
    public Optional<UserDTO> getClientByLogin(@RequestParam String login) {
        return userService.getClientByLogin(login);
    }

    @GetMapping("/clients/by-appointment/{appointmentId}")
    public Optional<UserDTO> getClientByAppointmentId(@PathVariable Long appointmentId) {
        return userService.getClientByAppointmentId(appointmentId);
    }

    /* ========== МАСТЕРСКИЕ МЕТОДЫ ========== */

    @GetMapping("/masters")
    public List<UserDTO> getAllMasters() {
        return userService.getAllMasters();
    }

    @GetMapping("/masters/{id}")
    public Optional<UserDTO> getMasterById(@PathVariable Long id) {
        return userService.getMasterById(id);
    }

    @GetMapping("/masters/by-name")
    public Optional<UserDTO> getMasterByName(@RequestParam String name) {
        return userService.getMasterByName(name);
    }

    @GetMapping("/masters/by-login")
    public Optional<UserDTO> getMasterByLogin(@RequestParam String login) {
        return userService.getMasterByLogin(login);
    }

    @GetMapping("/masters/by-appointment/{appointmentId}")
    public Optional<UserDTO> getMasterByAppointmentId(@PathVariable Long appointmentId) {
        return userService.getMasterByAppointmentId(appointmentId);
    }

    @GetMapping("/masters/by-beauty-service/{beautyServiceId}")
    public List<UserDTO> getMastersByBeautyServiceId(@PathVariable Long beautyServiceId) {
        return userService.getMastersByBeautyServiceId(beautyServiceId);
    }

    @PostMapping("/masters/{masterId}/beauty-services/{beautyServiceId}")
    public UserDTO addBeautyServiceToMaster(@PathVariable Long masterId,
                                            @PathVariable Long beautyServiceId) {
        return userService.addBeautyServiceToMaster(masterId, beautyServiceId);
    }

    @DeleteMapping("/masters/{masterId}/beauty-services/{beautyServiceId}")
    public UserDTO removeBeautyServiceFromMaster(@PathVariable Long masterId,
                                                 @PathVariable Long beautyServiceId) {
        return userService.removeBeautyServiceFromMaster(masterId, beautyServiceId);
    }

    @GetMapping("/masters/{masterId}/available-time-slots")
    public Map<LocalDate, List<AvailableTimeSlotDTO>> getAvailableTimeSlots(
            @PathVariable Long masterId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return userService.getAvailableTimeSlots(masterId, startDate, endDate);
    }
}
