package com.mponte.minhasfinancas.services.impl;

import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.enums.StatusLancamento;
import com.mponte.minhasfinancas.repositories.LancamentoRepository;
import com.mponte.minhasfinancas.services.exceptions.ObjetoNaoEncontradoException;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.interfaces.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public class LancamentoServiceImp implements LancamentoService {

    private LancamentoRepository lancamentoRepository;

    public LancamentoServiceImp(LancamentoRepository lancamentoRepository) {
        this.lancamentoRepository = lancamentoRepository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Long id, Lancamento lancamentoAtualizacao) {
        Lancamento lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("não foi encontrado um lançamento com o id: "+id));
        validar(lancamentoAtualizacao);
        atualizaLancamento(lancamento, lancamentoAtualizacao);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Lancamento lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("não foi encontrado um lançamento com o id: "+id));
        lancamentoRepository.delete(lancamento);
    }

    @Override
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Lancamento> example = Example.of(lancamentoFiltro, matcher);
        return lancamentoRepository.findAll(example);
    }

    @Override
    public void atualizarStatus(Long id, StatusLancamento status) {
        Lancamento lancamento = lancamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("não foi encontrado um lançamento com o id: "+id));
        lancamento.setStatus(status);
        salvar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioException("Informe uma Descrição válida.");
        }

        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12){
            throw new RegraNegocioException("Informe um Mês válido.");
        }

        if(lancamento.getAno() == null || String.valueOf(lancamento.getAno()).length() != 4){
            throw new RegraNegocioException("Informe um Ano válido.");
        }

        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraNegocioException("Informe um Usuário.");
        }

        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RegraNegocioException("Informe um Valor maior que 0.");
        }

        if(lancamento.getTipo() == null){
            throw new RegraNegocioException("Informe um tipo de lançamento.");
        }
    }

    private void atualizaLancamento(Lancamento lancamento, Lancamento lancamentoAtualizacao) {
        lancamento.setDescricao(lancamentoAtualizacao.getDescricao());
        lancamento.setMes(lancamentoAtualizacao.getMes());
        lancamento.setAno(lancamentoAtualizacao.getAno());
        lancamento.setUsuario(lancamentoAtualizacao.getUsuario());
        lancamento.setValor(lancamentoAtualizacao.getValor());
        lancamento.setDataCadastro(lancamentoAtualizacao.getDataCadastro());
        lancamento.setTipo(lancamentoAtualizacao.getTipo());
        lancamento.setStatus(lancamentoAtualizacao.getStatus());
    }
}
