package com.wenderson.meuimovel.domain.financas.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosAtualizacaoCategoria(

        @NotBlank(message = "O nome da categorio é Obrigatório")
        @Size(max =100, message = "O nome deve ter no máximo 100 caracteres")
        String nome
) {
}
