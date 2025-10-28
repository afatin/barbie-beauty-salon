package com.example.barbie_beauty_salon.entities;

import jakarta.persistence.*;
import com.example.barbie_beauty_salon.enums.UserRole;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "master_beauty_services",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "beauty_service_id")
    )
    private List<BeautyService> beautyServices = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<BeautyService> getBeautyServices() {
        return beautyServices;
    }

    public void setBeautyServices(List<BeautyService> beautyServices) {
        this.beautyServices = beautyServices;
    }

    public void addBeautyService(BeautyService beautyService) {
        if (!beautyServices.contains(beautyService)) {
            beautyServices.add(beautyService);
            beautyService.getMasters().add(this);
        }
    }

    public void removeBeautyService(BeautyService beautyService) {
        if (beautyServices.contains(beautyService)) {
            beautyServices.remove(beautyService);
            beautyService.getMasters().remove(this);
        }
    }
}
