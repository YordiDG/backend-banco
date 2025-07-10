package com.banco.bancodemo.services.impl;

import com.banco.bancodemo.dto.TransaccionDTO;
import com.banco.bancodemo.entity.Cuenta;
import com.banco.bancodemo.entity.Transaccion;
import com.banco.bancodemo.enums.TipoTransaccion;
import com.banco.bancodemo.exceptions.TransaccionNotFoundException;
import com.banco.bancodemo.mappers.TransaccionMapper;
import com.banco.bancodemo.repository.TransaccionRepository;
import com.banco.bancodemo.services.TransaccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionMapper transaccionMapper;

    @Override
    public TransaccionDTO crearTransaccion(TipoTransaccion tipo, BigDecimal monto, Cuenta cuentaOrigen,
                                           Cuenta cuentaDestino, String descripcion) {
        log.info("Creando transacción tipo: {}, monto: {}", tipo, monto);

        BigDecimal comision = BigDecimal.ZERO;
        if (cuentaOrigen != null) {
            comision = cuentaOrigen.calcularComision(monto, tipo.name().toLowerCase());
        }

        // Registrar saldos anteriores
        BigDecimal saldoAnteriorOrigen = cuentaOrigen != null ? cuentaOrigen.getSaldo() : null;
        BigDecimal saldoAnteriorDestino = cuentaDestino != null ? cuentaDestino.getSaldo() : null;

        // Crear transacción
        Transaccion transaccion = Transaccion.builder()
                .tipo(tipo)
                .monto(monto)
                .comision(comision)
                .fecha(LocalDateTime.now())
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .descripcion(descripcion)
                .saldoAnteriorOrigen(saldoAnteriorOrigen)
                .saldoAnteriorDestino(saldoAnteriorDestino)
                .build();

        // Guardar transacción
        transaccion = transaccionRepository.save(transaccion);

        // Actualizar saldos posteriores
        actualizarSaldos(transaccion);

        log.info("Transacción creada exitosamente con ID: {} y referencia: {}",
                transaccion.getId(), transaccion.getReferencia());

        return transaccionMapper.toTransaccionDTO(transaccion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorCuenta(Long cuentaId) {
        log.info("Obteniendo transacciones para cuenta ID: {}", cuentaId);

        List<Transaccion> transacciones = transaccionRepository.findByCuentaId(cuentaId);
        return transaccionMapper.toTransaccionDTO(transacciones);
    }

    @Override
    @Transactional(readOnly = true)
    public TransaccionDTO obtenerTransaccionPorId(Long id) {
        log.info("Obteniendo transacción con ID: {}", id);

        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new TransaccionNotFoundException("Transacción no encontrada con ID: " + id));

        return transaccionMapper.toTransaccionDTO(transaccion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerTransaccionesPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Obteniendo transacciones entre {} y {}", fechaInicio, fechaFin);

        List<Transaccion> transacciones = transaccionRepository.findByFechaBetween(fechaInicio, fechaFin);
        return transaccionMapper.toTransaccionDTO(transacciones);
    }

    @Override
    @Transactional(readOnly = true)
    public TransaccionDTO obtenerTransaccionPorReferencia(String referencia) {
        log.info("Obteniendo transacción con referencia: {}", referencia);

        Transaccion transaccion = transaccionRepository.findByReferencia(referencia)
                .orElseThrow(() -> new TransaccionNotFoundException("Transacción no encontrada con referencia: " + referencia));

        return transaccionMapper.toTransaccionDTO(transaccion);
    }

    @Override
    public void actualizarSaldos(Transaccion transaccion) {
        // Actualizar saldo posterior origen
        if (transaccion.getCuentaOrigen() != null) {
            transaccion.setSaldoPosteriorOrigen(transaccion.getCuentaOrigen().getSaldo());
        }

        if (transaccion.getCuentaDestino() != null) {
            transaccion.setSaldoPosteriorDestino(transaccion.getCuentaDestino().getSaldo());
        }

        transaccionRepository.save(transaccion);
    }
}
