package com.mponte.minhasfinancas.controllers;

import com.mponte.minhasfinancas.model.dtos.UsuarioDTO;
import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.interfaces.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Usuario> salvarUsuario(@RequestBody UsuarioDTO dto){
        Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();
        usuario = usuarioService.salvarUsuario(usuario);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @PostMapping("/autenticar")
    public ResponseEntity<Usuario> autenticarUsuario(@RequestBody UsuarioDTO dto){
        Usuario usuarioAutenticado = usuarioService.autenticar(dto.getEmail(),dto.getSenha());
        return ResponseEntity.ok().body(usuarioAutenticado);
    }
}

