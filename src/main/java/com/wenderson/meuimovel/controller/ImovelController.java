package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.imovel.DadosAtualizacaoImovel;
import com.wenderson.meuimovel.domain.imovel.DadosCadastroImovel;
import com.wenderson.meuimovel.domain.imovel.DadosDetalhamentoImovel;
import com.wenderson.meuimovel.domain.imovel.ImovelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/imoveis")
public class ImovelController {

    private final ImovelService service;

    public ImovelController(ImovelService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosDetalhamentoImovel> cadastrar(
            @RequestBody @Valid DadosCadastroImovel dados,
            UriComponentsBuilder uriBuilder
    ) {

        var imovel = service.cadastrar(dados);

        var uri = uriBuilder
                .path("/imoveis/{id}")
                .buildAndExpand(imovel.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(new DadosDetalhamentoImovel(imovel));
    }

    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoImovel>> listar() {

        var imoveis = service.listar();

        var dados = imoveis.stream()
                .map(DadosDetalhamentoImovel::new)
                .toList();

        return ResponseEntity.ok(dados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoImovel> detalhar(
            @PathVariable Long id
    ) {

        var imovel = service.buscarPorId(id);

        return ResponseEntity.ok(
                new DadosDetalhamentoImovel(imovel)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoImovel> atualizar(
            @PathVariable Long id,
            @RequestBody DadosAtualizacaoImovel dados
    ) {
        var imovel = service.atualizar(id, dados);

        return ResponseEntity.ok(
                new DadosDetalhamentoImovel(imovel)
        );
    }
}