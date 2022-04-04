package com.mponte.minhasfinancas.services.interfaces;

import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.enums.StatusLancamento;

import java.util.List;

public interface LancamentoService {
    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Long id, Lancamento lancamento);

    void deletar(Long id);

    List<Lancamento> buscar(Lancamento lancamentoFiltro);

    void atualizarStatus(Long id, StatusLancamento status);

    void validar(Lancamento lancamento);
}
