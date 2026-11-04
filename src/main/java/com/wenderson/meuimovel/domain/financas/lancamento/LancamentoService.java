package com.wenderson.meuimovel.domain.financas.lancamento;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class LancamentoService {
    private final LancamentoRepository repository;
    private final AutorizacaoFamiliaService autorizacao;

    public LancamentoService(LancamentoRepository repository, AutorizacaoFamiliaService autorizacao) {
        this.repository = repository;
        this.autorizacao = autorizacao;
    }

    @Transactional
    public DadosDetalhamentoLancamento cadastrar(
            UUID familiaId, UUID usuarioId, DadosCadastroLancamento dados) {
        autorizacao.verificarAcesso(familiaId, usuarioId);
        validar(familiaId, dados);
        return repository.cadastrar(familiaId, usuarioId, dados);
    }

    @Transactional(readOnly = true)
    public List<DadosDetalhamentoLancamento> listar(
            UUID familiaId, UUID usuarioId, YearMonth mes, Long imovelId) {
        autorizacao.verificarAcesso(familiaId, usuarioId);
        return repository.listar(familiaId, mes, imovelId);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoLancamento buscar(UUID familiaId, UUID usuarioId, UUID id) {
        autorizacao.verificarAcesso(familiaId, usuarioId);
        return localizar(familiaId, id);
    }

    @Transactional
    public DadosDetalhamentoLancamento atualizar(
            UUID familiaId, UUID usuarioId, UUID id, DadosCadastroLancamento dados) {
        autorizacao.verificarAcesso(familiaId, usuarioId);
        localizar(familiaId, id);
        validar(familiaId, dados);
        return repository.atualizar(familiaId, id, dados);
    }

    @Transactional
    public DadosDetalhamentoLancamento registrarPagamento(
            UUID familiaId, UUID usuarioId, UUID id, DadosPagamentoLancamento dados) {
        autorizacao.verificarAcesso(familiaId, usuarioId);
        var atual = localizar(familiaId, id);
        if (atual.status() == StatusLancamento.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Lancamento cancelado nao pode ser pago");
        }
        return repository.efetivar(familiaId, id, dados.dataPagamento());
    }

    private DadosDetalhamentoLancamento localizar(UUID familiaId, UUID id) {
        return repository.buscar(familiaId, id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Lancamento nao encontrado"));
    }

    private void validar(UUID familiaId, DadosCadastroLancamento dados) {
        if (dados.status() == StatusLancamento.EFETIVADO && dados.efetivadoEm() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Data do pagamento obrigatoria para lancamento efetivado");
        }
        if (dados.status() != StatusLancamento.EFETIVADO && dados.efetivadoEm() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lancamento previsto ou cancelado nao pode ter data de pagamento");
        }
        if (!repository.contaAtiva(familiaId, dados.contaId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Conta inexistente ou inativa nesta familia");
        }
        if (!repository.categoriaAtiva(familiaId, dados.categoriaId(), dados.tipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Categoria inexistente, inativa ou de tipo diferente");
        }
        if (dados.imovelId() != null && !repository.imovelVinculado(familiaId, dados.imovelId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Imovel nao vinculado a esta familia");
        }
    }
}
