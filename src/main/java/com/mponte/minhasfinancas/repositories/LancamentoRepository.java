package com.mponte.minhasfinancas.repositories;

import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.model.enums.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    @Query(value = "select sum(l.valor) as saldo  from Lancamento l " +
            "where l.usuario = :usuario " +
            "AND l.tipo = :tipoLancamento")
    BigDecimal buscarSaldoPorTipoLancamento(@Param("usuario") Usuario usuario, @Param("tipoLancamento") TipoLancamento tipoLancamento);
}
