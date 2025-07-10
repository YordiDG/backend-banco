package com.banco.bancodemo.services;

import com.banco.bancodemo.dto.TitularCuentaDTO;
import com.banco.bancodemo.entity.TitularCuenta;

import java.util.List;

public interface TitularCuentaService {

    List<TitularCuentaDTO> listarTitulares();

    TitularCuentaDTO obtenerTitularPorId(Long id);

    TitularCuentaDTO crearTitular(TitularCuentaDTO titularDTO);

    TitularCuentaDTO actualizarTitular(Long id, TitularCuentaDTO titularDTO);

    void eliminarTitular(Long id);

    TitularCuenta buscarPorEmail(String email);

    TitularCuenta buscarPorDni(String dni);

    boolean existePorEmail(String email);

    boolean existePorDni(String dni);
}
