package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.financas.lancamento.DadosCadastroLancamento;
import com.wenderson.meuimovel.domain.financas.lancamento.DadosDetalhamentoLancamento;
import com.wenderson.meuimovel.domain.financas.lancamento.DadosPagamentoLancamento;
import com.wenderson.meuimovel.domain.financas.lancamento.LancamentoService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/familias/{familiaId}/financas/lancamentos")
public class LancamentoController {
    private final LancamentoService service;
    private final UsuarioAutenticado usuario;

    public LancamentoController(LancamentoService service, UsuarioAutenticado usuario) {
        this.service = service;
        this.usuario = usuario;
    }

    @PostMapping
    public ResponseEntity<DadosDetalhamentoLancamento> cadastrar(
            @PathVariable UUID familiaId, @Valid @RequestBody DadosCadastroLancamento dados) {
        var lancamento = service.cadastrar(familiaId, usuario.obterUsuarioId(), dados);
        return ResponseEntity.created(URI.create(
                "/familias/" + familiaId + "/financas/lancamentos/" + lancamento.id()))
                .body(lancamento);
    }

    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoLancamento>> listar(
            @PathVariable UUID familiaId,
            @RequestParam(required = false) String mes,
            @RequestParam(required = false) Long imovelId) {
        YearMonth competencia = null;
        if (mes != null) {
            try {
                competencia = YearMonth.parse(mes);
            } catch (DateTimeParseException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Mes invalido. Informe AAAA-MM");
            }
        }
        return ResponseEntity.ok(service.listar(
                familiaId, usuario.obterUsuarioId(), competencia, imovelId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoLancamento> buscar(
            @PathVariable UUID familiaId, @PathVariable UUID id) {
        return ResponseEntity.ok(service.buscar(
                familiaId, usuario.obterUsuarioId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoLancamento> atualizar(
            @PathVariable UUID familiaId, @PathVariable UUID id,
            @Valid @RequestBody DadosCadastroLancamento dados) {
        return ResponseEntity.ok(service.atualizar(
                familiaId, usuario.obterUsuarioId(), id, dados));
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<DadosDetalhamentoLancamento> pagar(
            @PathVariable UUID familiaId, @PathVariable UUID id,
            @Valid @RequestBody DadosPagamentoLancamento dados) {
        return ResponseEntity.ok(service.registrarPagamento(
                familiaId, usuario.obterUsuarioId(), id, dados));
    }
}
