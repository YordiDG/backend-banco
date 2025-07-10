package com.banco.bancodemo.repository;


import com.banco.bancodemo.entity.Cuenta;
import com.banco.bancodemo.enums.TipoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByActivaTrue();

    Optional<Cuenta> findByIdAndActivaTrue(Long id);

    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);

    List<Cuenta> findByTipoCuenta(TipoCuenta tipoCuenta);

    boolean existsByNumeroCuenta(String numeroCuenta);

    @Query("SELECT c FROM Cuenta c WHERE c.titularCuenta.id = :titularId AND c.activa = true")
    List<Cuenta> findByTitularId(@Param("titularId") Long titularId);

    @Query("SELECT c FROM Cuenta c JOIN FETCH c.titularCuenta WHERE c.id = :id AND c.activa = true")
    Optional<Cuenta> findByIdWithTitular(@Param("id") Long id);

    @Query("SELECT c FROM Cuenta c JOIN FETCH c.titularCuenta WHERE c.activa = true")
    List<Cuenta> findAllActivasWithTitular();
}