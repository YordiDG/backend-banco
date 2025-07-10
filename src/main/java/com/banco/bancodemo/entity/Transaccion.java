package com.banco.bancodemo.entity;

import com.banco.bancodemo.enums.EstadoTransaccion;
import com.banco.bancodemo.enums.TipoTransaccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransaccion tipo;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "comision", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal comision = BigDecimal.ZERO;

    @Column(name = "fecha", nullable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_origen_id")
    private Cuenta cuentaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_destino_id")
    private Cuenta cuentaDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoTransaccion estado = EstadoTransaccion.COMPLETADA;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "referencia", unique = true, length = 50)
    private String referencia;

    @Column(name = "saldo_anterior_origen", precision = 15, scale = 2)
    private BigDecimal saldoAnteriorOrigen;

    @Column(name = "saldo_posterior_origen", precision = 15, scale = 2)
    private BigDecimal saldoPosteriorOrigen;

    @Column(name = "saldo_anterior_destino", precision = 15, scale = 2)
    private BigDecimal saldoAnteriorDestino;

    @Column(name = "saldo_posterior_destino", precision = 15, scale = 2)
    private BigDecimal saldoPosteriorDestino;

    @PrePersist
    public void prePersist() {
        if (this.referencia == null) {
            this.referencia = generarReferencia();
        }
    }

    private String generarReferencia() {
        return "TXN-" + System.currentTimeMillis() + "-" +
                (int)(Math.random() * 1000);
    }
}
