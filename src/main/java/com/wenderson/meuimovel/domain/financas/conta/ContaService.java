package com.wenderson.meuimovel.domain.financas.conta;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ContaService {
    private final ContaRepository repository;
    private final AutorizacaoFamiliaService autorizacao;

    public ContaService(ContaRepository repository, AutorizacaoFamiliaService autorizacao) {
        this.repository = repository;
        this.autorizacao = autorizacao;
    }

    @Transactional
    public DadosDetalhamentoConta cadastrar(UUID familiaId, UUID usuarioId, DadosCadastroConta dados) {
        autorizacao.verificarAcesso(familiaId, usuarioId);
        BigDecimal saldo = dados.saldoInicial() == null ? BigDecimal.ZERO : dados.saldoInicial();
        LocalDate data = dados.dataSaldoInicial() == null ? LocalDate.now() : dados.dataSaldoInicial();
        return repository.cadastrar(familiaId, usuarioId, dados.nome().trim(), dados.tipo(), saldo, data);
    }

    @Transactional(readOnly = true)
    public List<DadosDetalhamentoConta> listar(UUID familiaId, UUID usuarioId) {
        autorizacao.verificarAcesso(familiaId, usuarioId);
        return repository.listar(familiaId);
    }
}
