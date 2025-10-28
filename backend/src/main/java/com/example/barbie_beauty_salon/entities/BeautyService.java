package com.example.barbie_beauty_salon.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "beauty_services")
public class BeautyService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "beauty_service_name", nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToMany(mappedBy = "beautyServices", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<User> masters = new ArrayList<>();

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getMasters() {
        return masters;
    }

    public void setMasters(List<User> masters) {
        this.masters = masters;
    }

    public void addMaster(User master) {
        if (!masters.contains(master)) {
            masters.add(master);
            master.getBeautyServices().add(this);
        }
    }

    public void removeMaster(User master) {
        if (masters.contains(master)) {
            masters.remove(master);
            master.getBeautyServices().remove(this);
        }
    }
}
