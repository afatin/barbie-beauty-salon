package com.example.barbie_beauty_salon.repositories;

import com.example.barbie_beauty_salon.entities.Appointment;
import com.example.barbie_beauty_salon.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByClientId(Long clientId);

    List<Appointment> findByMasterId(Long masterId);

    List<Appointment> findByBeautyServiceId(Long beautyServiceId);

    List<Appointment> findByDateAndTime(LocalDate date, LocalTime time);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByMasterIdAndStatus(Long masterId, AppointmentStatus status);
}
