package com.banco.bancodemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaListaDTO {
    private Long id;
    private BigDecimal saldo;
    private String titularCuenta;
    private String tipoCuenta;
    private String numeroCuenta;
    private Boolean activa;
}
