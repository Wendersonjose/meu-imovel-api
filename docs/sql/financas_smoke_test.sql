-- Rodar APENAS em banco de testes com Flyway V1..V5 aplicado.
-- Esta transacao nunca persiste as insercoes: ROLLBACK ao final.
BEGIN;

DO $$
DECLARE
    familia_a UUID := gen_random_uuid();
    familia_b UUID := gen_random_uuid();
    usuario_a UUID := gen_random_uuid();
    usuario_b UUID := gen_random_uuid();
    conta_a UUID := gen_random_uuid();
    categoria_a UUID := gen_random_uuid();
    categoria_b UUID := gen_random_uuid();
    bloqueou_familia BOOLEAN := false;
    bloqueou_tipo BOOLEAN := false;
    valor_resumo NUMERIC(14,2);
    valor_dia NUMERIC(14,2);
BEGIN
    INSERT INTO financas.fin_familia(id, nome)
    VALUES (familia_a, 'Teste familia A'), (familia_b, 'Teste familia B');

    INSERT INTO financas.fin_membro(familia_id, usuario_id, nome_exibicao, papel)
    VALUES (familia_a, usuario_a, 'Pessoa A', 'ADMIN'),
           (familia_b, usuario_b, 'Pessoa B', 'ADMIN');

    INSERT INTO financas.fin_categoria(id, familia_id, nome, tipo)
    VALUES (categoria_a, familia_a, 'Mercado', 'DESPESA'),
           (categoria_b, familia_b, 'Mercado', 'DESPESA');

    INSERT INTO financas.fin_conta(id, familia_id, titular_usuario_id, nome, tipo)
    VALUES (conta_a, familia_a, usuario_a, 'Carteira A', 'CARTEIRA');

    INSERT INTO financas.fin_lancamento(
        familia_id, autor_usuario_id, responsavel_usuario_id,
        conta_id, categoria_id, tipo, descricao, valor, competencia,
        efetivado_em, status
    ) VALUES (
        familia_a, usuario_a, usuario_a,
        conta_a, categoria_a, 'DESPESA', 'Compras de teste',
        100.00, DATE '2026-10-23', DATE '2026-10-23', 'EFETIVADO'
    );

    SELECT despesas_efetivadas INTO valor_resumo
    FROM financas.fin_vw_resumo_competencia
    WHERE familia_id = familia_a AND mes_referencia = DATE '2026-10-01';

    SELECT saidas INTO valor_dia
    FROM financas.fin_vw_fluxo_caixa_diario
    WHERE familia_id = familia_a AND dia = DATE '2026-10-23';

    IF valor_resumo IS DISTINCT FROM 100.00 OR valor_dia IS DISTINCT FROM 100.00 THEN
        RAISE EXCEPTION 'Falha: resumo ou fluxo diario errado: % / %', valor_resumo, valor_dia;
    END IF;

    -- Uma transacao da familia A nao pode referenciar categoria da familia B.
    BEGIN
        INSERT INTO financas.fin_lancamento(
            familia_id, autor_usuario_id, responsavel_usuario_id,
            conta_id, categoria_id, tipo, descricao, valor, competencia,
            efetivado_em, status
        ) VALUES (
            familia_a, usuario_a, usuario_a,
            conta_a, categoria_b, 'DESPESA', 'Tentativa indevida',
            10.00, DATE '2026-10-23', DATE '2026-10-23', 'EFETIVADO'
        );
    EXCEPTION WHEN foreign_key_violation THEN
        bloqueou_familia := true;
    END;

    -- Uma categoria DESPESA nao pode ser usada em lancamento RECEITA.
    BEGIN
        INSERT INTO financas.fin_lancamento(
            familia_id, autor_usuario_id, responsavel_usuario_id,
            conta_id, categoria_id, tipo, descricao, valor, competencia,
            efetivado_em, status
        ) VALUES (
            familia_a, usuario_a, usuario_a,
            conta_a, categoria_a, 'RECEITA', 'Tipo inconsistente',
            10.00, DATE '2026-10-23', DATE '2026-10-23', 'EFETIVADO'
        );
    EXCEPTION WHEN foreign_key_violation THEN
        bloqueou_tipo := true;
    END;

    IF NOT bloqueou_familia OR NOT bloqueou_tipo THEN
        RAISE EXCEPTION 'Falha de isolamento: familia=% / tipo=%', bloqueou_familia, bloqueou_tipo;
    END IF;

    RAISE NOTICE 'Testes SQL basicos OK: visoes e chaves estrangeiras.';
END $$;

ROLLBACK;
