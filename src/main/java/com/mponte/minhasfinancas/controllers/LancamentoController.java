package com.mponte.minhasfinancas.controllers;

import com.mponte.minhasfinancas.model.dtos.LancamentoDTO;
import com.mponte.minhasfinancas.model.entities.Lancamento;
import com.mponte.minhasfinancas.model.enums.StatusLancamento;
import com.mponte.minhasfinancas.services.interfaces.LancamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {
    private LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @PostMapping
    public ResponseEntity<Lancamento> salvar(@RequestBody LancamentoDTO dto){
        Lancamento lancamento = lancamentoService.salvar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(lancamento.getId()).toUri();
        return ResponseEntity.created(uri).body(lancamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable Long id, @RequestBody LancamentoDTO dto){
        lancamentoService.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> atualizarStatus(@PathVariable Long id, @RequestParam String status){
        try {
            lancamentoService.atualizarStatus(id, StatusLancamento.valueOf(status));
            return ResponseEntity.noContent().build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("não foi possível atualizar o status do lançamento, envie um status válido.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        lancamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<Lancamento>> buscaFiltro(
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano,
            @RequestParam Long usuario){
        LancamentoDTO dto = LancamentoDTO.builder().descricao(descricao).mes(mes).ano(ano).usuario(usuario).build();
        return ResponseEntity.ok().body(lancamentoService.buscar(dto));
    }
}
