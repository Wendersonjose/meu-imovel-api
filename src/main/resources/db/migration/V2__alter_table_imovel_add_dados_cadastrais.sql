ALTER TABLE imovel
    ADD COLUMN tipo_imovel VARCHAR(100),

    ADD COLUMN matricula VARCHAR(50),
    ADD COLUMN cnm VARCHAR(50),
    ADD COLUMN cartorio VARCHAR(200),
    ADD COLUMN cadastro_municipal VARCHAR(80),

    ADD COLUMN logradouro VARCHAR(200),
    ADD COLUMN numero VARCHAR(30),
    ADD COLUMN cidade VARCHAR(100),
    ADD COLUMN uf CHAR(2),

    ADD COLUMN lote VARCHAR(30),
    ADD COLUMN quadra VARCHAR(30),
    ADD COLUMN loteamento VARCHAR(150),

    ADD COLUMN area_terreno NUMERIC(10,2),
    ADD COLUMN area_construida NUMERIC(10,2),

    ADD COLUMN numero_habite_se VARCHAR(50),
    ADD COLUMN data_habite_se DATE;

ALTER TABLE imovel
ALTER COLUMN created_at TYPE TIMESTAMPTZ
        USING created_at AT TIME ZONE 'UTC',

    ALTER COLUMN updated_at TYPE TIMESTAMPTZ
        USING updated_at AT TIME ZONE 'UTC';