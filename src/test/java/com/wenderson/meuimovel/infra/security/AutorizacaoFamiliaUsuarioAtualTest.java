
package com.wenderson.meuimovel.infra.security;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import static org.mockito.Mockito.doThrow;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacaoFamiliaUsuarioAtualTest {

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @Mock
    private AutorizacaoFamiliaService autorizacaoFamiliaService;

    @InjectMocks
    private AutorizacaoFamiliaUsuarioAtual autorizacaoFamiliaUsuarioAtual;

    @Test
    void deveVerificarAcessoComUsuarioDoJwt() {

        // Identificadores ficticios.
        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Simula o UUID extraido do JWT autenticado.
        when(usuarioAutenticado.obterUsuarioId())
                .thenReturn(usuarioId);

        // Executa a autorizacao para a familia.
        autorizacaoFamiliaUsuarioAtual.verificarAcesso(familiaId);

        // Confirma que o usuario foi identificado.
        verify(usuarioAutenticado).obterUsuarioId();

        // Confirma que o Service recebeu os UUIDs corretos.
        verify(autorizacaoFamiliaService).verificarAcesso(
                familiaId,
                usuarioId
        );
    }

    @Test
    void naoDeveConsultarAutorizacaoQuandoUsuarioNaoEstaAutenticado() {

        UUID familiaId = UUID.randomUUID();

        // Simula a ausencia de usuario autenticado.
        when(usuarioAutenticado.obterUsuarioId())
                .thenThrow(
                        new AuthenticationCredentialsNotFoundException(
                                "Usuario nao autenticado"
                        )
                );

        // A tentativa deve ser rejeitada.
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> autorizacaoFamiliaUsuarioAtual
                        .verificarAcesso(familiaId)
        );

        // O Service de autorizacao não deve ser chamado.
        verifyNoInteractions(autorizacaoFamiliaService);
    }


    @Test
    void deveNegarAcessoQuandoUsuarioNaoPertenceAFamilia() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Simula um usuario autenticado.
        when(usuarioAutenticado.obterUsuarioId())
                .thenReturn(usuarioId);

        // Simula a negativa de acesso pelo Service.
        doThrow(new AccessDeniedException(
                "Usuario sem permissao para acessar esta familia"
        ))
                .when(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);

        // Confirma que a negativa e preservada.
        assertThrows(
                AccessDeniedException.class,
                () -> autorizacaoFamiliaUsuarioAtual
                        .verificarAcesso(familiaId)
        );

        // Confirma que os UUIDs corretos foram utilizados.
        verify(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);
    }


}
