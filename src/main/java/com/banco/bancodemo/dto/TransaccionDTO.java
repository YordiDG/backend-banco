package com.banco.bancodemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    private Long id;
    private String tipo;
    private BigDecimal monto;
    private BigDecimal comision;
    private LocalDateTime fecha;
    private String descripcion;
    private String referencia;
    private String estado;
    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private BigDecimal saldoAnteriorOrigen;
    private BigDecimal saldoPosteriorOrigen;
}
