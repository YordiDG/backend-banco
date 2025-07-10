package com.banco.bancodemo.services.impl;

import com.banco.bancodemo.dto.TitularCuentaDTO;
import com.banco.bancodemo.entity.TitularCuenta;
import com.banco.bancodemo.exceptions.TitularCuentaNotFoundException;
import com.banco.bancodemo.exceptions.ValidationException;
import com.banco.bancodemo.mappers.CuentaMapper;
import com.banco.bancodemo.repository.TitularCuentaRepository;
import com.banco.bancodemo.services.TitularCuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TitularCuentaServiceImpl implements TitularCuentaService {

    private final TitularCuentaRepository titularRepository;
    private final CuentaMapper cuentaMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TitularCuentaDTO crearTitular(TitularCuentaDTO titularDTO) {
        // Validar datos únicos
        if (titularRepository.existsByEmail(titularDTO.getEmail())) {
            throw new ValidationException("Ya existe un titular con el email: " + titularDTO.getEmail());
        }

        if (titularRepository.existsByDni(titularDTO.getDni())) {
            throw new ValidationException("Ya existe un titular con el DNI: " + titularDTO.getDni());
        }

        // Crear entidad
        TitularCuenta titular = TitularCuenta.builder()
                .nombre(titularDTO.getNombre())
                .email(titularDTO.getEmail())
                .password(titularDTO.getPassword()) // Ya viene encriptada del controller
                .dni(titularDTO.getDni())
                .direccion(titularDTO.getDireccion())
                .telefono(titularDTO.getTelefono())
                .build();

        titular = titularRepository.save(titular);

        return cuentaMapper.toTitularCuentaDTO(titular);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TitularCuentaDTO> listarTitulares() {
        log.info("Listando todos los titulares de cuenta");

        List<TitularCuenta> titulares = titularRepository.findAll();
        return titulares.stream()
                .map(cuentaMapper::toTitularCuentaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TitularCuentaDTO obtenerTitularPorId(Long id) {
        log.info("Obteniendo titular con ID: {}", id);

        TitularCuenta titular = titularRepository.findById(id)
                .orElseThrow(() -> new TitularCuentaNotFoundException("Titular no encontrado con ID: " + id));

        return cuentaMapper.toTitularCuentaDTO(titular);
    }


    @Override
    public TitularCuentaDTO actualizarTitular(Long id, TitularCuentaDTO titularDTO) {
        log.info("Actualizando titular con ID: {}", id);

        TitularCuenta titular = titularRepository.findById(id)
                .orElseThrow(() -> new TitularCuentaNotFoundException("Titular no encontrado con ID: " + id));

        // Validar datos únicos solo si han cambiado
        if (!titular.getEmail().equals(titularDTO.getEmail()) && existePorEmail(titularDTO.getEmail())) {
            throw new ValidationException("Ya existe un titular con el email: " + titularDTO.getEmail());
        }

        if (!titular.getDni().equals(titularDTO.getDni()) && existePorDni(titularDTO.getDni())) {
            throw new ValidationException("Ya existe un titular con el DNI: " + titularDTO.getDni());
        }

        // Actualizar datos
        titular.setNombre(titularDTO.getNombre());
        titular.setDireccion(titularDTO.getDireccion());
        titular.setEmail(titularDTO.getEmail());
        titular.setTelefono(titularDTO.getTelefono());
        titular.setDni(titularDTO.getDni());

        titular = titularRepository.save(titular);

        log.info("Titular actualizado exitosamente con ID: {}", titular.getId());
        return cuentaMapper.toTitularCuentaDTO(titular);
    }

    @Override
    public void eliminarTitular(Long id) {
        log.info("Eliminando titular con ID: {}", id);

        if (!titularRepository.existsById(id)) {
            throw new TitularCuentaNotFoundException("Titular no encontrado con ID: " + id);
        }

        titularRepository.deleteById(id);
        log.info("Titular eliminado exitosamente con ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public TitularCuenta buscarPorEmail(String email) {
        log.info("Buscando titular por email: {}", email);

        return titularRepository.findByEmail(email)
                .orElseThrow(() -> new TitularCuentaNotFoundException("Titular no encontrado con email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public TitularCuenta buscarPorDni(String dni) {
        log.info("Buscando titular por DNI: {}", dni);

        return titularRepository.findByDni(dni)
                .orElseThrow(() -> new TitularCuentaNotFoundException("Titular no encontrado con DNI: " + dni));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return titularRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorDni(String dni) {
        return titularRepository.existsByDni(dni);
    }

    private void validarDatosUnicos(TitularCuentaDTO titularDTO) {
        if (existePorEmail(titularDTO.getEmail())) {
            throw new ValidationException("Ya existe un titular con el email: " + titularDTO.getEmail());
        }

        if (existePorDni(titularDTO.getDni())) {
            throw new ValidationException("Ya existe un titular con el DNI: " + titularDTO.getDni());
        }
    }
}
