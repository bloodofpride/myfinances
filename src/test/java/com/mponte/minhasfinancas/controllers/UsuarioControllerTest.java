package com.mponte.minhasfinancas.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mponte.minhasfinancas.model.dtos.UsuarioDTO;
import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.services.exceptions.ErroAutenticacao;
import com.mponte.minhasfinancas.services.exceptions.ObjetoNaoEncontradoException;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.interfaces.LancamentoService;
import com.mponte.minhasfinancas.services.interfaces.UsuarioService;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {
    static final String API = "/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService usuarioService;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    public void deveAutenticarUmUsuario() throws Exception {
        //cenário
        String email = "usuario@email.com";
        String senha = "123";

        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuarioAutenticado = Usuario.builder().id(1L).email(email).senha(senha).build();

        Mockito.when(usuarioService.autenticar(email, senha)).thenReturn(usuarioAutenticado);

        String json = new ObjectMapper().writeValueAsString(usuarioDTO);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                    .post(API.concat("/autenticar"))
                                                    .accept(JSON)
                                                    .contentType(JSON)
                                                    .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuarioAutenticado.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuarioAutenticado.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuarioAutenticado.getNome()));
    }

    @Test
    public void deveRetornarNotFoundAoNaoEncontrarUsuarioCadastradoComOEmailInformado() throws Exception {
        //cenário
        String email = "usuario@email.com";
        String senha = "123";

        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).senha(senha).build();
        Mockito.when(usuarioService.autenticar(email, senha)).thenThrow(ObjetoNaoEncontradoException.class);

        String json = new ObjectMapper().writeValueAsString(usuarioDTO);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deveRetornarBadRequestAoObterErroDeAutenticação() throws Exception {
        //cenário
        String email = "usuario@email.com";
        String senha = "123";

        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).senha(senha).build();
        Mockito.when(usuarioService.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);

        String json = new ObjectMapper().writeValueAsString(usuarioDTO);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deveSalvarUmUsuario() throws Exception {
        //cenário
        String email = "usuario@email.com";
        String nome = "usuario";
        String senha = "123";

        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).nome(nome).senha(senha).build();
        Usuario usuarioSalvo = Usuario.builder().id(1L).email(email).nome(nome).senha(senha).build();

        Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuarioSalvo);

        String json = new ObjectMapper().writeValueAsString(usuarioDTO);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuarioSalvo.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuarioSalvo.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuarioSalvo.getNome()));
    }

    @Test
    public void deveRetornarBadRequestAoTentarSalvarUmUsuarioJaCadastrado() throws Exception {
        //cenário
        String email = "usuario@email.com";
        String nome = "usuario";
        String senha = "123";

        UsuarioDTO usuarioDTO = UsuarioDTO.builder().email(email).nome(nome).senha(senha).build();
        Mockito.when(usuarioService.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = new ObjectMapper().writeValueAsString(usuarioDTO);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
