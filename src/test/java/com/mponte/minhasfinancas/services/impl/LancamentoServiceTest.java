package com.mponte.minhasfinancas.services.impl;

import com.mponte.minhasfinancas.model.dtos.LancamentoDTO;
import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.model.enums.StatusLancamento;
import com.mponte.minhasfinancas.model.enums.TipoLancamento;
import com.mponte.minhasfinancas.repositories.LancamentoRepository;
import com.mponte.minhasfinancas.services.exceptions.ObjetoNaoEncontradoException;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.interfaces.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @InjectMocks @Spy
    private LancamentoServiceImp lancamentoService;

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Test
    public void deveSalvarUmLancamento(){
        //cenario
        Lancamento lancamento = criarLancamento();
        doReturn(new Lancamento()).when(lancamentoService).converterDTO(Mockito.any());
        doNothing().when(lancamentoService).validar(Mockito.any());
        Mockito.when(lancamentoRepository.save(Mockito.any(Lancamento.class))).thenReturn(lancamento);

        //acao
        Lancamento lancamentoSalvo = lancamentoService.salvar(new LancamentoDTO());
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        //verificação
        Assertions.assertNotNull(lancamentoSalvo);
        Assertions.assertEquals("descricao", lancamentoSalvo.getDescricao());
        Assertions.assertEquals(1, lancamentoSalvo.getMes());
        Assertions.assertEquals(2000, lancamentoSalvo.getAno());
        Assertions.assertEquals(BigDecimal.valueOf(10), lancamentoSalvo.getValor());
        Assertions.assertEquals(TipoLancamento.RECEITA, lancamentoSalvo.getTipo());
        Assertions.assertEquals(StatusLancamento.PENDENTE, lancamentoSalvo.getStatus());
    }

    @Test
    public void naoDeveSalvarUmlancamentoQuandoHouverErroDeValidacao(){
        //cenário
        Lancamento lancamento = criarLancamento();
        doReturn(new Lancamento()).when(lancamentoService).converterDTO(Mockito.any());
        doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamento);

        //verificação
        verify(lancamentoRepository, never()).save(lancamento);
    }

    @Test
    public void deveObterUmLancamentoPorId(){
        Lancamento lancamento = criarLancamento();
        lancamento.setId(1L);
        Mockito.when(lancamentoRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(lancamento));

        //acao
        Lancamento lancamentoPorId = lancamentoService.obterLancamentoPorId(1L);

        //assert
        Assertions.assertNotNull(lancamentoPorId);
    }

    @Test
    public void deveLancarErrorAoBuscarUmLancamentoPorIdInexistente(){
        //cenario
        Long id = 1L;

        //acao / verificacao
        try {
            lancamentoService.obterLancamentoPorId(id);
            fail();
        }catch (ObjetoNaoEncontradoException e){
            assertEquals("não foi encontrado um lançamento com o id: 1", e.getLocalizedMessage());
        }
    }

    @Test
    public void deveConverterUmDtoParaLancamento(){
        //cenario
        LancamentoDTO dto = criarDto();
        Mockito.when(usuarioService.findById(Mockito.anyLong())).thenReturn(new Usuario());

        //acao
        Lancamento lancamento = lancamentoService.converterDTO(dto);

        //assert
        assertEquals("descricao", lancamento.getDescricao());
        assertEquals(1, lancamento.getMes());
        assertEquals(2000, lancamento.getAno());
        assertEquals(BigDecimal.valueOf(10), lancamento.getValor());
        assertEquals(TipoLancamento.RECEITA, lancamento.getTipo());
        assertEquals(TipoLancamento.RECEITA, lancamento.getTipo());
    }

    @Test
    public void deveLancarErroAotentarConverterUmDtoComIdDeUsuarioInexistente(){
        //cenario
        LancamentoDTO dto = criarDto();
        Mockito.when(usuarioService.findById(1L)).thenThrow(ObjetoNaoEncontradoException.class);

        //acao
        try {
            Lancamento lancamento = lancamentoService.converterDTO(dto);
            fail();
        }
        catch (ObjetoNaoEncontradoException e){
            assertTrue(true);
        }
    }


    @Test
    public void deveAtualizarUmLancamento() {
        //cenario
        Lancamento lancamentoAtualizado = criarLancamento();
        lancamentoAtualizado.setId(1L);
        doReturn(lancamentoAtualizado).when(lancamentoService).obterLancamentoPorId(Mockito.anyLong());
        doReturn(lancamentoAtualizado).when(lancamentoService).converterDTO(Mockito.any());
        doNothing().when(lancamentoService).validar(Mockito.any());
        doNothing().when(lancamentoService).atualizaLancamento(Mockito.any(), Mockito.any());
        Mockito.when(lancamentoRepository.save(Mockito.any(Lancamento.class))).thenReturn(lancamentoAtualizado);

        //acao
        lancamentoService.atualizar(1L, new LancamentoDTO());

        //verificacao
        verify(lancamentoRepository, times(1)).save(lancamentoAtualizado);
    }

    @Test
    public void deveDeletarUmLancamento(){
        //cenario
        Lancamento lancamentoADeletar = criarLancamento();
        doReturn(lancamentoADeletar).when(lancamentoService).obterLancamentoPorId(Mockito.anyLong());

        //acao
        lancamentoService.deletar(1L);

        //
        verify(lancamentoRepository, times(1)).delete(lancamentoADeletar);
    }

    @Test
    public void deveFiltrarUmLancamento(){
        //cenario
        LancamentoDTO dto = criarDto();
        Lancamento lancamento = criarLancamento();
        doReturn(lancamento).when(lancamentoService).converterDTO(Mockito.any(LancamentoDTO.class));
        lancamento.setId(1L);
        List<Lancamento> lancamentos = List.of(lancamento);
        when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(lancamentos);

        //ação
        List<Lancamento> lancamentosBuscados = lancamentoService.buscar(dto);

        //verificação
        assertNotEquals(0,lancamentosBuscados.size());
        assertNotNull(lancamentosBuscados);
    }
    
    @Test
    public void deveAtualizarOStatusDoLancamento(){
        //cenario
        Lancamento lancamentoParaAtualizarStatus = criarLancamento();
        lancamentoParaAtualizarStatus.setId(1L);
        lancamentoParaAtualizarStatus.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        doReturn(lancamentoParaAtualizarStatus).when(lancamentoService).obterLancamentoPorId(Mockito.anyLong());
        Mockito.when(lancamentoRepository.save(Mockito.any(Lancamento.class))).thenReturn(lancamentoParaAtualizarStatus);

        //acao
        lancamentoService.atualizarStatus(lancamentoParaAtualizarStatus.getId(),novoStatus);

        //verificacao
        assertEquals(novoStatus, lancamentoParaAtualizarStatus.getStatus());
        verify(lancamentoRepository).save(lancamentoParaAtualizarStatus);
    }

    @Test
    public void NaoDeveAtualizarOStatusDoLancamentoComIdInexistente(){
        //cenario
        //acao
        try {
            lancamentoService.atualizarStatus(1L, StatusLancamento.EFETIVADO);
            fail();
        }catch (ObjetoNaoEncontradoException e){
            assertEquals("não foi encontrado um lançamento com o id: 1", e.getMessage());
        }
    }

    @Test
    public void deveValidarUmLancamentoComSucesso(){
        //cenário
        Lancamento lancamento = criarLancamento();
        Usuario usuario = criarUsuario();
        lancamento.setId(2L);
        lancamento.setUsuario(usuario);

        //verificação
        assertDoesNotThrow(() -> {
            lancamentoService.validar(lancamento);
        });
    }

    @Test
    public void deveLancarErroAoValidarUmLancamentoIncompleto(){
        //cenário
        Lancamento lancamento = new Lancamento();

        Exception exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe uma Descrição válida.", exception.getMessage());

        lancamento.setDescricao("");
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe uma Descrição válida.", exception.getMessage());

        lancamento.setDescricao("Descrição do lançamento");
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Mês válido.", exception.getMessage());

        lancamento.setMes(0);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Mês válido.", exception.getMessage());

        lancamento.setMes(13);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Mês válido.", exception.getMessage());

        lancamento.setMes(5);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Ano válido.", exception.getMessage());

        lancamento.setAno(123);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Ano válido.", exception.getMessage());

        lancamento.setAno(2000);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Usuário.", exception.getMessage());

        Usuario usuario = criarUsuario();
        lancamento.setUsuario(usuario);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Valor maior que 0.", exception.getMessage());

        lancamento.setValor(BigDecimal.ZERO);
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um Valor maior que 0.", exception.getMessage());

        lancamento.setValor(BigDecimal.valueOf(10));
        exception = assertThrows(RegraNegocioException.class, () -> lancamentoService.validar(lancamento));
        assertEquals("Informe um tipo de lançamento.", exception.getMessage());
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

    private LancamentoDTO criarDto() {
        return LancamentoDTO.builder()
                .descricao("descricao")
                .mes(1)
                .usuario(1L)
                .ano(2000)
                .valor(BigDecimal.valueOf(10))
                .tipo("RECEITA")
                .status("PENDENTE")
                .build();
    }

    private Usuario criarUsuario(){
        return Usuario.builder()
                .id(1L)
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }
}
