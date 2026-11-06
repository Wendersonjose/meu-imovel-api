package com.wenderson.meuimovel.domain.financas.lancamento;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record DadosPagamentoLancamento(
        @NotNull LocalDate dataPagamento
) {}
