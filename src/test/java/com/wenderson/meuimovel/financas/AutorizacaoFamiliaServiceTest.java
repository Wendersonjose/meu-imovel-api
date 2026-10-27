
package com.wenderson.meuimovel.domain.financas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacaoFamiliaServiceTest {

    @Mock
    private MembroFamiliaRepository membroFamiliaRepository;

    @InjectMocks
    private AutorizacaoFamiliaService autorizacaoFamiliaService;

    @Test
    void devePermitirAcessoQuandoUsuarioPossuiVinculoAtivo() {

        // Identificadores ficticios para o teste.
        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Simula um vinculo ativo no banco.
        when(membroFamiliaRepository.existeVinculoAtivo(
                familiaId, usuarioId
        )).thenReturn(true);

        // A autorizacao nao deve lancar excecao.
        assertDoesNotThrow(() ->
                autorizacaoFamiliaService.verificarAcesso(
                        familiaId, usuarioId
                )
        );

        // Confirma que o Repository foi consultado.
        verify(membroFamiliaRepository).existeVinculoAtivo(
                familiaId, usuarioId
        );
    }

    @Test
    void deveNegarAcessoQuandoUsuarioNaoPossuiVinculoAtivo() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Simula ausencia de vinculo ativo.
        when(membroFamiliaRepository.existeVinculoAtivo(
                familiaId, usuarioId
        )).thenReturn(false);

        // O Service deve negar o acesso.
        assertThrows(
                AccessDeniedException.class,
                () -> autorizacaoFamiliaService.verificarAcesso(
                        familiaId, usuarioId
                )
        );

        verify(membroFamiliaRepository).existeVinculoAtivo(
                familiaId, usuarioId
        );
    }
}
