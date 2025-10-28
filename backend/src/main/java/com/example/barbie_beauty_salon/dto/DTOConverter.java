package com.example.barbie_beauty_salon.dto;

import com.example.barbie_beauty_salon.entities.Appointment;
import com.example.barbie_beauty_salon.entities.BeautyService;
import com.example.barbie_beauty_salon.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DTOConverter {

    public UserDTO convertToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getRole().name(),
                user.getBeautyServices() != null
                        ? user.getBeautyServices().stream()
                        .map(this::convertToBeautyServiceDTO)
                        .toList()
                        : List.<BeautyServiceDTO>of()
        );
    }

    public BeautyServiceDTO convertToBeautyServiceDTO(BeautyService beautyService) {
        List<UserDTO> masterDTOs = beautyService.getMasters() != null
                ? beautyService.getMasters().stream()
                .map(master -> new UserDTO(
                        master.getId(),
                        master.getName(),
                        master.getPhone(),
                        master.getRole().name(),
                        null
                ))
                .toList()
                : List.<UserDTO>of();

        return new BeautyServiceDTO(
                beautyService.getId(),
                beautyService.getName(),
                beautyService.getPrice(),
                beautyService.getDescription(),
                masterDTOs
        );
    }

    public AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        UserDTO clientDTO = convertToUserDTO(appointment.getClient());
        UserDTO masterDTO = convertToUserDTO(appointment.getMaster());
        BeautyServiceDTO beautyServiceDTO = convertToBeautyServiceDTO(appointment.getBeautyService());

        return new AppointmentDTO(
                appointment.getId(),
                clientDTO,
                masterDTO,
                beautyServiceDTO,
                appointment.getDate(),
                appointment.getTime(),
                appointment.getStatus()
        );
    }
}
