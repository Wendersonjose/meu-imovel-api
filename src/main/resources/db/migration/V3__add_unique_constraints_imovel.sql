ALTER TABLE imovel
    ADD CONSTRAINT uk_imovel_cnm
        UNIQUE (cnm),

    ADD CONSTRAINT uk_imovel_cartorio_matricula
        UNIQUE (cartorio, matricula),

    ADD CONSTRAINT uk_imovel_cadastro_municipal
        UNIQUE (cidade, uf, cadastro_municipal);