package com.example.barbie_beauty_salon.services;

import com.example.barbie_beauty_salon.dto.DTOConverter;
import com.example.barbie_beauty_salon.dto.RegisterRequestDTO;
import com.example.barbie_beauty_salon.dto.UserDTO;
import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.enums.UserRole;
import com.example.barbie_beauty_salon.exceptions.ValidationException;
import com.example.barbie_beauty_salon.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DTOConverter dtoConverter;

    public UserRegistrationService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   DTOConverter dtoConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dtoConverter = dtoConverter;
    }

    public UserDTO registerClient(RegisterRequestDTO request) {
        validateUnique(request.getLogin(), request.getPhone());
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.ROLE_CLIENT);
        return dtoConverter.convertToUserDTO(userRepository.save(user));
    }

    public UserDTO registerMaster(RegisterRequestDTO request) {
        validateUnique(request.getLogin(), request.getPhone());
        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.ROLE_MASTER);
        return dtoConverter.convertToUserDTO(userRepository.save(user));
    }

    private void validateUnique(String login, String phone) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new ValidationException("Login is already taken");
        }
        if (userRepository.findByPhone(phone).isPresent()) {
            throw new ValidationException("Phone number is already registered");
        }
    }
}
