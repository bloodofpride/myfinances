package com.mponte.minhasfinancas.repositories;

import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.model.enums.StatusLancamento;
import com.mponte.minhasfinancas.model.enums.TipoLancamento;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {
    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void devePersistirUmLancamentoNaBaseDeDados(){
        //Arrange/cenário
        Lancamento lancamento = criarLancamento();

        //Act/ação
        Lancamento lancamentoSalvo = lancamentoRepository.save(lancamento);

        //Assert/verificação
        assertNotNull(lancamentoSalvo.getId());
    }

    @Test
    public void deveDeletarUmLancamento(){
        //Arrange/cenário
        Lancamento lancamento = criarEPersistirUmLancamento();

        //Act/ação
        lancamentoRepository.deleteById(lancamento.getId());
        Lancamento lancamentoDeletado = entityManager.find(Lancamento.class, lancamento.getId());

        //Assert/verificação
        assertNull(lancamentoDeletado);
    }

    @Test
    public void deveAtualizarUmLancamento(){
        //Arrange/cenário
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento.setDescricao("Descricao Atualizada");
        lancamento.setAno(2018);
        lancamento.setStatus(StatusLancamento.CANCELADO);
        lancamento.setTipo(TipoLancamento.DESPESA);

        //Act/ação
        lancamentoRepository.save(lancamento);
        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        //Assert/verificação
        assertEquals("Descricao Atualizada", lancamentoAtualizado.getDescricao());
        assertEquals(2018, lancamentoAtualizado.getAno());
        assertEquals(StatusLancamento.CANCELADO, lancamentoAtualizado.getStatus());
        assertEquals(TipoLancamento.DESPESA, lancamentoAtualizado.getTipo());
    }

    @Test
    public void deveBuscarUmLancamentoPorId(){
        //Arrange/cenário
        Lancamento lancamento = criarEPersistirUmLancamento();

        //Act/ação
        Optional<Lancamento> lancamentoBuscadoPorId = lancamentoRepository.findById(lancamento.getId());

        //Assert/verificação
        assertTrue(lancamentoBuscadoPorId.isPresent());
    }

    private Lancamento criarEPersistirUmLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return entityManager.find(Lancamento.class, lancamento.getId());
    }

    private Lancamento criarLancamento() {
        return Lancamento.builder()
                .descricao("descricao")
                .mes(1)
                .ano(2000)
                .valor(BigDecimal.valueOf(10))
                .dataCadastro(LocalDate.now())
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .build();
    }
}
