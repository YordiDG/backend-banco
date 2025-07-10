package com.banco.bancodemo.services;

import com.banco.bancodemo.dto.*;

import java.util.List;

public interface CuentaService {

    List<CuentaListaDTO> listarCuentas();

    CuentaDetalleDTO obtenerDetalleCuenta(Long id);

    TransaccionDTO procesarDeposito(Long cuentaId, DepositoRequestDTO depositoRequest);

    TransaccionDTO procesarRetiro(Long cuentaId, RetiroRequestDTO retiroRequest);

    TransaccionDTO procesarTransferencia(Long cuentaOrigenId, TransferenciaRequestDTO transferenciaRequest);

    List<TransaccionDTO> obtenerHistorialTransacciones(Long cuentaId);

    void validarCuentaExiste(Long cuentaId);

    void validarSaldoSuficiente(Long cuentaId, java.math.BigDecimal monto, java.math.BigDecimal comision);

    CuentaDetalleDTO crearCuenta(CrearCuentaRequestDTO request);

}