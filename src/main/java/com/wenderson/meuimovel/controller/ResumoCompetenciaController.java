
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.financas.DadosResumoCompetencia;
import com.wenderson.meuimovel.domain.financas.ResumoCompetenciaService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/familias/{familiaId}/financas")
public class ResumoCompetenciaController {

    private final ResumoCompetenciaService service;
    private final UsuarioAutenticado usuarioAutenticado;

    public ResumoCompetenciaController(
            ResumoCompetenciaService service,
            UsuarioAutenticado usuarioAutenticado
    ) {
        this.service = service;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    @GetMapping("/resumo")
    public ResponseEntity<List<DadosResumoCompetencia>> listar(
            @PathVariable UUID familiaId
    ) {

        // Identifica o usuario autenticado pelo JWT.
        UUID usuarioId = usuarioAutenticado.obterUsuarioId();

        // O Service verifica a autorizacao antes da consulta.
        var resumos = service.listarPorFamilia(
                familiaId,
                usuarioId
        );

        return ResponseEntity.ok(resumos);
    }
}
