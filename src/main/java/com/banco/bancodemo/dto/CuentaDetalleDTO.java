package com.banco.bancodemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDetalleDTO {
    private Long id;
    private BigDecimal saldo;
    private TitularCuentaDTO titularCuenta;
    private String tipoCuenta;
    private String numeroCuenta;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    private List<TransaccionDTO> historialTransacciones;
}
