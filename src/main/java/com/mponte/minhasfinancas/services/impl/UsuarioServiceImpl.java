package com.mponte.minhasfinancas.services.impl;

import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.repositories.UsuarioRepository;
import com.mponte.minhasfinancas.services.exceptions.ErroAutenticacao;
import com.mponte.minhasfinancas.services.exceptions.ObjetoNaoEncontradoException;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.interfaces.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("Usuario não encontrado com esse email."));
        if(!usuario.getSenha().equals(senha)){
            throw new ErroAutenticacao("Senha incorreta.");
        }
        return usuario;
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = usuarioRepository.existsByEmail(email);
        if (existe){
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjetoNaoEncontradoException("usuário não encontrado com o id: "+id));
    }
}
