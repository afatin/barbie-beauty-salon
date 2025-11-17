package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.dto.AppointmentDTO;
import com.example.barbie_beauty_salon.dto.DTOConverter;
import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import com.example.barbie_beauty_salon.repositories.AppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DTOConverter dtoConverter;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DTOConverter dtoConverter
    ) {
        this.appointmentRepository = appointmentRepository;
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

    @Transactional
    public void deleteAppointment(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new EntityNotFoundException("Appointment not found");
        }
        appointmentRepository.deleteById(appointmentId);
    }
}
