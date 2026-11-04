package com.wenderson.meuimovel.domain.financas.lancamento;

import com.wenderson.meuimovel.enums.TipoCategoria;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DadosDetalhamentoLancamento(
        UUID id,
        UUID familiaId,
        UUID autorUsuarioId,
        UUID responsavelUsuarioId,
        UUID contaId,
        UUID categoriaId,
        Long imovelId,
        TipoCategoria tipo,
        String descricao,
        BigDecimal valor,
        LocalDate competencia,
        LocalDate vencimento,
        LocalDate efetivadoEm,
        StatusLancamento status,
        String observacao,
        Instant criadoEm,
        Instant atualizadoEm
) {}
