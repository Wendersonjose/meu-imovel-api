
package com.wenderson.meuimovel.domain.documento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DadosAtualizacaoDocumento(

        @NotBlank(message = "Tipo do documento é obrigatório")
        @Pattern(
                regexp = "[A-Z][A-Z0-9_]{0,39}",
                message = "Tipo do documento inválido"
        )
        String tipoDocumento,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(
                max = 255,
                message = "Descrição deve ter no máximo 255 caracteres"
        )
        String descricao

) {
}
