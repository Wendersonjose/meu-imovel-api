
package com.wenderson.meuimovel.domain.financas;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ResumoCompetenciaService {

    private final ResumoCompetenciaRepository repository;
    private final AutorizacaoFamiliaService autorizacaoFamiliaService;

    public ResumoCompetenciaService(
            ResumoCompetenciaRepository repository,
            AutorizacaoFamiliaService autorizacaoFamiliaService
    ) {
        this.repository = repository;
        this.autorizacaoFamiliaService = autorizacaoFamiliaService;
    }

    @Transactional(readOnly = true)
    public List<DadosResumoCompetencia> listarPorFamilia(
            UUID familiaId,
            UUID usuarioId
    ) {

        // Primeiro verifica se o usuario pode acessar a familia.
        autorizacaoFamiliaService.verificarAcesso(
                familiaId,
                usuarioId
        );

        // Somente usuarios autorizados consultam o resumo.
        return repository.listarPorFamilia(familiaId);
    }
}
