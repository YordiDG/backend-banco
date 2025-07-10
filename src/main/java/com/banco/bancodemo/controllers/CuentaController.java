package com.banco.bancodemo.controllers;

import com.banco.bancodemo.dto.*;
import com.banco.bancodemo.services.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cuentas", description = "API para gestión de cuentas bancarias")
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    @Operation(summary = "Listar todas las cuentas", description = "Obtiene una lista de todas las cuentas activas")
    public ResponseEntity<ApiResponseDTO<List<CuentaListaDTO>>> listarCuentas() {
        log.info("Solicitud para listar todas las cuentas");

        List<CuentaListaDTO> cuentas = cuentaService.listarCuentas();

        ApiResponseDTO<List<CuentaListaDTO>> response = ApiResponseDTO.success(
                cuentas,
                "Cuentas obtenidas exitosamente"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de cuenta", description = "Obtiene el detalle completo de una cuenta incluyendo historial de transacciones")
    public ResponseEntity<ApiResponseDTO<CuentaDetalleDTO>> obtenerDetalleCuenta(
            @Parameter(description = "ID de la cuenta") @PathVariable Long id) {
        log.info("Solicitud para obtener detalle de cuenta con ID: {}", id);

        CuentaDetalleDTO detalle = cuentaService.obtenerDetalleCuenta(id);

        ApiResponseDTO<CuentaDetalleDTO> response = ApiResponseDTO.success(
                detalle,
                "Detalle de cuenta obtenido exitosamente"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/depositar")
    @Operation(summary = "Realizar depósito", description = "Realiza un depósito en la cuenta especificada")
    public ResponseEntity<ApiResponseDTO<TransaccionDTO>> procesarDeposito(
            @Parameter(description = "ID de la cuenta") @PathVariable Long id,
            @Valid @RequestBody DepositoRequestDTO depositoRequest) {
        log.info("Solicitud para procesar depósito en cuenta ID: {}", id);

        TransaccionDTO transaccion = cuentaService.procesarDeposito(id, depositoRequest);

        ApiResponseDTO<TransaccionDTO> response = ApiResponseDTO.success(
                transaccion,
                "Depósito procesado exitosamente"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/retirar")
    @Operation(summary = "Realizar retiro", description = "Realiza un retiro de la cuenta especificada")
    public ResponseEntity<ApiResponseDTO<TransaccionDTO>> procesarRetiro(
            @Parameter(description = "ID de la cuenta") @PathVariable Long id,
            @Valid @RequestBody RetiroRequestDTO retiroRequest) {
        log.info("Solicitud para procesar retiro en cuenta ID: {}", id);

        TransaccionDTO transaccion = cuentaService.procesarRetiro(id, retiroRequest);

        ApiResponseDTO<TransaccionDTO> response = ApiResponseDTO.success(
                transaccion,
                "Retiro procesado exitosamente"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/transferir")
    @Operation(summary = "Realizar transferencia", description = "Realiza una transferencia desde la cuenta especificada a otra cuenta")
    public ResponseEntity<ApiResponseDTO<TransaccionDTO>> procesarTransferencia(
            @Parameter(description = "ID de la cuenta origen") @PathVariable Long id,
            @Valid @RequestBody TransferenciaRequestDTO transferenciaRequest) {
        log.info("Solicitud para procesar transferencia desde cuenta ID: {}", id);

        TransaccionDTO transaccion = cuentaService.procesarTransferencia(id, transferenciaRequest);

        ApiResponseDTO<TransaccionDTO> response = ApiResponseDTO.success(
                transaccion,
                "Transferencia procesada exitosamente"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/transacciones")
    @Operation(summary = "Obtener historial de transacciones", description = "Obtiene el historial de transacciones de una cuenta")
    public ResponseEntity<ApiResponseDTO<List<TransaccionDTO>>> obtenerHistorialTransacciones(
            @Parameter(description = "ID de la cuenta") @PathVariable Long id) {
        log.info("Solicitud para obtener historial de transacciones de cuenta ID: {}", id);

        List<TransaccionDTO> historial = cuentaService.obtenerHistorialTransacciones(id);

        ApiResponseDTO<List<TransaccionDTO>> response = ApiResponseDTO.success(
                historial,
                "Historial de transacciones obtenido exitosamente"
        );

        return ResponseEntity.ok(response);
    }

    // CORRECCIÓN: Parámetro corregido
    @PostMapping
    @Operation(summary = "Crear nueva cuenta", description = "Crea una nueva cuenta bancaria")
    public ResponseEntity<ApiResponseDTO<CuentaDetalleDTO>> crearCuenta(
            @Valid @RequestBody CrearCuentaRequestDTO crearCuentaRequest) {
        log.info("Solicitud para crear nueva cuenta para titular: {}", crearCuentaRequest.getTitularCuenta().getNombre());

        CuentaDetalleDTO nuevaCuenta = cuentaService.crearCuenta(crearCuentaRequest);

        ApiResponseDTO<CuentaDetalleDTO> response = ApiResponseDTO.success(
                nuevaCuenta,
                "Cuenta creada exitosamente"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}