package com.banco.bancodemo.mappers;

import com.banco.bancodemo.dto.*;
import com.banco.bancodemo.entity.Cuenta;
import com.banco.bancodemo.entity.Transaccion;
import com.banco.bancodemo.entity.TitularCuenta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CuentaMapper {

    public CuentaListaDTO toCuentaListaDTO(Cuenta cuenta) {
        return CuentaListaDTO.builder()
                .id(cuenta.getId())
                .saldo(cuenta.getSaldo())
                .titularCuenta(cuenta.getTitularCuenta().getNombre())
                .tipoCuenta(cuenta.getTipoCuenta().name())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .activa(cuenta.getActiva())
                .build();
    }

    public List<CuentaListaDTO> toListCuentaListaDTO(List<Cuenta> cuentas) {
        return cuentas.stream()
                .map(this::toCuentaListaDTO)
                .collect(Collectors.toList());
    }

    public CuentaDetalleDTO toCuentaDetalleDTO(Cuenta cuenta) {
        return CuentaDetalleDTO.builder()
                .id(cuenta.getId())
                .saldo(cuenta.getSaldo())
                .titularCuenta(toTitularCuentaDTO(cuenta.getTitularCuenta()))
                .tipoCuenta(cuenta.getTipoCuenta().name())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .activa(cuenta.getActiva())
                .fechaCreacion(cuenta.getFechaCreacion())
                .build();
    }

    public TitularCuentaDTO toTitularCuentaDTO(TitularCuenta titular) {
        return TitularCuentaDTO.builder()
                .id(titular.getId())
                .nombre(titular.getNombre())
                .direccion(titular.getDireccion())
                .email(titular.getEmail())
                .telefono(titular.getTelefono())
                .dni(titular.getDni())
                .build();
    }

    public TitularCuenta toTitularCuenta(TitularCuentaDTO titularDTO) {
        return TitularCuenta.builder()
                .id(titularDTO.getId())
                .nombre(titularDTO.getNombre())
                .direccion(titularDTO.getDireccion())
                .email(titularDTO.getEmail())
                .telefono(titularDTO.getTelefono())
                .dni(titularDTO.getDni())
                .build();
    }

    public TransaccionDTO toTransaccionDTO(Transaccion transaccion) {
        return TransaccionDTO.builder()
                .id(transaccion.getId())
                .tipo(transaccion.getTipo().name())
                .monto(transaccion.getMonto())
                .comision(transaccion.getComision())
                .fecha(transaccion.getFecha())
                .descripcion(transaccion.getDescripcion())
                .referencia(transaccion.getReferencia())
                .estado(transaccion.getEstado().name())
                .cuentaOrigenId(transaccion.getCuentaOrigen() != null ? transaccion.getCuentaOrigen().getId() : null)
                .cuentaDestinoId(transaccion.getCuentaDestino() != null ? transaccion.getCuentaDestino().getId() : null)
                .saldoAnteriorOrigen(transaccion.getSaldoAnteriorOrigen())
                .saldoPosteriorOrigen(transaccion.getSaldoPosteriorOrigen())
                .build();
    }

    public List<TransaccionDTO> toListTransaccionDTO(List<Transaccion> transacciones) {
        return transacciones.stream()
                .map(this::toTransaccionDTO)
                .collect(Collectors.toList());
    }
}