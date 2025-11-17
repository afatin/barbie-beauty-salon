package com.example.barbie_beauty_salon.config;

import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.enums.UserRole;
import com.example.barbie_beauty_salon.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Создаём админа, только если ни одного пользователя нет
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("Администратор");
            admin.setPhone("+70000000000");
            admin.setRole(UserRole.ROLE_ADMIN);

            userRepository.save(admin);
            System.out.println("Создан первый админ: login=admin, password=admin");
        }
    }
}
