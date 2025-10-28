package com.example.barbie_beauty_salon.dto;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequestDTO {

    @NotBlank(message = "Логин обязателен для заполнения")
    private String login;

    @NotBlank(message = "Пароль обязателен для заполнения")
    private String password;

    @NotBlank(message = "Имя обязательно для заполнения")
    private String name;

    @NotBlank(message = "Телефон обязателен для заполнения")
    private String phone;

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
}
