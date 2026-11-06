
package com.wenderson.meuimovel.domain.financas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumoCompetenciaServiceTest {

    @Mock
    private ResumoCompetenciaRepository repository;

    @Mock
    private AutorizacaoFamiliaService autorizacaoFamiliaService;

    @InjectMocks
    private ResumoCompetenciaService resumoCompetenciaService;

    // TESTE 1: Usuario sem permissao.
    @Test
    void naoDeveConsultarResumoQuandoUsuarioNaoTemPermissao() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        doThrow(new AccessDeniedException(
                "Usuario sem permissao para acessar esta familia"
        ))
                .when(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);

        assertThrows(
                AccessDeniedException.class,
                () -> resumoCompetenciaService.listarPorFamilia(
                        familiaId,
                        usuarioId
                )
        );

        verify(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);

        // Dados financeiros nao podem ser consultados.
        verifyNoInteractions(repository);
    }

    // TESTE 2: Usuario autorizado com dados financeiros.
    @Test
    void deveRetornarResumoQuandoUsuarioTemPermissao() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        DadosResumoCompetencia resumo =
                new DadosResumoCompetencia(
                        familiaId,
                        LocalDate.of(2026, 10, 1),
                        new BigDecimal("5000.00"),
                        new BigDecimal("3000.00"),
                        new BigDecimal("1000.00"),
                        new BigDecimal("500.00")
                );

        List<DadosResumoCompetencia> dadosEsperados =
                List.of(resumo);

        when(repository.listarPorFamilia(familiaId))
                .thenReturn(dadosEsperados);

        List<DadosResumoCompetencia> resultado =
                resumoCompetenciaService.listarPorFamilia(
                        familiaId,
                        usuarioId
                );

        // Confirma que retornou os dados esperados.
        assertEquals(dadosEsperados, resultado);

        // Confirma a ordem: autorizar antes de consultar.
        var ordem = inOrder(
                autorizacaoFamiliaService,
                repository
        );

        ordem.verify(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);

        ordem.verify(repository)
                .listarPorFamilia(familiaId);

        ordem.verifyNoMoreInteractions();
    }

    // TESTE 3: Usuario autorizado sem lancamentos.
    @Test
    void deveRetornarListaVaziaQuandoNaoExistemLancamentos() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        when(repository.listarPorFamilia(familiaId))
                .thenReturn(List.of());

        List<DadosResumoCompetencia> resultado =
                resumoCompetenciaService.listarPorFamilia(
                        familiaId,
                        usuarioId
                );

        assertTrue(resultado.isEmpty());

        verify(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);

        verify(repository)
                .listarPorFamilia(familiaId);
    }
}
