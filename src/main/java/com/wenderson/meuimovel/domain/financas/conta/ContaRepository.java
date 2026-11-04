package com.wenderson.meuimovel.domain.financas.conta;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ContaRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public ContaRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public DadosDetalhamentoConta cadastrar(
            UUID familiaId, UUID usuarioId, String nome, TipoConta tipo,
            BigDecimal saldoInicial, LocalDate dataSaldoInicial) {
        String sql = """
                INSERT INTO financas.fin_conta
                    (familia_id, titular_usuario_id, nome, tipo, saldo_inicial, data_saldo_inicial)
                VALUES (:familiaId, :usuarioId, :nome, :tipo, :saldo, :data)
                RETURNING id, nome, tipo, saldo_inicial, data_saldo_inicial, ativa
                """;
        var params = new MapSqlParameterSource()
                .addValue("familiaId", familiaId)
                .addValue("usuarioId", usuarioId)
                .addValue("nome", nome)
                .addValue("tipo", tipo.name())
                .addValue("saldo", saldoInicial)
                .addValue("data", dataSaldoInicial);
        return jdbc.queryForObject(sql, params, this::mapear);
    }

    public List<DadosDetalhamentoConta> listar(UUID familiaId) {
        return jdbc.query("""
                SELECT id, nome, tipo, saldo_inicial, data_saldo_inicial, ativa
                FROM financas.fin_conta
                WHERE familia_id = :familiaId AND ativa = true
                ORDER BY nome
                """, Map.of("familiaId", familiaId), this::mapear);
    }

    private DadosDetalhamentoConta mapear(ResultSet rs, int linha) throws SQLException {
        return new DadosDetalhamentoConta(
                rs.getObject("id", UUID.class),
                rs.getString("nome"),
                TipoConta.valueOf(rs.getString("tipo")),
                rs.getBigDecimal("saldo_inicial"),
                rs.getObject("data_saldo_inicial", LocalDate.class),
                rs.getBoolean("ativa"));
    }
}
