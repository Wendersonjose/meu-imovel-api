package com.wenderson.meuimovel.domain.financas.conta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DadosDetalhamentoConta(
        UUID id,
        String nome,
        TipoConta tipo,
        BigDecimal saldoInicial,
        LocalDate dataSaldoInicial,
        boolean ativa
) {}
