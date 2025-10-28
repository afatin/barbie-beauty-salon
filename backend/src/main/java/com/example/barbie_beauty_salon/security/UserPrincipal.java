package com.example.barbie_beauty_salon.security;

import com.example.barbie_beauty_salon.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String login;
    private final String password;
    private final String name;
    private final String phone;
    private final UserRole role;

    public UserPrincipal(Long id, String login, String password, String name, String phone, UserRole role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return login; }

    public Long getId() { return id; }

    public String getName() { return name; }

    public String getPhone() { return phone; }

    public UserRole getRole() { return role; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
