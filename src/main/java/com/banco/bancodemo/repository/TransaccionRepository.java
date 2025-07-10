package com.banco.bancodemo.repository;

import com.banco.bancodemo.entity.Transaccion;
import com.banco.bancodemo.enums.EstadoTransaccion;
import com.banco.bancodemo.enums.TipoTransaccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    @Query("SELECT t FROM Transaccion t WHERE t.cuentaOrigen.id = :cuentaId OR t.cuentaDestino.id = :cuentaId ORDER BY t.fecha DESC")
    List<Transaccion> findByCuentaId(@Param("cuentaId") Long cuentaId);

    @Query("SELECT t FROM Transaccion t WHERE t.cuentaOrigen.id = :cuentaId OR t.cuentaDestino.id = :cuentaId ORDER BY t.fecha DESC")
    Page<Transaccion> findByCuentaIdPaginated(@Param("cuentaId") Long cuentaId, Pageable pageable);

    List<Transaccion> findByCuentaOrigenIdOrderByFechaDesc(Long cuentaOrigenId);

    List<Transaccion> findByCuentaDestinoIdOrderByFechaDesc(Long cuentaDestinoId);

    List<Transaccion> findByTipoAndEstado(TipoTransaccion tipo, EstadoTransaccion estado);

    @Query("SELECT t FROM Transaccion t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fecha DESC")
    List<Transaccion> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                         @Param("fechaFin") LocalDateTime fechaFin);

    Optional<Transaccion> findByReferencia(String referencia);

    @Query("SELECT COUNT(t) FROM Transaccion t WHERE t.cuentaOrigen.id = :cuentaId AND t.tipo = :tipo AND t.estado = :estado")
    Long countByCuentaIdAndTipoAndEstado(@Param("cuentaId") Long cuentaId,
                                         @Param("tipo") TipoTransaccion tipo,
                                         @Param("estado") EstadoTransaccion estado);
}
