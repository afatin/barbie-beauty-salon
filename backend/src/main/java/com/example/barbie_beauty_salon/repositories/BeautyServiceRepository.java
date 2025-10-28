package com.example.barbie_beauty_salon.repositories;

import com.example.barbie_beauty_salon.entities.BeautyService;
import com.example.barbie_beauty_salon.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, Long> {

    Optional<BeautyService> findByName(String name);

    List<BeautyService> findByPriceBetween(double minPrice, double maxPrice);

    List<BeautyService> findByMastersId(Long masterId);

    List<BeautyService> findByMastersIn(List<User> masters);
}
