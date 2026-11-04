
package com.wenderson.meuimovel.domain.documento;

import java.time.Instant;
import java.util.UUID;

public record DadosDetalhamentoDocumento(
        UUID id,
        UUID familiaId,
        UUID autorUsuarioId,
        Long imovelId,
        UUID lancamentoId,
        String tipoDocumento,
        String descricao,
        String nomeOriginal,
        String mimeType,
        Long tamanhoBytes,
        Instant criadoEm
) {
}
