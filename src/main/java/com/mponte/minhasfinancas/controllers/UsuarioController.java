package com.mponte.minhasfinancas.controllers;

import com.mponte.minhasfinancas.model.dtos.UsuarioDTO;
import com.mponte.minhasfinancas.model.entities.Usuario;
import com.mponte.minhasfinancas.services.exceptions.RegraNegocioException;
import com.mponte.minhasfinancas.services.interfaces.LancamentoService;
import com.mponte.minhasfinancas.services.interfaces.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private UsuarioService usuarioService;
    private LancamentoService lancamentoService;

    public UsuarioController(UsuarioService usuarioService, LancamentoService lancamentoService) {
        this.usuarioService = usuarioService;
        this.lancamentoService = lancamentoService;
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

    @GetMapping("/saldo/{id}")
    public ResponseEntity<BigDecimal> saldoUsuario(@PathVariable("id") Long idUsuario){
        return ResponseEntity.ok().body(lancamentoService.saldoUsuario(idUsuario));
    }
}

