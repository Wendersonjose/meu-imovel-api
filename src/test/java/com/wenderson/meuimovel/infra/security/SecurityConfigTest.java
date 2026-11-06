
package com.wenderson.meuimovel.infra.security;

import com.wenderson.meuimovel.controller.ImovelController;
import com.wenderson.meuimovel.domain.imovel.ImovelService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.oauth2.jwt.BadJwtException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.springframework.security.oauth2.jwt.Jwt;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(
        controllers = ImovelController.class,
        properties = {
                "SUPABASE_URL=https://supabase.invalid"
        }
)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // Simula o service sem acessar o banco.
    @MockitoBean
    private ImovelService imovelService;

    // Simula o validador JWT sem acessar o Supabase.
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void deveRetornar401QuandoUsuarioNaoEstaAutenticado()
            throws Exception {

        mockMvc.perform(get("/imoveis"))
                .andExpect(status().isUnauthorized());

    }


        @Test
        void deveRetornar401QuandoTokenJwtForInvalido()
        throws Exception {

            // Simula a rejeicao de qualquer token JWT.
            when(jwtDecoder.decode(anyString()))
                    .thenThrow(new BadJwtException("JWT invalido"));

            // Envia uma requisicao com um token invalido.
            mockMvc.perform(get("/imoveis")
                            .header(
                                    "Authorization",
                                    "Bearer token-invalido"
                            ))
                    .andExpect(status().isUnauthorized());
        }


    @Test
    void devePermitirAcessoQuandoTokenJwtForValido()
            throws Exception {

        // Cria um JWT ficticio para o teste.
        var jwt = Jwt.withTokenValue("token-teste-valido")
                .header("alg", "ES256")
                .subject("usuario-teste")
                .claim("aud", List.of("authenticated"))
                .build();

        // Simula um JWT aceito pelo validador.
        when(jwtDecoder.decode("token-teste-valido"))
                .thenReturn(jwt);

        // Simula uma lista vazia de imoveis.
        // Nenhuma consulta ao banco sera realizada.
        when(imovelService.listar())
                .thenReturn(List.of());

        // Envia a requisicao com o token.
        mockMvc.perform(get("/imoveis")
                        .header(
                                "Authorization",
                                "Bearer token-teste-valido"
                        ))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


}

