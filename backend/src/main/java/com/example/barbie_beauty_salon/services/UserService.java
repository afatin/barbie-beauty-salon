package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.dto.*;
import com.example.barbie_beauty_salon.entities.*;
import com.example.barbie_beauty_salon.enums.UserRole;
import com.example.barbie_beauty_salon.exceptions.ValidationException;
import com.example.barbie_beauty_salon.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOConverter dtoConverter;

    public UserService(UserRepository userRepository,
                       BeautyServiceRepository beautyServiceRepository,
                       PasswordEncoder passwordEncoder,
                       DTOConverter dtoConverter) {
        this.userRepository = userRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoConverter = dtoConverter;
    }

    /* ========== ОБЩИЕ МЕТОДЫ ========== */

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(dtoConverter::convertToUserDTO)
                .toList();
    }

    public Optional<UserDTO> getUserById(Long userId) {
        return userRepository.findById(userId).map(dtoConverter::convertToUserDTO);
    }

    @Transactional
    public UserDTO saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return dtoConverter.convertToUserDTO(userRepository.save(user));
    }

    @Transactional
    public Optional<UserDTO> updateUser(Long userId, Map<String, Object> updates) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (updates.containsKey("name"))
                        user.setName((String) updates.get("name"));
                    if (updates.containsKey("phone"))
                        user.setPhone((String) updates.get("phone"));
                    if (updates.containsKey("login"))
                        user.setLogin((String) updates.get("login"));
                    return dtoConverter.convertToUserDTO(userRepository.save(user));
                });
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Wrong current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // === АДМИН-РЕГИСТРАЦИЯ (ТОЛЬКО ЧЕРЕЗ АДМИНА!) ===
    @Transactional
    public UserDTO registerAdmin(RegisterRequestDTO request) {
        long adminCount = userRepository.findByRole(UserRole.ROLE_ADMIN).size();
        if (adminCount > 1) {
            throw new SecurityException("Only up to 2 admins allowed. Use update instead.");
        }

        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new ValidationException("Login is already taken");
        }
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ValidationException("Phone number is already registered");
        }

        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.ROLE_ADMIN);

        return saveUser(user);
    }

    /* ========== КЛИЕНТСКИЕ МЕТОДЫ ========== */

    public Optional<UserDTO> getClientById(Long clientId) {
        return userRepository.findById(clientId)
                .filter(u -> u.getRole() == UserRole.ROLE_CLIENT)
                .map(dtoConverter::convertToUserDTO);
    }

    @Transactional
    public UserDTO updateClientFromSelf(Long clientId, UpdateClientProfileDTO dto) {
        return userRepository.findById(clientId)
                .filter(u -> u.getRole() == UserRole.ROLE_CLIENT)
                .map(user -> {
                    user.setName(dto.getName());
                    user.setPhone(dto.getPhone());
                    return dtoConverter.convertToUserDTO(userRepository.save(user));
                })
                .orElseThrow(() -> new SecurityException("Client not found"));
    }

    /* ========== МАСТЕРСКИЕ МЕТОДЫ ========== */

    public List<UserDTO> getAllMasters() {
        return userRepository.findByRole(UserRole.ROLE_MASTER).stream()
                .map(dtoConverter::convertToUserDTO)
                .toList();
    }

    public Optional<UserDTO> getMasterById(Long masterId) {
        return userRepository.findById(masterId)
                .filter(u -> u.getRole() == UserRole.ROLE_MASTER)
                .map(dtoConverter::convertToUserDTO);
    }

    public Optional<UserDTO> getMasterByName(String name) {
        return userRepository.findByName(name)
                .filter(u -> u.getRole() == UserRole.ROLE_MASTER)
                .map(dtoConverter::convertToUserDTO);
    }

    @Transactional
    public UserDTO updateMasterFromSelf(Long masterId, UpdateMasterProfileDTO dto) {
        return userRepository.findById(masterId)
                .filter(u -> u.getRole() == UserRole.ROLE_MASTER)
                .map(user -> {
                    user.setName(dto.getName());
                    user.setPhone(dto.getPhone());
                    return dtoConverter.convertToUserDTO(userRepository.save(user));
                })
                .orElseThrow(() -> new SecurityException("Master not found"));
    }

    @Transactional
    public UserDTO addBeautyServiceToMaster(Long masterId, Long beautyServiceId) {
        User master = userRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        if (master.getBeautyServices().contains(beautyService)) {
            throw new IllegalStateException("Service already assigned");
        }

        master.addBeautyService(beautyService);
        return dtoConverter.convertToUserDTO(userRepository.save(master));
    }

    @Transactional
    public UserDTO removeBeautyServiceFromMaster(Long masterId, Long beautyServiceId) {
        User master = userRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));

        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        master.removeBeautyService(beautyService);
        return dtoConverter.convertToUserDTO(userRepository.save(master));
    }
}
