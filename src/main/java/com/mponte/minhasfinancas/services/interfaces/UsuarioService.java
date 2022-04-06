package com.mponte.minhasfinancas.services.interfaces;

import com.mponte.minhasfinancas.model.entities.Usuario;

public interface UsuarioService {
    Usuario autenticar(String email, String senha);
    Usuario salvarUsuario(Usuario usuario);
    void validarEmail(String email);

    Usuario findById(Long usuario);
}
