package com.wenderson.meuimovel.domain.financas;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DadosResumoCompetencia(

        UUID familiaId,
        LocalDate mesReferencia,
        BigDecimal receitasEfetivadas,
        BigDecimal despesasEfetivadas,
        BigDecimal receitasPrevistas,
        BigDecimal despesasPrevistas
) {
}
