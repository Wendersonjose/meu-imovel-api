package com.wenderson.meuimovel.domain.financas.categoria;

import com.wenderson.meuimovel.enums.TipoCategoria;

import java.util.UUID;

public record DadosDetalhamentoCategoria(

        UUID id,
        String nome,
        TipoCategoria tipo,
        boolean ativa
) {
}
