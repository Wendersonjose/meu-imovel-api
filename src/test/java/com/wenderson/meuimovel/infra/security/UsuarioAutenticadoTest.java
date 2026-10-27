
package com.wenderson.meuimovel.infra.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.util.List;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioAutenticadoTest {

    private final UsuarioAutenticado usuarioAutenticado =
            new UsuarioAutenticado();

    @AfterEach
    void limparContextoSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveObterUsuarioIdDoJwtAutenticado() {

        // Cria um UUID ficticio para o usuario.
        UUID usuarioId = UUID.randomUUID();

        // Monta um JWT ficticio com o UUID no campo sub.
        Jwt jwt = Jwt.withTokenValue("token-teste")
                .header("alg", "ES256")
                .subject(usuarioId.toString())
                .build();

        // Simula uma autenticacao JWT ja realizada.
        var autenticacao = new JwtAuthenticationToken(
                jwt,
                List.of()
        );

        SecurityContextHolder.getContext()
                .setAuthentication(autenticacao);

        // Recupera o UUID pela classe que estamos testando.
        UUID resultado = usuarioAutenticado.obterUsuarioId();

        // Confirma que o UUID extraido corresponde ao original.
        assertEquals(usuarioId, resultado);
    }


    @Test
    void deveNegarAcessoQuandoUsuarioNaoEstaAutenticado() {

        // Garante que nao exista autenticacao no contexto.
        SecurityContextHolder.clearContext();

        // Tenta recuperar o identificador sem um JWT.
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> usuarioAutenticado.obterUsuarioId()
        );
    }


    @Test
    void deveRejeitarJwtComUsuarioIdInvalido() {

        // Cria um JWT ficticio com subject invalido.
        Jwt jwt = Jwt.withTokenValue("token-teste")
                .header("alg", "ES256")
                .subject("usuario-invalido")
                .build();

        // Simula uma autenticacao JWT ja realizada.
        var autenticacao = new JwtAuthenticationToken(
                jwt,
                List.of()
        );

        SecurityContextHolder.getContext()
                .setAuthentication(autenticacao);

        // Verifica se a conversao para UUID e rejeitada.
        assertThrows(
                BadCredentialsException.class,
                () -> usuarioAutenticado.obterUsuarioId()
        );
    }


}
