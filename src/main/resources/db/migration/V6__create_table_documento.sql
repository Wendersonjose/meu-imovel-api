
CREATE TABLE financas.fin_documento (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                        familia_id UUID NOT NULL,
                                        autor_usuario_id UUID NOT NULL,

                                        imovel_id BIGINT,
                                        lancamento_id UUID,

                                        tipo_documento VARCHAR(40) NOT NULL,
                                        descricao VARCHAR(255) NOT NULL,
                                        nome_original VARCHAR(255) NOT NULL,

                                        storage_bucket VARCHAR(100) NOT NULL,
                                        storage_caminho TEXT NOT NULL,

                                        mime_type VARCHAR(100) NOT NULL
                                            CHECK (mime_type IN (
                                                                 'application/pdf',
                                                                 'image/jpeg',
                                                                 'image/png'
                                                )),

                                        tamanho_bytes BIGINT NOT NULL
                                            CHECK (tamanho_bytes > 0),

                                        criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),

                                        CONSTRAINT ck_documento_vinculo
                                            CHECK (num_nonnulls(imovel_id, lancamento_id) = 1),

                                        CONSTRAINT fk_documento_autor
                                            FOREIGN KEY (familia_id, autor_usuario_id)
                                                REFERENCES financas.fin_membro(familia_id, usuario_id),

                                        CONSTRAINT fk_documento_imovel
                                            FOREIGN KEY (familia_id, imovel_id)
                                                REFERENCES financas.fin_imovel_familia(familia_id, imovel_id),

                                        CONSTRAINT fk_documento_lancamento
                                            FOREIGN KEY (familia_id, lancamento_id)
                                                REFERENCES financas.fin_lancamento(familia_id, id),

                                        CONSTRAINT uk_documento_storage
                                            UNIQUE (storage_bucket, storage_caminho)
);

CREATE INDEX idx_documento_imovel
    ON financas.fin_documento(familia_id, imovel_id)
    WHERE imovel_id IS NOT NULL;

CREATE INDEX idx_documento_lancamento
    ON financas.fin_documento(familia_id, lancamento_id)
    WHERE lancamento_id IS NOT NULL;
