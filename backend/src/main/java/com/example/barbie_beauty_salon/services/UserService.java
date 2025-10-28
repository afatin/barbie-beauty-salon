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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOConverter dtoConverter;

    public UserService(UserRepository userRepository,
                       AppointmentRepository appointmentRepository,
                       BeautyServiceRepository beautyServiceRepository,
                       PasswordEncoder passwordEncoder,
                       DTOConverter dtoConverter) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
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

    public Optional<UserDTO> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone).map(dtoConverter::convertToUserDTO);
    }

    public Optional<UserDTO> getUserByLogin(String login) {
        return userRepository.findByLogin(login).map(dtoConverter::convertToUserDTO);
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

    /* ========== КЛИЕНТСКИЕ МЕТОДЫ ========== */

    public List<UserDTO> getAllClients() {
        return userRepository.findByRole(UserRole.ROLE_CLIENT).stream()
                .map(dtoConverter::convertToUserDTO)
                .toList();
    }

    public Optional<UserDTO> getClientById(Long clientId) {
        return userRepository.findById(clientId)
                .filter(u -> u.getRole() == UserRole.ROLE_CLIENT)
                .map(dtoConverter::convertToUserDTO);
    }

    public Optional<UserDTO> getClientByName(String name) {
        return userRepository.findByName(name)
                .filter(u -> u.getRole() == UserRole.ROLE_CLIENT)
                .map(dtoConverter::convertToUserDTO);
    }

    public Optional<UserDTO> getClientByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .filter(u -> u.getRole() == UserRole.ROLE_CLIENT)
                .map(dtoConverter::convertToUserDTO);
    }

    public Optional<UserDTO> getClientByLogin(String login) {
        return userRepository.findByLogin(login)
                .filter(u -> u.getRole() == UserRole.ROLE_CLIENT)
                .map(dtoConverter::convertToUserDTO);
    }

    public Optional<UserDTO> getClientByAppointmentId(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(Appointment::getClient)
                .map(dtoConverter::convertToUserDTO);
    }

    @Transactional
    public UserDTO registerClient(RegisterRequestDTO request) {
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
        user.setRole(UserRole.ROLE_CLIENT);

        return saveUser(user);
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

    public Optional<UserDTO> getMasterByLogin(String login) {
        return userRepository.findByLogin(login)
                .filter(u -> u.getRole() == UserRole.ROLE_MASTER)
                .map(dtoConverter::convertToUserDTO);
    }

    public Optional<UserDTO> getMasterByAppointmentId(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(Appointment::getMaster)
                .map(dtoConverter::convertToUserDTO);
    }

    public List<UserDTO> getMastersByBeautyServiceId(Long beautyServiceId) {
        return beautyServiceRepository.findById(beautyServiceId)
                .map(service -> service.getMasters().stream()
                        .map(dtoConverter::convertToUserDTO)
                        .toList()
                )
                .orElse(Collections.emptyList());
    }

    @Transactional
    public UserDTO registerMaster(RegisterRequestDTO request) {
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
        user.setRole(UserRole.ROLE_MASTER);

        return saveUser(user);
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

    public Map<LocalDate, List<AvailableTimeSlotDTO>> getAvailableTimeSlots(Long masterId,
                                                                            LocalDate startDate,
                                                                            LocalDate endDate) {
        Map<LocalDate, List<AvailableTimeSlotDTO>> availableSlotsByDate = new LinkedHashMap<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            LocalDate finalDate = currentDate;
            List<AppointmentDTO> appointments = appointmentRepository.findByMasterId(masterId).stream()
                    .map(dtoConverter::convertToAppointmentDTO)
                    .filter(a -> a.getDate().isEqual(finalDate))
                    .collect(Collectors.toList());

            List<AvailableTimeSlotDTO> availableSlots = calculateAvailableSlots(appointments);
            availableSlotsByDate.put(currentDate, availableSlots);
            currentDate = currentDate.plusDays(1);
        }

        return availableSlotsByDate;
    }

    private List<AvailableTimeSlotDTO> calculateAvailableSlots(List<AppointmentDTO> appointments) {
        LocalTime startOfDay = LocalTime.of(9, 0);
        LocalTime endOfDay = LocalTime.of(19, 0);
        Duration beautyServiceDuration = Duration.ofHours(2);
        Duration breakDuration = Duration.ofMinutes(30);
        List<AvailableTimeSlotDTO> availableTimeSlots = new ArrayList<>();

        if (appointments.isEmpty()) {
            LocalTime currentStart = startOfDay;
            while (currentStart.plus(beautyServiceDuration).isBefore(endOfDay) ||
                    currentStart.plus(beautyServiceDuration).equals(endOfDay)) {
                LocalTime currentEnd = currentStart.plus(beautyServiceDuration);
                availableTimeSlots.add(new AvailableTimeSlotDTO(currentStart, currentEnd));
                currentStart = currentEnd.plus(breakDuration);
            }
            return availableTimeSlots;
        }

        List<AvailableTimeSlotDTO> busyIntervals = appointments.stream()
                .map(a -> new AvailableTimeSlotDTO(a.getTime(), a.getTime().plusHours(2)))
                .sorted(Comparator.comparing(AvailableTimeSlotDTO::getStart))
                .toList();

        LocalTime currentStart = startOfDay;
        for (AvailableTimeSlotDTO busy : busyIntervals) {
            while (currentStart.plus(beautyServiceDuration).isBefore(busy.getStart()) ||
                    currentStart.plus(beautyServiceDuration).equals(busy.getStart())) {
                LocalTime currentEnd = currentStart.plus(beautyServiceDuration);
                availableTimeSlots.add(new AvailableTimeSlotDTO(currentStart, currentEnd));
                currentStart = currentEnd.plus(breakDuration);
            }
            currentStart = busy.getEnd().plus(breakDuration);
        }

        while (currentStart.plus(beautyServiceDuration).isBefore(endOfDay) ||
                currentStart.plus(beautyServiceDuration).equals(endOfDay)) {
            LocalTime currentEnd = currentStart.plus(beautyServiceDuration);
            availableTimeSlots.add(new AvailableTimeSlotDTO(currentStart, currentEnd));
            currentStart = currentEnd.plus(breakDuration);
        }

        return availableTimeSlots;
    }
}
