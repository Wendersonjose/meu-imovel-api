
package com.wenderson.meuimovel.domain.documento;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DocumentoRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public DocumentoRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existeImovelVinculado(
            UUID familiaId,
            Long imovelId) {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM financas.fin_imovel_familia
                    WHERE familia_id = :familiaId
                      AND imovel_id = :imovelId
                )
                """;

        return Boolean.TRUE.equals(
                jdbc.queryForObject(
                        sql,
                        Map.of(
                                "familiaId", familiaId,
                                "imovelId", imovelId
                        ),
                        Boolean.class
                )
        );
    }

    public boolean existeLancamentoVinculado(
            UUID familiaId,
            UUID lancamentoId) {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM financas.fin_lancamento
                    WHERE familia_id = :familiaId
                      AND id = :lancamentoId
                )
                """;

        return Boolean.TRUE.equals(
                jdbc.queryForObject(
                        sql,
                        Map.of(
                                "familiaId", familiaId,
                                "lancamentoId", lancamentoId
                        ),
                        Boolean.class
                )
        );
    }

    public DadosDetalhamentoDocumento cadastrar(
            UUID id,
            UUID familiaId,
            UUID autorUsuarioId,
            Long imovelId,
            UUID lancamentoId,
            String tipoDocumento,
            String descricao,
            String nomeOriginal,
            String mimeType,
            long tamanhoBytes,
            String bucket,
            String caminho) {

        String sql = """
                INSERT INTO financas.fin_documento (
                    id,
                    familia_id,
                    autor_usuario_id,
                    imovel_id,
                    lancamento_id,
                    tipo_documento,
                    descricao,
                    nome_original,
                    storage_bucket,
                    storage_caminho,
                    mime_type,
                    tamanho_bytes
                )
                VALUES (
                    :id,
                    :familiaId,
                    :autorUsuarioId,
                    :imovelId,
                    :lancamentoId,
                    :tipoDocumento,
                    :descricao,
                    :nomeOriginal,
                    :bucket,
                    :caminho,
                    :mimeType,
                    :tamanhoBytes
                )
                RETURNING
                    id,
                    familia_id,
                    autor_usuario_id,
                    imovel_id,
                    lancamento_id,
                    tipo_documento,
                    descricao,
                    nome_original,
                    mime_type,
                    tamanho_bytes,
                    criado_em
                """;

        var parametros = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("familiaId", familiaId)
                .addValue("autorUsuarioId", autorUsuarioId)
                .addValue("imovelId", imovelId, Types.BIGINT)
                .addValue("lancamentoId", lancamentoId, Types.OTHER)
                .addValue("tipoDocumento", tipoDocumento)
                .addValue("descricao", descricao)
                .addValue("nomeOriginal", nomeOriginal)
                .addValue("bucket", bucket)
                .addValue("caminho", caminho)
                .addValue("mimeType", mimeType)
                .addValue("tamanhoBytes", tamanhoBytes);

        return jdbc.queryForObject(
                sql,
                parametros,
                this::mapear
        );
    }

    public Optional<DadosDetalhamentoDocumento> buscar(
            UUID familiaId,
            UUID documentoId) {

        String sql = """
                SELECT
                    id,
                    familia_id,
                    autor_usuario_id,
                    imovel_id,
                    lancamento_id,
                    tipo_documento,
                    descricao,
                    nome_original,
                    mime_type,
                    tamanho_bytes,
                    criado_em
                FROM financas.fin_documento
                WHERE familia_id = :familiaId
                  AND id = :documentoId
                """;

        List<DadosDetalhamentoDocumento> documentos =
                jdbc.query(
                        sql,
                        Map.of(
                                "familiaId", familiaId,
                                "documentoId", documentoId
                        ),
                        this::mapear
                );

        return documentos.stream().findFirst();
    }

    private DadosDetalhamentoDocumento mapear(
            ResultSet rs,
            int linha) throws SQLException {

        return new DadosDetalhamentoDocumento(
                rs.getObject("id", UUID.class),
                rs.getObject("familia_id", UUID.class),
                rs.getObject("autor_usuario_id", UUID.class),
                rs.getObject("imovel_id", Long.class),
                rs.getObject("lancamento_id", UUID.class),
                rs.getString("tipo_documento"),
                rs.getString("descricao"),
                rs.getString("nome_original"),
                rs.getString("mime_type"),
                rs.getLong("tamanho_bytes"),
                rs.getTimestamp("criado_em").toInstant()
        );
    }
}
