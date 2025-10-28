package com.example.barbie_beauty_salon.dto;

import java.time.LocalTime;

public class AvailableTimeSlotDTO {
    private LocalTime start;
    private LocalTime end;

    public AvailableTimeSlotDTO(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }
    public LocalTime getEnd() {
        return end;
    }
}
