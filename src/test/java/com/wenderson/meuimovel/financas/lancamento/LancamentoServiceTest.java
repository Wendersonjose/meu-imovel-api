package com.wenderson.meuimovel.domain.financas.lancamento;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import com.wenderson.meuimovel.enums.TipoCategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest {
    @Mock private LancamentoRepository repository;
    @Mock private AutorizacaoFamiliaService autorizacao;
    private LancamentoService service;

    @BeforeEach
    void preparar() {
        service = new LancamentoService(repository, autorizacao);
    }

    private DadosCadastroLancamento despesa(UUID conta, UUID categoria, Long imovel) {
        return new DadosCadastroLancamento(
                conta, categoria, TipoCategoria.DESPESA, "Cartorio",
                new BigDecimal("1500.00"), LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 10),
                StatusLancamento.PREVISTO, null, imovel, null);
    }

    @Test
    void naoPermiteLancamentoSemAcessoAFamilia() {
        UUID familia = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        doThrow(new AccessDeniedException("Negado"))
                .when(autorizacao).verificarAcesso(familia, usuario);
        assertThrows(AccessDeniedException.class, () ->
                service.cadastrar(familia, usuario,
                        despesa(UUID.randomUUID(), UUID.randomUUID(), null)));
        verifyNoInteractions(repository);
    }

    @Test
    void naoPermiteContaInexistenteNaFamilia() {
        UUID familia = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        var dados = despesa(UUID.randomUUID(), UUID.randomUUID(), null);
        var ex = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(familia, usuario, dados));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repository, never()).cadastrar(any(), any(), any());
    }

    @Test
    void naoPermiteDespesaDoImovelNaoVinculado() {
        UUID familia = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        var dados = despesa(UUID.randomUUID(), UUID.randomUUID(), 1L);
        when(repository.contaAtiva(familia, dados.contaId())).thenReturn(true);
        when(repository.categoriaAtiva(familia, dados.categoriaId(), TipoCategoria.DESPESA))
                .thenReturn(true);
        var ex = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(familia, usuario, dados));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(repository, never()).cadastrar(any(), any(), any());
    }

    @Test
    void naoPermiteEfetivadoSemDataPagamento() {
        UUID familia = UUID.randomUUID();
        UUID usuario = UUID.randomUUID();
        var base = despesa(UUID.randomUUID(), UUID.randomUUID(), null);
        var dados = new DadosCadastroLancamento(
                base.contaId(), base.categoriaId(), base.tipo(), base.descricao(),
                base.valor(), base.competencia(), base.vencimento(),
                StatusLancamento.EFETIVADO, null, null, null);
        var ex = assertThrows(ResponseStatusException.class,
                () -> service.cadastrar(familia, usuario, dados));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(repository);
    }
}
