package com.wenderson.meuimovel.domain.imovel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosAtualizacaoImovel(
        String descricao,
        String tipoImovel,

        String matricula,
        String cnm,
        String cartorio,
        String cadastroMunicipal,

        String logradouro,
        String numero,
        String cidade,
        String uf,

        String lote,
        String quadra,
        String loteamento,

        BigDecimal areaTerreno,
        BigDecimal areaConstruida,

        String numeroHabiteSe,
        LocalDate dataHabiteSe,

        BigDecimal valorImovel,
        BigDecimal valorFinanciado,
        BigDecimal valorEntrada,

        LocalDate dataCompra,
        String observacao
) {
}