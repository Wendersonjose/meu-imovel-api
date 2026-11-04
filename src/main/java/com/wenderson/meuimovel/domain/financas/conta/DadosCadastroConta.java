package com.wenderson.meuimovel.domain.financas.conta;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosCadastroConta(
        @NotBlank @Size(max = 100) String nome,
        @NotNull TipoConta tipo,
        @Digits(integer = 12, fraction = 2) BigDecimal saldoInicial,
        LocalDate dataSaldoInicial
) {}
