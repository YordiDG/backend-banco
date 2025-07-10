package com.banco.bancodemo.services;

import com.banco.bancodemo.dto.TransaccionDTO;
import com.banco.bancodemo.entity.Cuenta;
import com.banco.bancodemo.entity.Transaccion;
import com.banco.bancodemo.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransaccionService {

    TransaccionDTO crearTransaccion(TipoTransaccion tipo, BigDecimal monto, Cuenta cuentaOrigen,
                                    Cuenta cuentaDestino, String descripcion);

    List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long cuentaId);

    TransaccionDTO obtenerTransaccionPorId(Long id);

    List<TransaccionDTO> obtenerTransaccionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    TransaccionDTO obtenerTransaccionPorReferencia(String referencia);

    void actualizarSaldos(Transaccion transaccion);
}
