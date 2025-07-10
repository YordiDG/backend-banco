package com.banco.bancodemo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrearCuentaRequestDTO {

    @Valid
    @NotNull(message = "Los datos del titular son requeridos")
    private TitularCuentaDTO titularCuenta;

    @NotNull(message = "El tipo de cuenta es requerido")
    @Pattern(regexp = "CUENTA_ESTANDAR|CUENTA_PREMIUM", message = "Tipo de cuenta debe ser CUENTA_ESTANDAR o CUENTA_PREMIUM")
    private String tipoCuenta;

    @DecimalMin(value = "0.0", inclusive = false, message = "El saldo inicial debe ser mayor a 0")
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TitularCuentaDTO {

        @NotNull(message = "El nombre es requerido")
        private String nombre;

        @NotNull(message = "La dirección es requerida")
        private String direccion;

        @NotNull(message = "El email es requerido")
        private String email;

        @NotNull(message = "la contraseña es requerido")
        private String password;

        private String telefono;

        @NotNull(message = "El DNI es requerido")
        @Pattern(regexp = "\\d{8}", message = "DNI debe tener 8 dígitos")
        private String dni;
    }
}
