package com.mponte.minhasfinancas.services.interfaces;

import com.mponte.minhasfinancas.model.dtos.LancamentoDTO;
import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.enums.StatusLancamento;

import java.math.BigDecimal;
import java.util.List;

public interface LancamentoService {
    Lancamento salvar(LancamentoDTO dto);

    Lancamento atualizar(Long id, LancamentoDTO dto);

    void deletar(Long id);

    List<Lancamento> buscar(LancamentoDTO lancamentoFiltro);

    void atualizarStatus(Long id, StatusLancamento status);

    void validar(Lancamento lancamento);

    BigDecimal saldoUsuario(Long idUsuario);

}
