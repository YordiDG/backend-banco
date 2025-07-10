package com.banco.bancodemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TitularCuentaDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String email;
    private String password;
    private String telefono;
    private String dni;
}

