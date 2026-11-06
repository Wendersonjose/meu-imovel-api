
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.financas.DadosResumoCompetencia;
import com.wenderson.meuimovel.domain.financas.ResumoCompetenciaService;
import com.wenderson.meuimovel.infra.security.SecurityConfig;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ResumoCompetenciaController.class,
        properties = {
                "SUPABASE_URL=https://supabase.invalid"
        }
)
@Import({
        SecurityConfig.class,
        UsuarioAutenticado.class
})
class ResumoCompetenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResumoCompetenciaService service;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // TESTE 1 - Requisicao sem autenticacao.
    @Test
    void deveRetornar401SemJwt() throws Exception {

        UUID familiaId = UUID.randomUUID();

        mockMvc.perform(get(
                        "/familias/{familiaId}/financas/resumo",
                        familiaId
                ))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    // TESTE 2 - Usuario autenticado e autorizado.
    @Test
    void deveRetornar200ComResumoFinanceiro()
            throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        var resumo = new DadosResumoCompetencia(
                familiaId,
                LocalDate.of(2026, 10, 1),
                new BigDecimal("5000.00"),
                new BigDecimal("3000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("500.00")
        );

        when(service.listarPorFamilia(familiaId, usuarioId))
                .thenReturn(List.of(resumo));

        mockMvc.perform(
                        get(
                                "/familias/{familiaId}/financas/resumo",
                                familiaId
                        )
                                .with(jwt().jwt(token ->
                                        token.subject(usuarioId.toString())
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].familiaId")
                        .value(familiaId.toString()))
                .andExpect(jsonPath("$[0].receitasEfetivadas")
                        .value(5000.00))
                .andExpect(jsonPath("$[0].despesasEfetivadas")
                        .value(3000.00));

        verify(service).listarPorFamilia(
                familiaId,
                usuarioId
        );
    }

    // TESTE 3 - Usuario autenticado sem permissao.
    @Test
    void deveRetornar403SemPermissaoNaFamilia()
            throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        when(service.listarPorFamilia(familiaId, usuarioId))
                .thenThrow(new AccessDeniedException(
                        "Usuario sem permissao para acessar esta familia"
                ));

        mockMvc.perform(
                        get(
                                "/familias/{familiaId}/financas/resumo",
                                familiaId
                        )
                                .with(jwt().jwt(token ->
                                        token.subject(usuarioId.toString())
                                ))
                )
                .andExpect(status().isForbidden());

        verify(service).listarPorFamilia(
                familiaId,
                usuarioId
        );
    }
}
