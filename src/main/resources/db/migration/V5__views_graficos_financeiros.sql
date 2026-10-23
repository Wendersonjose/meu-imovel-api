-- V5: consultas analiticas para graficos, preservando a distincao
-- entre competencia, vencimento e data efetiva de entrada/saida de dinheiro.
-- IMPORTANTE: views nao implementam autorizacao. Toda consulta de API
-- deve filtrar familia_id validado pela identidade autenticada no servidor.

-- Visao do mes de competencia: receitas/despesas previstas e efetivadas separadas.
CREATE VIEW fin_vw_resumo_competencia AS
SELECT
    familia_id,
    date_trunc('month', competencia)::date AS mes_referencia,
    COALESCE(SUM(valor) FILTER (WHERE tipo = 'RECEITA' AND status = 'EFETIVADO'), 0)::numeric(14,2) AS receitas_efetivadas,
    COALESCE(SUM(valor) FILTER (WHERE tipo = 'DESPESA' AND status = 'EFETIVADO'), 0)::numeric(14,2) AS despesas_efetivadas,
    COALESCE(SUM(valor) FILTER (WHERE tipo = 'RECEITA' AND status = 'PREVISTO'), 0)::numeric(14,2) AS receitas_previstas,
    COALESCE(SUM(valor) FILTER (WHERE tipo = 'DESPESA' AND status = 'PREVISTO'), 0)::numeric(14,2) AS despesas_previstas
FROM fin_lancamento
WHERE status <> 'CANCELADO'
GROUP BY familia_id, date_trunc('month', competencia)::date;

-- Fluxo de caixa realizado por dia; nao mistura transferencia com receita.
CREATE VIEW fin_vw_fluxo_caixa_diario AS
SELECT
    familia_id,
    efetivado_em AS dia,
    COALESCE(SUM(valor) FILTER (WHERE tipo = 'RECEITA'), 0)::numeric(14,2) AS entradas,
    COALESCE(SUM(valor) FILTER (WHERE tipo = 'DESPESA'), 0)::numeric(14,2) AS saidas
FROM fin_lancamento
WHERE status = 'EFETIVADO' AND efetivado_em IS NOT NULL
GROUP BY familia_id, efetivado_em;

-- Despesas por categoria: inclui valores compromissados e efetivados.
CREATE VIEW fin_vw_despesas_categoria_mes AS
SELECT
    l.familia_id,
    date_trunc('month', l.competencia)::date AS mes_referencia,
    l.categoria_id,
    c.nome AS categoria,
    COALESCE(SUM(l.valor) FILTER (WHERE l.status = 'EFETIVADO'), 0)::numeric(14,2) AS despesas_efetivadas,
    COALESCE(SUM(l.valor) FILTER (WHERE l.status = 'PREVISTO'), 0)::numeric(14,2) AS despesas_previstas
FROM fin_lancamento l
JOIN fin_categoria c ON (c.familia_id = l.familia_id AND c.id = l.categoria_id)
WHERE l.tipo = 'DESPESA' AND l.status <> 'CANCELADO'
GROUP BY l.familia_id, date_trunc('month', l.competencia)::date, l.categoria_id, c.nome;

-- Quem fez o gasto (responsavel); autor do registro e campo separado na tabela.
CREATE VIEW fin_vw_gastos_responsavel_mes AS
SELECT
    l.familia_id,
    date_trunc('month', l.competencia)::date AS mes_referencia,
    l.responsavel_usuario_id,
    m.nome_exibicao,
    COALESCE(SUM(l.valor) FILTER (WHERE l.status = 'EFETIVADO'), 0)::numeric(14,2) AS despesas_efetivadas,
    COALESCE(SUM(l.valor) FILTER (WHERE l.status = 'PREVISTO'), 0)::numeric(14,2) AS despesas_previstas
FROM fin_lancamento l
JOIN fin_membro m ON (m.familia_id = l.familia_id AND m.usuario_id = l.responsavel_usuario_id)
WHERE l.tipo = 'DESPESA' AND l.status <> 'CANCELADO'
GROUP BY l.familia_id, date_trunc('month', l.competencia)::date, l.responsavel_usuario_id, m.nome_exibicao;

-- Orcamento mensal, sem confundir a sobra da meta com o saldo bancario.
CREATE VIEW fin_vw_orcamento_categoria_mes AS
SELECT
    o.familia_id,
    o.mes_referencia,
    o.categoria_id,
    c.nome AS categoria,
    o.valor_limite,
    COALESCE(SUM(l.valor) FILTER (WHERE l.status = 'EFETIVADO'), 0)::numeric(14,2) AS gastos_efetivados,
    COALESCE(SUM(l.valor) FILTER (WHERE l.status = 'PREVISTO'), 0)::numeric(14,2) AS gastos_previstos
FROM fin_orcamento o
JOIN fin_categoria c ON (c.familia_id = o.familia_id AND c.id = o.categoria_id)
LEFT JOIN fin_lancamento l
    ON l.familia_id = o.familia_id
    AND l.categoria_id = o.categoria_id
    AND l.tipo = 'DESPESA'
    AND date_trunc('month', l.competencia)::date = o.mes_referencia
GROUP BY o.familia_id, o.mes_referencia, o.categoria_id, c.nome, o.valor_limite;
