package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.dto.AppointmentDTO;
import com.example.barbie_beauty_salon.dto.DTOConverter;
import com.example.barbie_beauty_salon.entities.Appointment;
import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.entities.BeautyService;
import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import com.example.barbie_beauty_salon.repositories.AppointmentRepository;
import com.example.barbie_beauty_salon.repositories.UserRepository;
import com.example.barbie_beauty_salon.repositories.BeautyServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final DTOConverter dtoConverter;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            BeautyServiceRepository beautyServiceRepository,
            DTOConverter dtoConverter
    ) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.dtoConverter = dtoConverter;
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public Optional<AppointmentDTO> getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(dtoConverter::convertToAppointmentDTO);
    }

    public List<AppointmentDTO> getAppointmentsByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByMasterId(Long masterId) {
        return appointmentRepository.findByMasterId(masterId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByBeautyServiceId(Long beautyServiceId) {
        return appointmentRepository.findByBeautyServiceId(beautyServiceId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByDateAndTime(LocalDate date, LocalTime time) {
        return appointmentRepository.findByDateAndTime(date, time).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .toList();
    }

    @Transactional
    public AppointmentDTO createAppointment(Long beautyServiceId, Long masterId, Long clientId, LocalDate date, LocalTime time) {
        Appointment appointment = new Appointment();

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        User master = userRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService service = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty Service not found"));

        appointment.setClient(client);
        appointment.setMaster(master);
        appointment.setBeautyService(service);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setStatus(AppointmentStatus.PENDING);

        return dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public Optional<AppointmentDTO> updateAppointment(Long appointmentId, Map<String, Object> updates) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (updates.containsKey("clientId")) {
            Long clientId = ((Number) updates.get("clientId")).longValue();
            userRepository.findById(clientId).ifPresent(appointment::setClient);
        }
        if (updates.containsKey("masterId")) {
            Long masterId = ((Number) updates.get("masterId")).longValue();
            userRepository.findById(masterId).ifPresent(appointment::setMaster);
        }
        if (updates.containsKey("beautyServiceId")) {
            Long serviceId = ((Number) updates.get("beautyServiceId")).longValue();
            beautyServiceRepository.findById(serviceId).ifPresent(appointment::setBeautyService);
        }
        if (updates.containsKey("date")) {
            appointment.setDate(LocalDate.parse((String) updates.get("date")));
        }
        if (updates.containsKey("time")) {
            appointment.setTime(LocalTime.parse((String) updates.get("time")));
        }
        if (updates.containsKey("status")) {
            appointment.setStatus(AppointmentStatus.valueOf((String) updates.get("status")));
        }

        return Optional.of(dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appointment)));
    }

    @Transactional
    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        appointment.setStatus(AppointmentStatus.valueOf(status));
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new EntityNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(appointmentId);
    }
}
