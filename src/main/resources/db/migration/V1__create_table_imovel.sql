CREATE TABLE imovel (
                        id BIGSERIAL PRIMARY KEY,
                        descricao VARCHAR(150) NOT NULL,
                        valor_imovel NUMERIC(12,2) NOT NULL,
                        valor_financiado NUMERIC(12,2),
                        valor_entrada NUMERIC(12,2),
                        data_compra DATE,
                        observacao TEXT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);