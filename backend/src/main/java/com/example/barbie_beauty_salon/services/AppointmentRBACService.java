package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.dto.AppointmentDTO;
import com.example.barbie_beauty_salon.dto.DTOConverter;
import com.example.barbie_beauty_salon.entities.Appointment;
import com.example.barbie_beauty_salon.entities.BeautyService;
import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import com.example.barbie_beauty_salon.enums.UserRole;
import com.example.barbie_beauty_salon.exceptions.ValidationException;
import com.example.barbie_beauty_salon.repositories.AppointmentRepository;
import com.example.barbie_beauty_salon.repositories.BeautyServiceRepository;
import com.example.barbie_beauty_salon.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentRBACService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BeautyServiceRepository beautyServiceRepository;
    private final DTOConverter dtoConverter;
    private final AuthorizationService authService;

    public AppointmentRBACService(AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  BeautyServiceRepository beautyServiceRepository,
                                  DTOConverter dtoConverter,
                                  AuthorizationService authService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.beautyServiceRepository = beautyServiceRepository;
        this.dtoConverter = dtoConverter;
        this.authService = authService;
    }

    public AppointmentDTO createForClient(Long clientId,
                                          Long beautyServiceId,
                                          Long masterId,
                                          LocalDate date,
                                          LocalTime time) {
        authService.assertRole(clientId, UserRole.ROLE_CLIENT);

        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        User master = userRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
        BeautyService service = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));

        if (!master.getBeautyServices().contains(service)) {
            throw new ValidationException("Master does not provide this beauty service");
        }

        Appointment appt = new Appointment();
        appt.setClient(client);
        appt.setMaster(master);
        appt.setBeautyService(service);
        appt.setDate(date);
        appt.setTime(time);
        appt.setStatus(AppointmentStatus.PENDING);

        return dtoConverter.convertToAppointmentDTO(appointmentRepository.save(appt));
    }

    public void updateAsClient(Long clientId, Long appointmentId, Map<String, Object> updates) {
        authService.assertClientCanManageAppointment(clientId, appointmentId);

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (updates.containsKey("status")) {
            String statusStr = (String) updates.get("status");
            if (!"CANCELED".equalsIgnoreCase(statusStr)) {
                throw new SecurityException("Client can only set status to CANCELED");
            }
            appt.setStatus(AppointmentStatus.CANCELED);
            return;
        }

        if (appt.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only pending appointments can be rescheduled");
        }

        if (updates.containsKey("date")) {
            appt.setDate(LocalDate.parse((String) updates.get("date")));
        }
        if (updates.containsKey("time")) {
            appt.setTime(LocalTime.parse((String) updates.get("time")));
        }
    }

    public void cancelAsClient(Long clientId, Long appointmentId) {
        authService.assertClientCanManageAppointment(clientId, appointmentId);
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appt.getStatus() != AppointmentStatus.CANCELED) {
            appt.setStatus(AppointmentStatus.CANCELED);
            appointmentRepository.save(appt);
        }
    }

    public void confirmAsMaster(Long masterId, Long appointmentId) {
        authService.assertMasterCanManageAppointment(masterId, appointmentId);
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appt.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Only pending appointments can be confirmed");
        }
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt);
    }

    public void cancelAsMaster(Long masterId, Long appointmentId) {
        authService.assertMasterCanManageAppointment(masterId, appointmentId);
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appt.getStatus() != AppointmentStatus.CANCELED) {
            appt.setStatus(AppointmentStatus.CANCELED);
            appointmentRepository.save(appt);
        }
    }

    public void completeAsMaster(Long masterId, Long appointmentId) {
        authService.assertMasterCanManageAppointment(masterId, appointmentId);
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appt.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointments can be completed");
        }
        appt.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appt);
    }

    public List<AppointmentDTO> getOwnAppointmentsAsClient(Long clientId) {
        authService.assertRole(clientId, UserRole.ROLE_CLIENT);
        return appointmentRepository.findByClientId(clientId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getOwnAppointmentsAsMaster(Long masterId) {
        authService.assertRole(masterId, UserRole.ROLE_MASTER);
        return appointmentRepository.findByMasterId(masterId).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getOwnAppointmentsAsClientByStatus(Long clientId, AppointmentStatus status) {
        authService.assertRole(clientId, UserRole.ROLE_CLIENT);
        return appointmentRepository.findByClientIdAndStatus(clientId, status).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getOwnAppointmentsByStatusAsMaster(Long masterId, AppointmentStatus status) {
        authService.assertRole(masterId, UserRole.ROLE_MASTER);
        return appointmentRepository.findByMasterIdAndStatus(masterId, status).stream()
                .map(dtoConverter::convertToAppointmentDTO)
                .collect(Collectors.toList());
    }
}
