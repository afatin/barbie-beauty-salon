package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.entities.Appointment;
import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.enums.UserRole;
import com.example.barbie_beauty_salon.repositories.AppointmentRepository;
import com.example.barbie_beauty_salon.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public AuthorizationService(UserRepository userRepository,
                                AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public void assertRole(Long userId, UserRole requiredRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getRole() != requiredRole) {
            throw new SecurityException("User " + userId + " must be " + requiredRole);
        }
    }

    public void assertClientOwnsAppointment(Long clientId, Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (!appt.getClient().getId().equals(clientId)) {
            throw new SecurityException("Client " + clientId + " does not own appointment " + appointmentId);
        }
    }

    public void assertMasterOwnsAppointment(Long masterId, Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (!appt.getMaster().getId().equals(masterId)) {
            throw new SecurityException("Master " + masterId + " does not own appointment " + appointmentId);
        }
    }

    public void assertClientCanManageAppointment(Long userId, Long appointmentId) {
        assertRole(userId, UserRole.ROLE_CLIENT);
        assertClientOwnsAppointment(userId, appointmentId);
    }

    public void assertMasterCanManageAppointment(Long userId, Long appointmentId) {
        assertRole(userId, UserRole.ROLE_MASTER);
        assertMasterOwnsAppointment(userId, appointmentId);
    }

    public void assertAdminOrMasterCanManageAppointment(Long userId, Long appointmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getRole() == UserRole.ROLE_ADMIN) return;
        if (user.getRole() == UserRole.ROLE_MASTER) {
            assertMasterOwnsAppointment(userId, appointmentId);
            return;
        }
        throw new SecurityException("Only admin or master (owner) can manage appointment " + appointmentId);
    }

    public void assertAdminOrSelfCanUpdateUser(Long requesterId, Long targetUserId) {
        if (requesterId.equals(targetUserId)) return;
        assertRole(requesterId, UserRole.ROLE_ADMIN);
    }
}
