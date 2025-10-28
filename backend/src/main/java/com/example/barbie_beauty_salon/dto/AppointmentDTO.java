package com.example.barbie_beauty_salon.dto;

import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDTO {

    private Long id;
    private UserDTO client;
    private UserDTO master;
    private BeautyServiceDTO beautyService;
    private LocalDate date;
    private LocalTime time;
    private AppointmentStatus status;

    public AppointmentDTO(Long id, UserDTO client, UserDTO master,
                          BeautyServiceDTO beautyService, LocalDate date,
                          LocalTime time, AppointmentStatus status) {
        this.id = id;
        this.client = client;
        this.master = master;
        this.beautyService = beautyService;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getClient() { return client; }
    public void setClient(UserDTO client) { this.client = client; }

    public UserDTO getMaster() { return master; }
    public void setMaster(UserDTO master) { this.master = master; }

    public BeautyServiceDTO getBeautyService() { return beautyService; }
    public void setBeautyService(BeautyServiceDTO beautyService) { this.beautyService = beautyService; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
}
