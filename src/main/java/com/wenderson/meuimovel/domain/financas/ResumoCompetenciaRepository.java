package com.wenderson.meuimovel.domain.financas;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ResumoCompetenciaRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ResumoCompetenciaRepository(
            NamedParameterJdbcTemplate jdbcTemplate
    ) {

        this.jdbcTemplate = jdbcTemplate;

    }

    public List< DadosResumoCompetencia> listarPorFamilia(
            UUID familiaId
    ){

        String sql = """
        SELECT
            familia_id,
            mes_referencia,
            receitas_efetivadas,
            despesas_efetivadas,
            receitas_previstas,
            despesas_previstas
        FROM financas.fin_vw_resumo_competencia
        WHERE familia_id = :familiaId
        ORDER BY mes_referencia DESC
        """;


        return jdbcTemplate.query(
                sql,
                Map.of("familiaId", familiaId),
            (rs, numeroLinha) -> new DadosResumoCompetencia(
                    rs.getObject("familia_id", UUID.class),
                    rs.getObject("mes_referencia", LocalDate.class),
                    rs.getBigDecimal("receitas_efetivadas"),
                    rs.getBigDecimal("despesas_efetivadas"),
                    rs.getBigDecimal("receitas_previstas"),
                    rs.getBigDecimal("despesas_previstas")
            )
        );
    }
}
