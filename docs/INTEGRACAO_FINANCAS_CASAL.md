# Integração do NossaGrana ao Meu Imóvel — fase 1 (estrutura)

## Objetivo e decisão

O projeto **Meu Imóvel** continua sendo a aplicação principal. A funcionalidade de finanças
familiares será um **módulo interno**, não um segundo backend paralelo.
O NossaGrana (MIT) serve de referência para a experiência do casal; os gráficos
da versão adaptada, feitos com Recharts e inspirados nas capturas do FinanCerto,
serão reutilizados **somente depois que existir o frontend React/TypeScript do Meu Imóvel**.

Arquitetura-alvo:

```text
Meu Imóvel (React + TypeScript; ainda não existe no repositório)
 ├─ Imóveis
 ├─ Finanças da família
 │   ├─ Painel e gráficos
 │   ├─ Lançamentos e calendário
 │   ├─ Contas e transferências
 │   ├─ Orçamentos por categoria
 │   └─ Relatórios de despesas do imóvel
 └─ Acesso / configurações
           │ HTTPS + JWT validado
           ▼
Meu Imóvel API (Java 17 / Spring Boot)
 ├─ domain/imovel (existente)
 └─ domain/financas (a implementar)
           │
           ▼
PostgreSQL (Flyway, V1–V3 existentes, V4–V5 propostas)
 ├─ public.imovel (inalterado)
 └─ schema privado financas (nao expor na Data API)
     ├─ fin_familia / fin_membro
     ├─ fin_categoria / fin_conta / fin_lancamento
     ├─ fin_orcamento / fin_transferencia
     ├─ fin_imovel_familia
     └─ fin_vw_* (leituras dos graficos)
```

## O que já foi implementado nesta branch

- **V4**: schema privado `financas` e tabelas financeiras para famílias, usuários, contas,
  categorias, despesas e receitas, orçamentos, transferências e vínculo opcional
  a imóveis. Sem alterar ou excluir registros antigos.
- **V5**: views privadas para consultas analíticas de competência, fluxo de caixa diário real,
  despesas por categoria, gastos por responsável e consumo de orçamento.
- Associações com chave de família composta impedem referenciar contas,
  categorias, membros ou imóveis de outra família por engano.
- Não foram criados logins, endpoints públicos, dados fictícios ou deploys.

Estas migrations serão aplicadas **somente quando a branch for mesclada e a
aplicação rodar Flyway**. Não executar diretamente no banco de produção sem revisão.

## Regras para o casal

1. Cada pessoa possui **um login independente** (sub/UUID do token).
2. Ambos devem pertencer à mesma família para ver o consolidado.
3. A API deriva o **autor** do token; o cliente não pode escolher quem assina
   uma transação.
4. O **responsável pelo gasto** é registrado separadamente. Inicialmente o
   padrão deve ser o próprio usuário logado.
5. Consultas e mutações SEMPRE filtram pelo `familia_id` autorizado; não basta
   receber um UUID de família da interface.
6. Transferências entre contas nunca viram receitas/despesas na visão consolidada.
7. `saldo_inicial` + movimentações efetivadas ≠ `limite de orçamento - despesas`.
   Apresentar **saldo bancário** e **orçamento restante** como métricas distintas.
8. Um imóvel só poderá ser vinculado à família após validação de propriedade,
   usando `fin_imovel_familia`. Nenhum imóvel existente foi associado automaticamente.

## Segurança (bloqueador para publicar)

**A API atual não tem autenticação nem autorização implementadas.** Os endpoints
`/imoveis` atuais também não têm proteção por usuário. **Não expor dados reais**
nem endpoints financeiros até completar esta etapa. O schema `financas` **nao deve ser adicionado** aos
'Exposed schemas' da Data API do Supabase. Uma aplicacao Spring/JDBC que use um
usuario diferente do criador do schema precisara de privilegios SQL concedidos
explicitamente a esse usuario (nunca a `anon` ou `authenticated`).

Implementar antes de conectar o frontend:

- Provedor de identidade (preferencialmente Supabase Auth, já que o banco é
  Supabase, mas ainda não foi adotado pelo código).
- Spring Security Resource Server para validar assinatura, emissor, público e
  prazo dos JWTs, inclusive troca/expiração de chaves.
- Criação e convite de membros em fluxo autenticado e protegido contra
  adivinhação/reutilização de convites.
- Autorização por família em TODO serviço, com testes de isolamento cruzado.
- Restrição de acesso a imóveis pela família legítima.
- Taxa limite em endpoints de autenticação/convites, logs sem valores sensíveis,
  backup persistente e testes de restauração.
- Não usar `JWT_SECRET` de outro projeto automaticamente; evitar dois esquemas
  de login em paralelo e nunca confiar em `usuario_id` do JSON recebido.

## Etapas posteriores (ainda NÃO implementadas)

**Fase 2 — API segura:** autenticação, associação de imóveis, DTOs, JPA,
repositories, serviços com escopo por família e endpoints `/api/financas/**`.
Incluir teste de autorização garantindo que membro da família A não consulte
ou altere dados da família B.

**Fase 3 — frontend único:** criar frontend Meu Imóvel com React/TypeScript,
rota protegida `/financas`; transportar os componentes do arquivo
`NossaGrana-Graficos-FinanCerto.zip`, substituindo o cliente de API e tipos
por DTOs do Meu Imóvel. Manter aviso de copyright MIT de NossaGrana quando
reutilizar código. **Não copiar código do FinanCerto (AGPL).**

**Fase 4 — funcionalidades avançadas:** despesas recorrentes, cartões,
faturas, parcelas, rateio (compartilhado), orçamento familiar, relatórios
vinculados a imóvel e importação/validação de dados legados. As faturas precisam
de tratamento que evite dupla contagem entre compra no cartão e pagamento da
fatura.

## Contrato sugerido de API (somente especificação)

- `GET /api/financas/dashboard?mes=2026-10`
- `GET /api/financas/lancamentos?mes=2026-10`
- `POST /api/financas/lancamentos`
- `PATCH /api/financas/lancamentos/{id}`
- `GET /api/financas/contas`
- `POST /api/financas/contas`
- `GET /api/financas/orcamentos?mes=2026-10`
- `GET /api/financas/imoveis/{imovelId}/despesas`

As rotas acima **não existem ainda**; é necessário implementar autenticação,
autorização, validações, service e controller antes de utilizá-las.

## Checklist antes do merge

- [ ] Testar Flyway V4 e V5 em PostgreSQL local de testes (nunca homologar direto em producao)
- [ ] Executar `docs/sql/financas_smoke_test.sql` em banco de teste; termina com `ROLLBACK`
- [ ] Confirmar que `financas` nao aparece nos schemas expostos da API Supabase
- [ ] Validar constraints e índices, sobretudo referências entre famílias
- [ ] Configurar autenticação e restringir `/imoveis` por dono/família
- [ ] Testar duas contas de usuários e acesso de outro grupo
- [ ] Reaproveitar gráficos do NossaGrana no frontend unificado
- [ ] Validar saldos e despesas com transações/estornos/parcelas
- [ ] Validar versão mobile e HTTPS
- [ ] Revisar licença, backups, migrações e planos de rollback

**Status:** a branch contém somente estrutura privada de banco,
consultas analiticas, roteiro de teste e documentacao de integracao. **Não há implantação nem integração visual concluída**.
