
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.financas.categoria.CategoriaService;
import com.wenderson.meuimovel.domain.financas.categoria.DadosAtualizacaoCategoria;
import com.wenderson.meuimovel.domain.financas.categoria.DadosCadastroCategoria;
import com.wenderson.meuimovel.domain.financas.categoria.DadosDetalhamentoCategoria;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/familias/{familiaId}/financas/categorias")
@Tag(
        name = "Categorias Financeiras",
        description = "Gerenciamento de categorias de receitas e despesas"
)
public class CategoriaController {

    private final CategoriaService service;
    private final UsuarioAutenticado usuarioAutenticado;

    public CategoriaController(
            CategoriaService service,
            UsuarioAutenticado usuarioAutenticado
    ) {
        this.service = service;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    @PostMapping
    @Operation(summary = "Cadastrar categoria financeira")
    public ResponseEntity<DadosDetalhamentoCategoria> cadastrar(
            @PathVariable UUID familiaId,
            @RequestBody @Valid DadosCadastroCategoria dados,
            UriComponentsBuilder uriBuilder
    ) {
        UUID usuarioId = usuarioAutenticado.obterUsuarioId();

        var categoria = service.cadastrar(familiaId, usuarioId, dados);

        var uri = uriBuilder
                .path("/familias/{familiaId}/financas/categorias/{id}")
                .buildAndExpand(familiaId, categoria.id())
                .toUri();

        return ResponseEntity.created(uri).body(categoria);
    }

    @GetMapping
    @Operation(summary = "Listar categorias financeiras ativas")
    public ResponseEntity<List<DadosDetalhamentoCategoria>> listar(
            @PathVariable UUID familiaId
    ) {
        UUID usuarioId = usuarioAutenticado.obterUsuarioId();

        return ResponseEntity.ok(
                service.listar(familiaId, usuarioId)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar categoria por ID")
    public ResponseEntity<DadosDetalhamentoCategoria> detalhar(
            @PathVariable UUID familiaId,
            @PathVariable UUID id
    ) {
        UUID usuarioId = usuarioAutenticado.obterUsuarioId();

        return ResponseEntity.ok(
                service.buscarPorId(familiaId, usuarioId, id)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nome de categoria financeira")
    public ResponseEntity<DadosDetalhamentoCategoria> atualizar(
            @PathVariable UUID familiaId,
            @PathVariable UUID id,
            @RequestBody @Valid DadosAtualizacaoCategoria dados
    ) {
        UUID usuarioId = usuarioAutenticado.obterUsuarioId();

        return ResponseEntity.ok(
                service.atualizar(familiaId, usuarioId, id, dados)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar categoria financeira")
    public ResponseEntity<Void> desativar(
            @PathVariable UUID familiaId,
            @PathVariable UUID id
    ) {
        UUID usuarioId = usuarioAutenticado.obterUsuarioId();

        service.desativar(familiaId, usuarioId, id);

        return ResponseEntity.noContent().build();
    }
}
