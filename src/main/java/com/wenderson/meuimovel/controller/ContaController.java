package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.financas.conta.ContaService;
import com.wenderson.meuimovel.domain.financas.conta.DadosCadastroConta;
import com.wenderson.meuimovel.domain.financas.conta.DadosDetalhamentoConta;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/familias/{familiaId}/financas/contas")
public class ContaController {
    private final ContaService service;
    private final UsuarioAutenticado usuario;

    public ContaController(ContaService service, UsuarioAutenticado usuario) {
        this.service = service;
        this.usuario = usuario;
    }

    @PostMapping
    public ResponseEntity<DadosDetalhamentoConta> cadastrar(
            @PathVariable UUID familiaId, @Valid @RequestBody DadosCadastroConta dados) {
        var conta = service.cadastrar(familiaId, usuario.obterUsuarioId(), dados);
        return ResponseEntity.created(URI.create(
                "/familias/" + familiaId + "/financas/contas/" + conta.id())).body(conta);
    }

    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoConta>> listar(@PathVariable UUID familiaId) {
        return ResponseEntity.ok(service.listar(familiaId, usuario.obterUsuarioId()));
    }
}
