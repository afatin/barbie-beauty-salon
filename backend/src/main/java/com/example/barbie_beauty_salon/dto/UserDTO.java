package com.example.barbie_beauty_salon.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private Long id;
    private String name;
    private String phone;
    private String role;
    private List<BeautyServiceDTO> beautyServices;

    public UserDTO(Long id, String name, String phone, String role, List<BeautyServiceDTO> beautyServices) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.beautyServices = beautyServices != null ? beautyServices : new ArrayList<>();
    }

    public UserDTO(Long id, String name, String phone, String role) {
        this(id, name, phone, role, new ArrayList<>());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<BeautyServiceDTO> getBeautyServices() { return beautyServices; }
    public void setBeautyServices(List<BeautyServiceDTO> beautyServices) { this.beautyServices = beautyServices; }
}
