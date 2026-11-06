
package com.wenderson.meuimovel.infra.security;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AutorizacaoFamiliaUsuarioAtual {

    private final UsuarioAutenticado usuarioAutenticado;
    private final AutorizacaoFamiliaService autorizacaoFamiliaService;

    public AutorizacaoFamiliaUsuarioAtual(
            UsuarioAutenticado usuarioAutenticado,
            AutorizacaoFamiliaService autorizacaoFamiliaService
    ) {
        this.usuarioAutenticado = usuarioAutenticado;
        this.autorizacaoFamiliaService = autorizacaoFamiliaService;
    }

    public void verificarAcesso(UUID familiaId) {

        // Identifica o usuario pelo JWT autenticado.
        UUID usuarioId = usuarioAutenticado.obterUsuarioId();

        // Verifica se o usuario possui vínculo ativo com a familia.
        autorizacaoFamiliaService.verificarAcesso(
                familiaId,
                usuarioId
        );
    }
}
