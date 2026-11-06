
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.financas.categoria.CategoriaService;
import com.wenderson.meuimovel.domain.financas.categoria.DadosAtualizacaoCategoria;
import com.wenderson.meuimovel.domain.financas.categoria.DadosCadastroCategoria;
import com.wenderson.meuimovel.domain.financas.categoria.DadosDetalhamentoCategoria;
import com.wenderson.meuimovel.enums.TipoCategoria;
import com.wenderson.meuimovel.infra.security.SecurityConfig;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = CategoriaController.class,
        properties = {
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://supabase.invalid/auth/v1",
                "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://supabase.invalid/auth/v1/.well-known/jwks.json"
        }
)

@Import({SecurityConfig.class, UsuarioAutenticado.class})
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void deveCadastrarCategoriaComUsuarioAutenticado() throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        var resposta = new DadosDetalhamentoCategoria(
                categoriaId,
                "Supermercado",
                TipoCategoria.DESPESA,
                true
        );

        when(service.cadastrar(
                eq(familiaId),
                eq(usuarioId),
                any(DadosCadastroCategoria.class)
        )).thenReturn(resposta);

        String json = """
                {
                    "nome": "Supermercado",
                    "tipo": "DESPESA"
                }
                """;

        mockMvc.perform(
                        post("/familias/{familiaId}/financas/categorias", familiaId)
                                .with(jwt().jwt(token -> token
                                        .subject(usuarioId.toString())
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(categoriaId.toString()))
                .andExpect(jsonPath("$.nome")
                        .value("Supermercado"))
                .andExpect(jsonPath("$.tipo")
                        .value("DESPESA"))
                .andExpect(jsonPath("$.ativa")
                        .value(true));

        verify(service).cadastrar(
                eq(familiaId),
                eq(usuarioId),
                any(DadosCadastroCategoria.class)
        );

    }


    @Test
    void deveNegarCadastroSemAutenticacao() throws Exception {

        UUID familiaId = UUID.randomUUID();

        String json = """
            {
                "nome": "Supermercado",
                "tipo": "DESPESA"
            }
            """;

        mockMvc.perform(
                        post("/familias/{familiaId}/financas/categorias", familiaId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isUnauthorized());

        verify(service, org.mockito.Mockito.never())
                .cadastrar(
                        any(),
                        any(),
                        any(DadosCadastroCategoria.class)
                );
    }


    @Test
    void deveRejeitarCadastroComNomeVazio() throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        String json = """
            {
                "nome": "",
                "tipo": "DESPESA"
            }
            """;

        mockMvc.perform(
                        post("/familias/{familiaId}/financas/categorias", familiaId)
                                .with(jwt().jwt(token -> token
                                        .subject(usuarioId.toString())
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(service, org.mockito.Mockito.never())
                .cadastrar(
                        any(),
                        any(),
                        any(DadosCadastroCategoria.class)
                );
    }


    @Test
    void deveListarCategoriasComUsuarioAutenticado() throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        var categoria = new DadosDetalhamentoCategoria(
                categoriaId,
                "Supermercado",
                TipoCategoria.DESPESA,
                true
        );

        when(service.listar(familiaId, usuarioId))
                .thenReturn(List.of(categoria));

        mockMvc.perform(
                        get("/familias/{familiaId}/financas/categorias", familiaId)
                                .with(jwt().jwt(token -> token
                                        .subject(usuarioId.toString())
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(categoriaId.toString()))
                .andExpect(jsonPath("$[0].nome")
                        .value("Supermercado"))
                .andExpect(jsonPath("$[0].tipo")
                        .value("DESPESA"))
                .andExpect(jsonPath("$[0].ativa")
                        .value(true));

        verify(service).listar(familiaId, usuarioId);
    }


    @Test
    void deveConsultarCategoriaPorIdComUsuarioAutenticado() throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        var categoria = new DadosDetalhamentoCategoria(
                categoriaId,
                "Energia elétrica",
                TipoCategoria.DESPESA,
                true
        );

        when(service.buscarPorId(
                familiaId,
                usuarioId,
                categoriaId
        )).thenReturn(categoria);

        mockMvc.perform(
                        get(
                                "/familias/{familiaId}/financas/categorias/{id}",
                                familiaId,
                                categoriaId
                        )
                                .with(jwt().jwt(token -> token
                                        .subject(usuarioId.toString())
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(categoriaId.toString()))
                .andExpect(jsonPath("$.nome")
                        .value("Energia elétrica"))
                .andExpect(jsonPath("$.tipo")
                        .value("DESPESA"))
                .andExpect(jsonPath("$.ativa")
                        .value(true));

        verify(service).buscarPorId(
                familiaId,
                usuarioId,
                categoriaId
        );
    }


    @Test
    void deveAtualizarCategoriaComUsuarioAutenticado() throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        var resposta = new DadosDetalhamentoCategoria(
                categoriaId,
                "Compras de supermercado",
                TipoCategoria.DESPESA,
                true
        );

        when(service.atualizar(
                eq(familiaId),
                eq(usuarioId),
                eq(categoriaId),
                any(DadosAtualizacaoCategoria.class)
        )).thenReturn(resposta);

        String json = """
            {
                "nome": "Compras de supermercado"
            }
            """;

        mockMvc.perform(
                        put(
                                "/familias/{familiaId}/financas/categorias/{id}",
                                familiaId,
                                categoriaId
                        )
                                .with(jwt().jwt(token -> token
                                        .subject(usuarioId.toString())
                                ))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(categoriaId.toString()))
                .andExpect(jsonPath("$.nome")
                        .value("Compras de supermercado"))
                .andExpect(jsonPath("$.tipo")
                        .value("DESPESA"))
                .andExpect(jsonPath("$.ativa")
                        .value(true));

        verify(service).atualizar(
                eq(familiaId),
                eq(usuarioId),
                eq(categoriaId),
                any(DadosAtualizacaoCategoria.class)
        );
    }


    @Test
    void deveDesativarCategoriaComUsuarioAutenticado() throws Exception {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                "/familias/{familiaId}/financas/categorias/{id}",
                                familiaId,
                                categoriaId
                        )
                                .with(jwt().jwt(token -> token
                                        .subject(usuarioId.toString())
                                ))
                )
                .andExpect(status().isNoContent());

        verify(service).desativar(
                familiaId,
                usuarioId,
                categoriaId
        );
    }



}
