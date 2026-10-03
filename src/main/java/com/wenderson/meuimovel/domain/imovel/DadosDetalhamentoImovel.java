package com.wenderson.meuimovel.domain.imovel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record DadosDetalhamentoImovel(
        Long id,
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
        String observacao,

        Instant createdAt,
        Instant updatedAt
) {

    public DadosDetalhamentoImovel(Imovel imovel) {
        this(
                imovel.getId(),
                imovel.getDescricao(),
                imovel.getTipoImovel(),

                imovel.getMatricula(),
                imovel.getCnm(),
                imovel.getCartorio(),
                imovel.getCadastroMunicipal(),

                imovel.getLogradouro(),
                imovel.getNumero(),
                imovel.getCidade(),
                imovel.getUf(),

                imovel.getLote(),
                imovel.getQuadra(),
                imovel.getLoteamento(),

                imovel.getAreaTerreno(),
                imovel.getAreaConstruida(),

                imovel.getNumeroHabiteSe(),
                imovel.getDataHabiteSe(),

                imovel.getValorImovel(),
                imovel.getValorFinanciado(),
                imovel.getValorEntrada(),

                imovel.getDataCompra(),
                imovel.getObservacao(),

                imovel.getCreatedAt(),
                imovel.getUpdatedAt()
        );
    }
}