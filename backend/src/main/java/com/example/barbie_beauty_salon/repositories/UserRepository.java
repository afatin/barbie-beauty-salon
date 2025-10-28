package com.example.barbie_beauty_salon.repositories;

import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    Optional<User> findByPhone(String phone);

    Optional<User> findByName(String name);

    List<User> findByRole(UserRole role);

    Optional<User> findByLoginAndRole(String login, UserRole role);

    Optional<User> findByPhoneAndRole(String phone, UserRole role);

    List<User> findAllByIdIn(List<Long> ids);
}
