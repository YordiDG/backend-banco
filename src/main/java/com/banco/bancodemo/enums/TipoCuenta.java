package com.banco.bancodemo.enums;

public enum TipoCuenta {
    CUENTA_ESTANDAR("Cuenta Estándar"),
    CUENTA_PREMIUM("Cuenta Premium");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}