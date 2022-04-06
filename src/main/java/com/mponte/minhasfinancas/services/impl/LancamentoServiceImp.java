package com.mponte.minhasfinancas.services.impl;

import com.mponte.minhasfinancas.model.dtos.LancamentoDTO;
import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.model.enums.StatusLancamento;
import com.mponte.minhasfinancas.model.enums.TipoLancamento;
import com.mponte.minhasfinancas.repositories.LancamentoRepository;
import com.mponte.minhasfinancas.services.exceptions.ObjetoNaoEncontradoException;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.interfaces.LancamentoService;
import com.mponte.minhasfinancas.services.interfaces.UsuarioService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class LancamentoServiceImp implements LancamentoService {

    private LancamentoRepository lancamentoRepository;
    private UsuarioService usuarioService;

    public LancamentoServiceImp(LancamentoRepository lancamentoRepository, UsuarioService usuarioService) {
        this.lancamentoRepository = lancamentoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    @Transactional
    public Lancamento salvar(LancamentoDTO dto) {
        Lancamento lancamento = converterDTO(dto);
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        lancamento.setDataCadastro(LocalDate.now());
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Long id, LancamentoDTO dto) {
        Lancamento lancamento = obterLancamentoPorId(id);
        Lancamento lancamentoAtualizacao = converterDTO(dto);
        lancamentoAtualizacao.setId(id);
        validar(lancamentoAtualizacao);
        atualizaLancamento(lancamento, lancamentoAtualizacao);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        Lancamento lancamento = obterLancamentoPorId(id);
        lancamentoRepository.delete(lancamento);
    }

    @Override
    public List<Lancamento> buscar(LancamentoDTO dto) {
        Lancamento lancamentoFiltro = converterDTO(dto);
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Lancamento> example = Example.of(lancamentoFiltro, matcher);
        return lancamentoRepository.findAll(example);
    }

    @Override
    public void atualizarStatus(Long id, StatusLancamento status) {
        Lancamento lancamento = obterLancamentoPorId(id);
        lancamento.setStatus(status);
        lancamentoRepository.save(lancamento);
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

    @Override
    public BigDecimal saldoUsuario(Long idUsuario) {
        Usuario usuario = usuarioService.findById(idUsuario);
        BigDecimal receitas = lancamentoRepository.buscarSaldoPorTipoLancamento(usuario, TipoLancamento.RECEITA);
        BigDecimal despesas = lancamentoRepository.buscarSaldoPorTipoLancamento(usuario, TipoLancamento.DESPESA);

        if(receitas == null){
            receitas = BigDecimal.ZERO;
        }
        if(despesas == null){
            despesas = BigDecimal.ZERO;
        }
        return receitas.subtract(despesas);
    }

    private void atualizaLancamento(Lancamento lancamento, Lancamento lancamentoAtualizacao) {
        lancamento.setDescricao(lancamentoAtualizacao.getDescricao());
        lancamento.setMes(lancamentoAtualizacao.getMes());
        lancamento.setAno(lancamentoAtualizacao.getAno());
        lancamento.setUsuario(lancamentoAtualizacao.getUsuario());
        lancamento.setValor(lancamentoAtualizacao.getValor());
        lancamento.setTipo(lancamentoAtualizacao.getTipo());
        lancamento.setStatus(lancamentoAtualizacao.getStatus());
    }

    private Lancamento converterDTO(LancamentoDTO dto){
        Usuario usuario = usuarioService.findById(dto.getUsuario());
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setMes(dto.getMes());
        lancamento.setAno(dto.getAno());
        lancamento.setUsuario(usuario);
        lancamento.setValor(dto.getValor());
        if(dto.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        }
        if(dto.getTipo() != null){
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        }

        return lancamento;
    }

    private Lancamento obterLancamentoPorId(Long id){
        return lancamentoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("não foi encontrado um lançamento com o id: "+id));
    }
}
