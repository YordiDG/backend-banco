package com.banco.bancodemo.repository;

import com.banco.bancodemo.entity.TitularCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitularCuentaRepository extends JpaRepository<TitularCuenta, Long> {

    Optional<TitularCuenta> findByEmail(String email);

    Optional<TitularCuenta> findByDni(String dni);

    @Query("SELECT t FROM TitularCuenta t WHERE t.nombre LIKE %:nombre%")
    List<TitularCuenta> findByNombreContaining(@Param("nombre") String nombre);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);
}
