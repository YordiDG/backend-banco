package com.banco.bancodemo.services.impl;

import com.banco.bancodemo.dto.*;
import com.banco.bancodemo.entity.Cuenta;
import com.banco.bancodemo.entity.TitularCuenta;
import com.banco.bancodemo.entity.Transaccion;
import com.banco.bancodemo.enums.TipoTransaccion;
import com.banco.bancodemo.enums.TipoCuenta;
import com.banco.bancodemo.exceptions.CuentaNotFoundException;
import com.banco.bancodemo.exceptions.OperacionNoPermitidaException;
import com.banco.bancodemo.exceptions.SaldoInsuficienteException;
import com.banco.bancodemo.mappers.CuentaMapper;
import com.banco.bancodemo.repository.CuentaRepository;
import com.banco.bancodemo.repository.TitularCuentaRepository;
import com.banco.bancodemo.repository.TransaccionRepository;
import com.banco.bancodemo.services.CuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private final TitularCuentaRepository titularRepository;
    private final CuentaMapper cuentaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaListaDTO> listarCuentas() {
        log.info("Listando todas las cuentas activas");
        return cuentaRepository.findAllActivasWithTitular().stream()
                .map(cuentaMapper::toCuentaListaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDetalleDTO obtenerDetalleCuenta(Long id) {
        log.info("Obteniendo detalle de cuenta con ID: {}", id);
        Cuenta cuenta = cuentaRepository.findByIdWithTitular(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        CuentaDetalleDTO detalleDTO = cuentaMapper.toCuentaDetalleDTO(cuenta);
        detalleDTO.setHistorialTransacciones(obtenerHistorialTransacciones(id));
        return detalleDTO;
    }

    @Override
    public TransaccionDTO procesarDeposito(Long cuentaId, DepositoRequestDTO depositoRequest) {
        log.info("Procesando depósito para cuenta ID: {}, monto: {}", cuentaId, depositoRequest.getMonto());

        Cuenta cuenta = obtenerCuentaValida(cuentaId);

        if (depositoRequest.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperacionNoPermitidaException("El monto del depósito debe ser mayor a cero");
        }

        cuenta.setSaldo(cuenta.getSaldo().add(depositoRequest.getMonto()));
        cuentaRepository.save(cuenta);

        TransaccionDTO transaccion = crearTransaccion(
                TipoTransaccion.DEPOSITO,
                depositoRequest.getMonto(),
                null,
                cuenta,
                depositoRequest.getDescripcion() != null ? depositoRequest.getDescripcion() : "Depósito en cuenta"
        );

        log.info("Depósito procesado exitosamente. Referencia: {}", transaccion.getReferencia());
        return transaccion;
    }

    @Override
    public TransaccionDTO procesarRetiro(Long cuentaId, RetiroRequestDTO retiroRequest) {
        log.info("Procesando retiro para cuenta ID: {}, monto: {}", cuentaId, retiroRequest.getMonto());

        Cuenta cuenta = obtenerCuentaValida(cuentaId);

        if (retiroRequest.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperacionNoPermitidaException("El monto del retiro debe ser mayor a cero");
        }

        BigDecimal comision = cuenta.calcularComision(retiroRequest.getMonto(), "retiro");
        validarSaldoSuficiente(cuentaId, retiroRequest.getMonto(), comision);

        BigDecimal montoTotal = retiroRequest.getMonto().add(comision);
        cuenta.setSaldo(cuenta.getSaldo().subtract(montoTotal));
        cuentaRepository.save(cuenta);

        TransaccionDTO transaccion = crearTransaccion(
                TipoTransaccion.RETIRO,
                retiroRequest.getMonto(),
                cuenta,
                null,
                retiroRequest.getDescripcion() != null ? retiroRequest.getDescripcion() : "Retiro de cuenta"
        );

        log.info("Retiro procesado exitosamente. Referencia: {}, Comisión: {}",
                transaccion.getReferencia(), comision);
        return transaccion;
    }

    @Override
    public TransaccionDTO procesarTransferencia(Long cuentaOrigenId, TransferenciaRequestDTO transferenciaRequest) {
        log.info("Procesando transferencia desde cuenta ID: {} hacia cuenta ID: {}, monto: {}",
                cuentaOrigenId, transferenciaRequest.getCuentaDestinoId(), transferenciaRequest.getMonto());

        if (cuentaOrigenId.equals(transferenciaRequest.getCuentaDestinoId())) {
            throw new OperacionNoPermitidaException("No se puede transferir a la misma cuenta");
        }

        Cuenta cuentaOrigen = obtenerCuentaValida(cuentaOrigenId);
        Cuenta cuentaDestino = obtenerCuentaValida(transferenciaRequest.getCuentaDestinoId());

        if (transferenciaRequest.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperacionNoPermitidaException("El monto de la transferencia debe ser mayor a cero");
        }

        BigDecimal comision = cuentaOrigen.calcularComision(transferenciaRequest.getMonto(), "transferencia");
        validarSaldoSuficiente(cuentaOrigenId, transferenciaRequest.getMonto(), comision);

        BigDecimal montoTotal = transferenciaRequest.getMonto().add(comision);
        cuentaOrigen.setSaldo(cuentaOrigen.getSaldo().subtract(montoTotal));
        cuentaDestino.setSaldo(cuentaDestino.getSaldo().add(transferenciaRequest.getMonto()));

        cuentaRepository.save(cuentaOrigen);
        cuentaRepository.save(cuentaDestino);

        TransaccionDTO transaccion = crearTransaccion(
                TipoTransaccion.TRANSFERENCIA,
                transferenciaRequest.getMonto(),
                cuentaOrigen,
                cuentaDestino,
                transferenciaRequest.getDescripcion() != null ?
                        transferenciaRequest.getDescripcion() : "Transferencia entre cuentas"
        );

        log.info("Transferencia procesada exitosamente. Referencia: {}, Comisión: {}",
                transaccion.getReferencia(), comision);
        return transaccion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransaccionDTO> obtenerHistorialTransacciones(Long cuentaId) {
        log.info("Obteniendo historial de transacciones para cuenta ID: {}", cuentaId);
        validarCuentaExiste(cuentaId);
        return transaccionRepository.findByCuentaId(cuentaId).stream()
                .map(cuentaMapper::toTransaccionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void validarCuentaExiste(Long cuentaId) {
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new CuentaNotFoundException("Cuenta no encontrada con ID: " + cuentaId);
        }
    }

    @Override
    public void validarSaldoSuficiente(Long cuentaId, BigDecimal monto, BigDecimal comision) {
        Cuenta cuenta = obtenerCuentaValida(cuentaId);

        if (!cuenta.puedeRetirar(monto, comision)) {
            throw new SaldoInsuficienteException(
                    String.format("Saldo insuficiente. Saldo actual: %.2f, Monto requerido: %.2f, Saldo mínimo: %.2f",
                            cuenta.getSaldo().doubleValue(),
                            monto.add(comision).doubleValue(),
                            cuenta.getSaldoMinimo().doubleValue())
            );
        }
    }

    @Override
    public CuentaDetalleDTO crearCuenta(CrearCuentaRequestDTO request) {
        log.info("Creando nueva cuenta para titular: {}", request.getTitularCuenta().getNombre());

        // Validar que no exista un titular con el mismo DNI
        if (titularRepository.existsByDni(request.getTitularCuenta().getDni())) {
            throw new RuntimeException("Ya existe un titular con el DNI: " + request.getTitularCuenta().getDni());
        }

        TitularCuenta titular = TitularCuenta.builder()
                .nombre(request.getTitularCuenta().getNombre())
                .direccion(request.getTitularCuenta().getDireccion())
                .email(request.getTitularCuenta().getEmail())
                .telefono(request.getTitularCuenta().getTelefono())
                .dni(request.getTitularCuenta().getDni())
                .build();

        titular = titularRepository.save(titular);

        String numeroCuenta = generarNumeroCuenta();

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(numeroCuenta)
                .saldo(request.getSaldoInicial())
                .tipoCuenta(TipoCuenta.valueOf(request.getTipoCuenta()))
                .titularCuenta(titular)  // <- CAMBIO: usar titularCuenta en lugar de titular
                .activa(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        cuenta = cuentaRepository.save(cuenta);

        // Crear transacción de depósito inicial si el saldo es mayor a cero
        if (request.getSaldoInicial().compareTo(BigDecimal.ZERO) > 0) {
            crearTransaccionDeposito(cuenta, request.getSaldoInicial(), "Depósito inicial");
        }

        log.info("Cuenta creada exitosamente con ID: {} y número: {}", cuenta.getId(), numeroCuenta);

        return cuentaMapper.toCuentaDetalleDTO(cuenta);
    }

    private Cuenta obtenerCuentaValida(Long cuentaId) {
        return cuentaRepository.findByIdAndActivaTrue(cuentaId)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada o inactiva con ID: " + cuentaId));
    }

    private TransaccionDTO crearTransaccion(TipoTransaccion tipo, BigDecimal monto,
                                            Cuenta cuentaOrigen, Cuenta cuentaDestino,
                                            String descripcion) {
        Transaccion transaccion = Transaccion.builder()
                .tipo(tipo)
                .monto(monto)
                .comision(cuentaOrigen != null ? cuentaOrigen.calcularComision(monto, tipo.name().toLowerCase()) : BigDecimal.ZERO)
                .fecha(LocalDateTime.now())
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .descripcion(descripcion)
                .saldoAnteriorOrigen(cuentaOrigen != null ? cuentaOrigen.getSaldo() : null)
                .saldoAnteriorDestino(cuentaDestino != null ? cuentaDestino.getSaldo() : null)
                .build();

        transaccion = transaccionRepository.save(transaccion);
        return cuentaMapper.toTransaccionDTO(transaccion);
    }

    // MÉTODO AGREGADO: Generar número de cuenta
    private String generarNumeroCuenta() {
        // Genera un número de cuenta único de 10 dígitos
        String numeroCuenta;
        do {
            numeroCuenta = String.format("%010d", (long) (Math.random() * 10000000000L));
        } while (cuentaRepository.existsByNumeroCuenta(numeroCuenta));

        return numeroCuenta;
    }

    // MÉTODO AGREGADO: Crear transacción de depósito inicial
    private void crearTransaccionDeposito(Cuenta cuenta, BigDecimal monto, String descripcion) {
        Transaccion transaccion = Transaccion.builder()
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(monto)
                .comision(BigDecimal.ZERO)
                .fecha(LocalDateTime.now())
                .cuentaOrigen(null)
                .cuentaDestino(cuenta)
                .descripcion(descripcion)
                .saldoAnteriorOrigen(null)
                .saldoAnteriorDestino(BigDecimal.ZERO)
                .build();

        transaccionRepository.save(transaccion);
    }
}