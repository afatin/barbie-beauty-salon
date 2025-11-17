package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.dto.AppointmentDTO;
import com.example.barbie_beauty_salon.dto.AvailableTimeSlotDTO;
import com.example.barbie_beauty_salon.dto.DTOConverter;
import com.example.barbie_beauty_salon.repositories.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final AppointmentRepository appointmentRepository;
    private final DTOConverter dtoConverter;

    public ScheduleService(AppointmentRepository appointmentRepository,
                           DTOConverter dtoConverter) {
        this.appointmentRepository = appointmentRepository;
        this.dtoConverter = dtoConverter;
    }

    public Map<LocalDate, List<AvailableTimeSlotDTO>> getAvailableTimeSlots(Long masterId,
                                                                            LocalDate startDate,
                                                                            LocalDate endDate) {
        Map<LocalDate, List<AvailableTimeSlotDTO>> result = new LinkedHashMap<>();
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            LocalDate finalDate = date;
            List<AppointmentDTO> appts = appointmentRepository.findByMasterId(masterId).stream()
                    .map(dtoConverter::convertToAppointmentDTO)
                    .filter(a -> a.getDate().isEqual(finalDate))
                    .collect(Collectors.toList());
            result.put(date, calculateSlots(appts));
            date = date.plusDays(1);
        }
        return result;
    }

    private List<AvailableTimeSlotDTO> calculateSlots(List<AppointmentDTO> appointments) {
        LocalTime START = LocalTime.of(9, 0);
        LocalTime END = LocalTime.of(19, 0);
        Duration DURATION = Duration.ofHours(2);
        Duration BREAK = Duration.ofMinutes(30);

        if (appointments.isEmpty()) {
            return generateSlots(START, END, DURATION, BREAK);
        }

        List<AvailableTimeSlotDTO> busy = appointments.stream()
                .map(a -> new AvailableTimeSlotDTO(a.getTime(), a.getTime().plusHours(2)))
                .sorted(Comparator.comparing(AvailableTimeSlotDTO::getStart))
                .toList();

        List<AvailableTimeSlotDTO> free = new ArrayList<>();
        LocalTime current = START;

        for (AvailableTimeSlotDTO b : busy) {
            while (current.plus(DURATION).compareTo(b.getStart()) <= 0 &&
                    current.plus(DURATION).compareTo(END) <= 0) {
                free.add(new AvailableTimeSlotDTO(current, current.plus(DURATION)));
                current = current.plus(DURATION).plus(BREAK);
            }
            current = b.getEnd().plus(BREAK);
        }

        while (current.plus(DURATION).compareTo(END) <= 0) {
            free.add(new AvailableTimeSlotDTO(current, current.plus(DURATION)));
            current = current.plus(DURATION).plus(BREAK);
        }

        return free;
    }

    private List<AvailableTimeSlotDTO> generateSlots(LocalTime start, LocalTime end,
                                                     Duration duration, Duration breakTime) {
        List<AvailableTimeSlotDTO> slots = new ArrayList<>();
        LocalTime current = start;
        while (current.plus(duration).compareTo(end) <= 0) {
            slots.add(new AvailableTimeSlotDTO(current, current.plus(duration)));
            current = current.plus(duration).plus(breakTime);
        }
        return slots;
    }
}
