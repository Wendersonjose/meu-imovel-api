package com.wenderson.meuimovel.domain.financas.lancamento;

import com.wenderson.meuimovel.enums.TipoCategoria;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LancamentoRepository {

    private static final String CAMPOS = """
            id, familia_id, autor_usuario_id, responsavel_usuario_id,
            conta_id, categoria_id, imovel_id, tipo, descricao, valor,
            competencia, vencimento, efetivado_em, status, observacao,
            criado_em, atualizado_em
            """;

    private final NamedParameterJdbcTemplate jdbc;

    public LancamentoRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean contaAtiva(UUID familiaId, UUID contaId) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM financas.fin_conta
                    WHERE familia_id = :familiaId AND id = :contaId AND ativa = true
                )
                """, Map.of("familiaId", familiaId, "contaId", contaId), Boolean.class));
    }

    public boolean categoriaAtiva(UUID familiaId, UUID categoriaId, TipoCategoria tipo) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM financas.fin_categoria
                    WHERE familia_id = :familiaId AND id = :categoriaId
                      AND tipo = :tipo AND ativa = true
                )
                """, Map.of("familiaId", familiaId, "categoriaId", categoriaId,
                "tipo", tipo.name()), Boolean.class));
    }

    public boolean imovelVinculado(UUID familiaId, Long imovelId) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM financas.fin_imovel_familia
                    WHERE familia_id = :familiaId AND imovel_id = :imovelId
                )
                """, Map.of("familiaId", familiaId, "imovelId", imovelId), Boolean.class));
    }

    public DadosDetalhamentoLancamento cadastrar(
            UUID familiaId, UUID usuarioId, DadosCadastroLancamento dados) {
        String sql = """
                INSERT INTO financas.fin_lancamento (
                    familia_id, autor_usuario_id, responsavel_usuario_id,
                    conta_id, categoria_id, imovel_id, tipo, descricao, valor,
                    competencia, vencimento, efetivado_em, status, observacao
                ) VALUES (
                    :familiaId, :usuarioId, :usuarioId,
                    :contaId, :categoriaId, :imovelId, :tipo, :descricao, :valor,
                    :competencia, :vencimento, :efetivadoEm, :status, :observacao
                )
                RETURNING
                """ + CAMPOS;
        return jdbc.queryForObject(sql, parametros(familiaId, usuarioId, dados), this::mapear);
    }

    public List<DadosDetalhamentoLancamento> listar(
            UUID familiaId, YearMonth mes, Long imovelId) {
        StringBuilder sql = new StringBuilder("SELECT " + CAMPOS
                + " FROM financas.fin_lancamento WHERE familia_id = :familiaId");
        var params = new MapSqlParameterSource().addValue("familiaId", familiaId);
        if (mes != null) {
            sql.append(" AND competencia >= :inicio AND competencia < :fim");
            params.addValue("inicio", mes.atDay(1));
            params.addValue("fim", mes.plusMonths(1).atDay(1));
        }
        if (imovelId != null) {
            sql.append(" AND imovel_id = :imovelId");
            params.addValue("imovelId", imovelId);
        }
        sql.append("""
                 ORDER BY CASE WHEN status = 'PREVISTO' THEN 0 ELSE 1 END,
                          vencimento ASC NULLS LAST, criado_em DESC LIMIT 500
                """);
        return jdbc.query(sql.toString(), params, this::mapear);
    }

    public Optional<DadosDetalhamentoLancamento> buscar(UUID familiaId, UUID id) {
        List<DadosDetalhamentoLancamento> resultados = jdbc.query(
                "SELECT " + CAMPOS + """
                 FROM financas.fin_lancamento
                 WHERE familia_id = :familiaId AND id = :id
                 """, Map.of("familiaId", familiaId, "id", id), this::mapear);
        return resultados.stream().findFirst();
    }

    public DadosDetalhamentoLancamento atualizar(
            UUID familiaId, UUID id, DadosCadastroLancamento dados) {
        String sql = """
                UPDATE financas.fin_lancamento
                SET conta_id = :contaId, categoria_id = :categoriaId,
                    imovel_id = :imovelId, tipo = :tipo, descricao = :descricao,
                    valor = :valor, competencia = :competencia,
                    vencimento = :vencimento, efetivado_em = :efetivadoEm,
                    status = :status, observacao = :observacao,
                    atualizado_em = now()
                WHERE familia_id = :familiaId AND id = :id
                RETURNING
                """ + CAMPOS;
        var params = parametros(familiaId, null, dados).addValue("id", id);
        return jdbc.queryForObject(sql, params, this::mapear);
    }

    public DadosDetalhamentoLancamento efetivar(
            UUID familiaId, UUID id, LocalDate dataPagamento) {
        String sql = """
                UPDATE financas.fin_lancamento
                SET status = 'EFETIVADO', efetivado_em = :dataPagamento,
                    atualizado_em = now()
                WHERE familia_id = :familiaId AND id = :id
                RETURNING
                """ + CAMPOS;
        var params = new MapSqlParameterSource()
                .addValue("familiaId", familiaId)
                .addValue("id", id)
                .addValue("dataPagamento", dataPagamento);
        return jdbc.queryForObject(sql, params, this::mapear);
    }

    private MapSqlParameterSource parametros(
            UUID familiaId, UUID usuarioId, DadosCadastroLancamento dados) {
        return new MapSqlParameterSource()
                .addValue("familiaId", familiaId)
                .addValue("usuarioId", usuarioId)
                .addValue("contaId", dados.contaId())
                .addValue("categoriaId", dados.categoriaId())
                .addValue("imovelId", dados.imovelId(), java.sql.Types.BIGINT)
                .addValue("tipo", dados.tipo().name())
                .addValue("descricao", dados.descricao().trim())
                .addValue("valor", dados.valor())
                .addValue("competencia", dados.competencia())
                .addValue("vencimento", dados.vencimento())
                .addValue("efetivadoEm", dados.efetivadoEm(), java.sql.Types.DATE)
                .addValue("status", dados.status().name())
                .addValue("observacao", dados.observacao());
    }

    private DadosDetalhamentoLancamento mapear(ResultSet rs, int linha)
            throws SQLException {
        return new DadosDetalhamentoLancamento(
                rs.getObject("id", UUID.class),
                rs.getObject("familia_id", UUID.class),
                rs.getObject("autor_usuario_id", UUID.class),
                rs.getObject("responsavel_usuario_id", UUID.class),
                rs.getObject("conta_id", UUID.class),
                rs.getObject("categoria_id", UUID.class),
                rs.getObject("imovel_id", Long.class),
                TipoCategoria.valueOf(rs.getString("tipo")),
                rs.getString("descricao"),
                rs.getBigDecimal("valor"),
                rs.getObject("competencia", LocalDate.class),
                rs.getObject("vencimento", LocalDate.class),
                rs.getObject("efetivado_em", LocalDate.class),
                StatusLancamento.valueOf(rs.getString("status")),
                rs.getString("observacao"),
                rs.getTimestamp("criado_em").toInstant(),
                rs.getTimestamp("atualizado_em").toInstant());
    }
}
