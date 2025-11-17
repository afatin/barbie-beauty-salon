package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.dto.BeautyServiceDTO;
import com.example.barbie_beauty_salon.dto.DTOConverter;
import com.example.barbie_beauty_salon.entities.BeautyService;
import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.repositories.BeautyServiceRepository;
import com.example.barbie_beauty_salon.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BeautyServiceService {

    private final BeautyServiceRepository beautyServiceRepository;
    private final UserRepository userRepository;
    private final DTOConverter dtoConverter;

    public BeautyServiceService(BeautyServiceRepository beautyServiceRepository,
                                UserRepository userRepository,
                                DTOConverter dtoConverter) {
        this.beautyServiceRepository = beautyServiceRepository;
        this.userRepository = userRepository;
        this.dtoConverter = dtoConverter;
    }

    public List<BeautyServiceDTO> getAllBeautyServices() {
        return beautyServiceRepository.findAll().stream()
                .map(dtoConverter::convertToBeautyServiceDTO)
                .toList();
    }

    public Optional<BeautyServiceDTO> getBeautyServiceById(Long beautyServiceId) {
        return beautyServiceRepository.findById(beautyServiceId)
                .map(dtoConverter::convertToBeautyServiceDTO);
    }

    public Optional<BeautyServiceDTO> getBeautyServiceByName(String name) {
        return beautyServiceRepository.findByName(name)
                .map(dtoConverter::convertToBeautyServiceDTO);
    }

    public List<BeautyServiceDTO> getBeautyServicesByPriceRange(double min, double max) {
        return beautyServiceRepository.findByPriceBetween(min, max).stream()
                .map(dtoConverter::convertToBeautyServiceDTO)
                .toList();
    }

    public List<BeautyServiceDTO> getBeautyServicesByMasterId(Long masterId) {
        User master = userRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found"));
        if (master.getBeautyServices() == null) return List.of();
        return master.getBeautyServices().stream()
                .map(dtoConverter::convertToBeautyServiceDTO)
                .toList();
    }

    @Transactional
    public BeautyServiceDTO createBeautyService(BeautyService beautyService) {
        BeautyService newBeautyService = beautyServiceRepository.save(beautyService);
        return dtoConverter.convertToBeautyServiceDTO(newBeautyService);
    }

    @Transactional
    public BeautyServiceDTO updateBeautyServiceFromAdmin(Long id, BeautyService updates) {
        return beautyServiceRepository.findById(id)
                .map(existing -> {
                    if (updates.getName() != null) existing.setName(updates.getName());
                    if (updates.getPrice() != null) existing.setPrice(updates.getPrice());
                    if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
                    return dtoConverter.convertToBeautyServiceDTO(beautyServiceRepository.save(existing));
                })
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
    }

    @Transactional
    public void deleteBeautyService(Long beautyServiceId) {
        BeautyService beautyService = beautyServiceRepository.findById(beautyServiceId)
                .orElseThrow(() -> new EntityNotFoundException("Beauty service not found"));
        beautyServiceRepository.delete(beautyService);
    }
}
