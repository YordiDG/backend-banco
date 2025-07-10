package com.banco.bancodemo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String nombre;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @Size(min = 8, max = 8)
    private String dni;

    @NotBlank
    private String direccion;

    @NotBlank
    @Size(min = 9, max = 9)
    private String telefono;
}