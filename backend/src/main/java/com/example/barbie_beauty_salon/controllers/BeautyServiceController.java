package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.BeautyServiceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.barbie_beauty_salon.entities.BeautyService;
import com.example.barbie_beauty_salon.services.BeautyServiceService;

@RestController
@RequestMapping("/api/beauty-services")
public class BeautyServiceController {

    private final BeautyServiceService beautyServiceService;

    @Autowired
    public BeautyServiceController(BeautyServiceService beautyServiceService) {
        this.beautyServiceService = beautyServiceService;
    }

    @GetMapping
    public List<BeautyServiceDTO> getAllBeautyServices() {
        return beautyServiceService.getAllBeautyServices();
    }

    @GetMapping("/{beautyServiceId}")
    public Optional<BeautyServiceDTO> getBeautyServiceById(@PathVariable Long beautyServiceId) {
        return beautyServiceService.getBeautyServiceById(beautyServiceId);
    }

    @GetMapping("/by-name")
    public Optional<BeautyServiceDTO> getBeautyServiceByName(@RequestParam String name) {
        return beautyServiceService.getBeautyServiceByName(name);
    }

    @GetMapping("/price-range")
    public List<BeautyServiceDTO> getBeautyServiceByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice) {
        return beautyServiceService.getBeautyServicesByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/by-masterId")
    public List<BeautyServiceDTO> getBeautyServicesByMasterId(@RequestParam Long masterId) {
        return beautyServiceService.getBeautyServicesByMasterId(masterId);
    }

    @PostMapping("/create")
    public BeautyServiceDTO createBeautyService(@RequestBody BeautyService beautyService) {
        return beautyServiceService.createBeautyService(beautyService);
    }

    @PatchMapping("/{beautyServiceId}")
    public void updateBeautyService(@PathVariable Long beautyServiceId, @RequestBody Map<String, Object> updates) {
        beautyServiceService.updateBeautyService(beautyServiceId, updates);
    }

    @DeleteMapping("/{beautyServiceId}")
    public void deleteBeautyService(@PathVariable Long beautyServiceId) {
        beautyServiceService.deleteBeautyService(beautyServiceId);
    }
}
