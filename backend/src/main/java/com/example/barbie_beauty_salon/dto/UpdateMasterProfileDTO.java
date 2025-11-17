package com.example.barbie_beauty_salon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateMasterProfileDTO {

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Формат: +79991234567")
    private String phone;

    public UpdateMasterProfileDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
