package com.banco.bancodemo.enums;

public enum EstadoTransaccion {
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    FALLIDA("Fallida"),
    CANCELADA("Cancelada");

    private final String descripcion;

    EstadoTransaccion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
