
package com.wenderson.meuimovel.domain.financas.categoria;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import com.wenderson.meuimovel.enums.TipoCategoria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @Mock
    private AutorizacaoFamiliaService autorizacaoFamiliaService;

    private CategoriaService service;

    @BeforeEach
    void configurar() {
        service = new CategoriaService(
                repository,
                autorizacaoFamiliaService
        );
    }

    @Test
    void deveImpedirCadastroQuandoUsuarioNaoPertenceAFamilia() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        var dados = new DadosCadastroCategoria(
                "Supermercado",
                TipoCategoria.DESPESA
        );

        doThrow(new AccessDeniedException("Acesso negado"))
                .when(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);

        assertThrows(
                AccessDeniedException.class,
                () -> service.cadastrar(familiaId, usuarioId, dados)
        );

        verify(autorizacaoFamiliaService)
                .verificarAcesso(familiaId, usuarioId);

        verifyNoInteractions(repository);
    }

    @Test
    void deveImpedirCadastroDeCategoriaDuplicada() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        var dados = new DadosCadastroCategoria(
                "Supermercado",
                TipoCategoria.DESPESA
        );

        when(repository.existsByFamiliaIdAndTipoAndNomeIgnoreCase(
                familiaId,
                TipoCategoria.DESPESA,
                "Supermercado"
        )).thenReturn(true);

        var exception = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> service.cadastrar(familiaId, usuarioId, dados)
        );

        assertEquals(
                org.springframework.http.HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(repository, never()).saveAndFlush(any(Categoria.class));
    }

}
