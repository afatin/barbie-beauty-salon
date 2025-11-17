package com.example.barbie_beauty_salon.controllers;

import com.example.barbie_beauty_salon.dto.UserDTO;
import com.example.barbie_beauty_salon.dto.BeautyServiceDTO;
import com.example.barbie_beauty_salon.services.UserService;
import com.example.barbie_beauty_salon.services.BeautyServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final UserService userService;
    private final BeautyServiceService beautyServiceService;

    @Autowired
    public CatalogController(UserService userService,
                                BeautyServiceService beautyServiceService) {
        this.userService = userService;
        this.beautyServiceService = beautyServiceService;
    }

    @GetMapping("/masters")
    public List<UserDTO> getAllMasters() {
        return userService.getAllMasters();
    }

    @GetMapping("/masters/{id}")
    public UserDTO getMasterById(@PathVariable Long id) {
        return userService.getMasterById(id)
                .orElseThrow(() -> new RuntimeException("Master not found"));
    }

    @GetMapping("/masters/by-name")
    public UserDTO getMasterByName(@RequestParam String name) {
        return userService.getMasterByName(name)
                .orElseThrow(() -> new RuntimeException("Master not found"));
    }

    @GetMapping("/beauty-services")
    public List<BeautyServiceDTO> getAllBeautyServices() {
        return beautyServiceService.getAllBeautyServices();
    }

    @GetMapping("/beauty-services/{id}")
    public BeautyServiceDTO getBeautyServiceById(@PathVariable Long id) {
        return beautyServiceService.getBeautyServiceById(id)
                .orElseThrow(() -> new RuntimeException("Beauty service not found"));
    }

    @GetMapping("/beauty-services/by-master/{masterId}")
    public List<BeautyServiceDTO> getBeautyServicesByMasterId(@PathVariable Long masterId) {
        return beautyServiceService.getBeautyServicesByMasterId(masterId);
    }

    @GetMapping("/beauty-services/price-range")
    public List<BeautyServiceDTO> getBeautyServicesByPriceRange(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        return beautyServiceService.getBeautyServicesByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/beauty-services/by-name")
    public BeautyServiceDTO getBeautyServiceByName(@RequestParam String name) {
        return beautyServiceService.getBeautyServiceByName(name)
                .orElseThrow(() -> new RuntimeException("Beauty service not found"));
    }
}
