-- V4: estrutura do modulo financeiro familiar.
-- Sem dados ficticios e sem alterar a tabela imovel existente.
-- Uma familia pode ter dois ou mais membros, com dados compartilhados e autoria rastreavel.
-- ATENCAO: ainda nao existe autenticacao na API; nao publicar endpoints financeiros ate protege-los.

CREATE TABLE fin_familia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(120) NOT NULL CHECK (length(btrim(nome)) > 0),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- usuario_id sera o subject (sub) UUID do provedor de autenticacao escolhido;
-- sem FK para auth.users para manter a migracao portavel entre PostgreSQL local e Supabase.
CREATE TABLE fin_membro (
    familia_id UUID NOT NULL REFERENCES fin_familia(id),
    usuario_id UUID NOT NULL,
    nome_exibicao VARCHAR(120) NOT NULL CHECK (length(btrim(nome_exibicao)) > 0),
    papel VARCHAR(12) NOT NULL CHECK (papel IN ('ADMIN', 'MEMBRO')),
    ativo BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (familia_id, usuario_id)
);

CREATE INDEX idx_fin_membro_usuario ON fin_membro(usuario_id) WHERE ativo = true;

CREATE TABLE fin_categoria (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    familia_id UUID NOT NULL REFERENCES fin_familia(id),
    nome VARCHAR(100) NOT NULL CHECK (length(btrim(nome)) > 0),
    tipo VARCHAR(8) NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
    ativa BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (familia_id, id)
);
CREATE UNIQUE INDEX uk_fin_categoria_nome ON fin_categoria(familia_id, tipo, lower(btrim(nome)));

CREATE TABLE fin_conta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    familia_id UUID NOT NULL REFERENCES fin_familia(id),
    titular_usuario_id UUID NOT NULL,
    nome VARCHAR(100) NOT NULL CHECK (length(btrim(nome)) > 0),
    tipo VARCHAR(18) NOT NULL CHECK (tipo IN ('CORRENTE', 'POUPANCA', 'CARTEIRA', 'INVESTIMENTO', 'OUTRA')),
    saldo_inicial NUMERIC(14,2) NOT NULL DEFAULT 0,
    data_saldo_inicial DATE NOT NULL DEFAULT CURRENT_DATE,
    ativa BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (familia_id, id),
    FOREIGN KEY (familia_id, titular_usuario_id)
        REFERENCES fin_membro(familia_id, usuario_id)
);

-- Nao vincular imoveis antigos automaticamente: atribuir familia apos verificar propriedade.
-- UNIQUE(imovel_id) impede um mesmo imovel de pertencer a familias diferentes.
CREATE TABLE fin_imovel_familia (
    familia_id UUID NOT NULL REFERENCES fin_familia(id),
    imovel_id BIGINT NOT NULL UNIQUE REFERENCES imovel(id),
    vinculado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (familia_id, imovel_id)
);

CREATE TABLE fin_lancamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    familia_id UUID NOT NULL REFERENCES fin_familia(id),
    autor_usuario_id UUID NOT NULL,
    responsavel_usuario_id UUID NOT NULL,
    conta_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    imovel_id BIGINT,
    tipo VARCHAR(8) NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
    descricao VARCHAR(240) NOT NULL CHECK (length(btrim(descricao)) > 0),
    valor NUMERIC(14,2) NOT NULL CHECK (valor > 0),
    competencia DATE NOT NULL,
    vencimento DATE,
    efetivado_em DATE,
    status VARCHAR(12) NOT NULL DEFAULT 'PREVISTO'
        CHECK (status IN ('PREVISTO', 'EFETIVADO', 'CANCELADO')),
    observacao TEXT,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (familia_id, id),
    FOREIGN KEY (familia_id, autor_usuario_id)
        REFERENCES fin_membro(familia_id, usuario_id),
    FOREIGN KEY (familia_id, responsavel_usuario_id)
        REFERENCES fin_membro(familia_id, usuario_id),
    FOREIGN KEY (familia_id, conta_id)
        REFERENCES fin_conta(familia_id, id),
    FOREIGN KEY (familia_id, categoria_id)
        REFERENCES fin_categoria(familia_id, id),
    FOREIGN KEY (familia_id, imovel_id)
        REFERENCES fin_imovel_familia(familia_id, imovel_id),
    CONSTRAINT ck_fin_lancamento_efetivacao CHECK (
        (status = 'EFETIVADO' AND efetivado_em IS NOT NULL)
        OR (status <> 'EFETIVADO' AND efetivado_em IS NULL)
    )
);
CREATE INDEX idx_fin_lancamento_familia_competencia
    ON fin_lancamento(familia_id, competencia);
CREATE INDEX idx_fin_lancamento_familia_vencimento
    ON fin_lancamento(familia_id, vencimento) WHERE status = 'PREVISTO';
CREATE INDEX idx_fin_lancamento_familia_efetivado
    ON fin_lancamento(familia_id, efetivado_em) WHERE status = 'EFETIVADO';
CREATE INDEX idx_fin_lancamento_responsavel
    ON fin_lancamento(familia_id, responsavel_usuario_id, competencia);

CREATE TABLE fin_orcamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    familia_id UUID NOT NULL REFERENCES fin_familia(id),
    categoria_id UUID NOT NULL,
    mes_referencia DATE NOT NULL CHECK (EXTRACT(DAY FROM mes_referencia) = 1),
    valor_limite NUMERIC(14,2) NOT NULL CHECK (valor_limite >= 0),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (familia_id, categoria_id, mes_referencia),
    FOREIGN KEY (familia_id, categoria_id)
        REFERENCES fin_categoria(familia_id, id)
);

-- Transferencias nao sao receitas nem despesas; sao movimentacoes entre contas.
CREATE TABLE fin_transferencia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    familia_id UUID NOT NULL REFERENCES fin_familia(id),
    autor_usuario_id UUID NOT NULL,
    conta_origem_id UUID NOT NULL,
    conta_destino_id UUID NOT NULL,
    valor NUMERIC(14,2) NOT NULL CHECK (valor > 0),
    data_transferencia DATE NOT NULL,
    descricao VARCHAR(240),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (conta_origem_id <> conta_destino_id),
    FOREIGN KEY (familia_id, autor_usuario_id)
        REFERENCES fin_membro(familia_id, usuario_id),
    FOREIGN KEY (familia_id, conta_origem_id)
        REFERENCES fin_conta(familia_id, id),
    FOREIGN KEY (familia_id, conta_destino_id)
        REFERENCES fin_conta(familia_id, id)
);
CREATE INDEX idx_fin_transferencia_data
    ON fin_transferencia(familia_id, data_transferencia);
