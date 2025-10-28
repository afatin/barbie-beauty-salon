package com.example.barbie_beauty_salon.security;

import com.example.barbie_beauty_salon.entities.User;
import com.example.barbie_beauty_salon.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User with login '" + login + "' not found"));

        return new UserPrincipal(
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                user.getName(),
                user.getPhone(),
                user.getRole()
        );
    }
}
