package com.wenderson.meuimovel.domain.financas.lancamento;

import com.wenderson.meuimovel.enums.TipoCategoria;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record DadosCadastroLancamento(
        @NotNull UUID contaId,
        @NotNull UUID categoriaId,
        @NotNull TipoCategoria tipo,
        @NotBlank @Size(max = 240) String descricao,
        @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal valor,
        @NotNull LocalDate competencia,
        @NotNull LocalDate vencimento,
        @NotNull StatusLancamento status,
        LocalDate efetivadoEm,
        @Positive Long imovelId,
        String observacao
) {}
