package com.mponte.minhasfinancas.services.interfaces;

import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.repositories.UsuarioRepository;
import com.mponte.minhasfinancas.services.exceptions.ErroAutenticacao;
import com.mponte.minhasfinancas.services.exceptions.ObjetoNaoEncontradoException;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.impl.UsuarioServiceImpl;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @InjectMocks @Spy
    UsuarioServiceImpl usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

//    @Before
//    public void setUp(){
//        usuarioService = Mockito.spy(UsuarioServiceImpl.class);
//    }

    @Test
    public void deveSalvarUmUsuario(){
        //cenario
        doNothing().when(usuarioService).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();

        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //ação
        Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());

        //verificação
        assertNotNull(usuarioSalvo);
        assertEquals(usuarioSalvo.getId(), 1L);
        assertEquals(usuarioSalvo.getNome(), "nome");
        assertEquals(usuarioSalvo.getEmail(), "email@email.com");
        assertEquals(usuarioSalvo.getSenha(), "senha");
    }

    @Test()
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado(){
        //cenario
        String email = "email@email.com";
        Usuario usuario = Usuario.builder()
                .email(email)
                .build();
        doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);

        //ação
        Exception exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.salvarUsuario(usuario);
        });
//        try {
//            Usuario usuarioNaoSalvo = usuarioService.salvarUsuario(usuario);
//            fail();
//        }catch(RegraNegocioException e){
//            assertTrue(true);
//        }
        //verificacao
        verify(usuarioRepository, never()).save(usuario);
    }

    @Test()
    public void deveValidarEmail(){
        //cenario
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        //ação
        usuarioService.validarEmail("email@email.com");
    }

    @Test()
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        //cenario
        when(usuarioRepository.existsByEmail("email@email.com")).thenReturn(true);

        Exception exception = assertThrows(RegraNegocioException.class, () -> {
            usuarioService.validarEmail("email@email.com");
        });

        String expectedMessage = "Já existe um usuário cadastrado com este e-mail.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deveAuntenticarUmUsuarioComSucesso(){
        //Arrange
        String email = "email@email.com";
        String senha = "senha";
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //Act
        Usuario resultado = usuarioService.autenticar(email,senha);

        //Assert
        assertNotNull(resultado);
    }

    @Test
    public void deveLancarErroAoTentarAuntenticarUmUsuarioComEmailNaoCadastrado(){
        //Arrange
        String email = "email@email.com";
        String senha = "senha";
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //Act
        Exception exception = assertThrows(ObjetoNaoEncontradoException.class, () -> usuarioService.autenticar(email,senha));
        String resultadoEsperado = "Usuario não encontrado com esse email.";

        //Assert
        assertEquals(resultadoEsperado, exception.getMessage());
    }

    @Test
    public void deveLancarErroAoTentarAuntenticarUmUsuarioComSenhaIncorreta(){
        //Arrange
        String email = "email@email.com";
        String senha = "senha";
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //Act
        Exception exception = assertThrows(ErroAutenticacao.class, () -> usuarioService.autenticar(email,"123"));
        String resultadoEsperado = "Senha incorreta.";

        //Assert
        assertEquals(resultadoEsperado, exception.getMessage());
    }
}
