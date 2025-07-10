package com.banco.bancodemo.entity;

import com.banco.bancodemo.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cuentas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id", nullable = false)
    private TitularCuenta titularCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false)
    private TipoCuenta tipoCuenta;

    @Column(name = "numero_cuenta", unique = true, length = 20)
    private String numeroCuenta;

    @Column(name = "activa", nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "cuentaOrigen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaccion> transaccionesOrigen = new ArrayList<>();

    @OneToMany(mappedBy = "cuentaDestino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaccion> transaccionesDestino = new ArrayList<>();

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // obtener el saldo mínimo según el tipo de cuenta
    public BigDecimal getSaldoMinimo() {
        return switch (this.tipoCuenta) {
            case CUENTA_ESTANDAR -> new BigDecimal("100.00");
            case CUENTA_PREMIUM -> BigDecimal.ZERO;
        };
    }

    //verificar si puede realizar una operación
    public boolean puedeRetirar(BigDecimal monto, BigDecimal comision) {
        BigDecimal montoTotal = monto.add(comision);
        BigDecimal saldoResultante = this.saldo.subtract(montoTotal);
        return saldoResultante.compareTo(getSaldoMinimo()) >= 0;
    }

    // calcular comisión
    public BigDecimal calcularComision(BigDecimal monto, String tipoOperacion) {
        if (this.tipoCuenta == TipoCuenta.CUENTA_PREMIUM) {
            return BigDecimal.ZERO;
        }

        return switch (tipoOperacion.toLowerCase()) {
            case "retiro" -> monto.multiply(new BigDecimal("0.02")); // 2%
            case "transferencia" -> monto.multiply(new BigDecimal("0.01")); // 1%
            default -> BigDecimal.ZERO;
        };
    }
}
