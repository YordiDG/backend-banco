package com.banco.bancodemo.mappers;

import com.banco.bancodemo.dto.TransaccionDTO;
import com.banco.bancodemo.entity.Transaccion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransaccionMapper {

    public TransaccionDTO toTransaccionDTO(Transaccion transaccion) {
        return TransaccionDTO.builder()
                .id(transaccion.getId())
                .tipo(transaccion.getTipo().getDescripcion())
                .monto(transaccion.getMonto())
                .comision(transaccion.getComision())
                .fecha(transaccion.getFecha())
                .descripcion(transaccion.getDescripcion())
                .referencia(transaccion.getReferencia())
                .estado(transaccion.getEstado().getDescripcion())
                .cuentaOrigenId(transaccion.getCuentaOrigen() != null ? transaccion.getCuentaOrigen().getId() : null)
                .cuentaDestinoId(transaccion.getCuentaDestino() != null ? transaccion.getCuentaDestino().getId() : null)
                .saldoAnteriorOrigen(transaccion.getSaldoAnteriorOrigen())
                .saldoPosteriorOrigen(transaccion.getSaldoPosteriorOrigen())
                .build();
    }

    public List<TransaccionDTO> toTransaccionDTO(List<Transaccion> transacciones) {
        return transacciones.stream()
                .map(this::toTransaccionDTO)
                .collect(Collectors.toList());
    }
}