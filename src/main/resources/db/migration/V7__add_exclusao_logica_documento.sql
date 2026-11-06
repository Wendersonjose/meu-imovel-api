
ALTER TABLE financas.fin_documento
    ADD COLUMN excluido_em TIMESTAMPTZ,
    ADD COLUMN excluido_por_usuario_id UUID;

ALTER TABLE financas.fin_documento
    ADD CONSTRAINT fk_documento_usuario_exclusao
        FOREIGN KEY (familia_id, excluido_por_usuario_id)
            REFERENCES financas.fin_membro(familia_id, usuario_id);

ALTER TABLE financas.fin_documento
    ADD CONSTRAINT ck_documento_exclusao
        CHECK (
            (excluido_em IS NULL AND excluido_por_usuario_id IS NULL)
                OR
            (excluido_em IS NOT NULL AND excluido_por_usuario_id IS NOT NULL)
            );

CREATE INDEX idx_documento_ativos_familia
    ON financas.fin_documento(familia_id)
    WHERE excluido_em IS NULL;
