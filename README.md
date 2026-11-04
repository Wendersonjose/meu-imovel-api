
# 🏠 Minha Casa API

Backend REST para gestão imobiliária e financeira familiar, desenvolvido com Java 17, Spring Boot 4.1.1 e PostgreSQL.

O projeto **Minha Casa** nasceu para controlar a aquisição de um imóvel e evoluiu para uma aplicação de gestão financeira compartilhada, permitindo acompanhar receitas, despesas, contas, pagamentos e movimentações relacionadas ao patrimônio familiar.

Além da finalidade prática, o projeto é utilizado para estudar engenharia de software, arquitetura backend, segurança, persistência de dados e desenvolvimento de APIs REST.

> **Status:** desenvolvimento ativo. O módulo de imóveis está implementado. Os módulos financeiros estão em evolução na branch `feature/modulo-financas-casal`. Ainda existem pendências de integração, segurança e homologação antes do uso em produção.

## 🎯 Objetivos

O sistema está sendo desenvolvido para:

- Centralizar informações cadastrais e financeiras de imóveis.
- Registrar o valor de aquisição, entrada e financiamento.
- Controlar receitas e despesas familiares.
- Permitir usuários independentes com informações compartilhadas.
- Identificar o responsável e o autor de cada movimentação.
- Registrar pagamentos realizados e valores previstos.
- Vincular despesas e movimentações financeiras aos imóveis.
- Acompanhar saldos, orçamentos e indicadores financeiros.
- Organizar contratos, recibos e comprovantes.
- Preservar o histórico das movimentações financeiras.

O objetivo arquitetural é manter um único backend, com módulos separados por responsabilidade.

---

## 🛠️ Stack tecnológica

| Tecnologia | Utilização |
|---|---|
| Java 17 | Linguagem principal |
| Spring Boot 4.1.1 | Framework backend |
| Spring Web MVC | Endpoints REST |
| Spring Security | Autenticação e proteção HTTP |
| OAuth2 Resource Server | Validação de JWT |
| Supabase Auth | Provedor de identidade |
| Spring Data JPA | Persistência de entidades |
| Hibernate | ORM |
| Spring JDBC | Consultas e operações SQL financeiras |
| Bean Validation | Validação de DTOs |
| Flyway | Versionamento do banco de dados |
| PostgreSQL | Banco de dados relacional |
| Supabase | Hospedagem do PostgreSQL e autenticação |
| Lombok | Redução controlada de boilerplate |
| Maven | Gerenciamento de dependências e build |
| JUnit 5 / Mockito | Testes automatizados |
| Springdoc OpenAPI | Documentação da API |
| Insomnia | Testes manuais de endpoints |
| IntelliJ IDEA | Ambiente de desenvolvimento |

### Frontend planejado

- React
- TypeScript
- Interface responsiva
- Autenticação integrada ao Supabase
- Dashboard financeiro
- Calendário de despesas e pagamentos
- Gestão de documentos
- Visualização compartilhada por família

O frontend ainda não faz parte deste repositório.

---

## 🏗️ Arquitetura

A aplicação segue uma arquitetura modular em camadas.

```text
                CLIENTE HTTP
                     |
                     v
              Spring Security
               JWT / OAuth2
                     |
                     v
                 Controller
                     |
                     v
                    DTO
                     |
                     v
                  Service
                     |
                     v
                 Repository
                     |
              +------+------+
              |             |
              v             v
          JPA/Hibernate  Spring JDBC
              |             |
              +------+------+
                     |
                     v
                 PostgreSQL
                     |
                     v
                  Supabase
```

### Responsabilidades

**Controller**

Gerencia requisições e respostas HTTP, valida os dados recebidos e delega a execução dos casos de uso.

**DTO**

Transporta dados entre a API e seus consumidores. Os DTOs de entrada e saída são separados, utilizando Java `record` sempre que apropriado.

**Service**

Coordena os casos de uso, as regras de negócio, as transações e as verificações de autorização.

**Repository**

Concentra o acesso à persistência.

O projeto utiliza diferentes abordagens de persistência:

- Spring Data JPA e Hibernate no domínio de imóveis e nas categorias financeiras.
- Spring JDBC, com `NamedParameterJdbcTemplate`, em contas e lançamentos financeiros.
- Consultas SQL nativas em componentes financeiros específicos.

**Banco de dados**

PostgreSQL, com evolução estrutural controlada exclusivamente por migrations Flyway.

---

## 📂 Estrutura do projeto

Estrutura resumida da branch de desenvolvimento:

```text
meu-imovel-api/
|
├── docs/
│   ├── INTEGRACAO_FINANCAS_CASAL.md
│   └── sql/
│       └── financas_smoke_test.sql
|
├── src/
│   ├── main/
│   │   ├── java/com/wenderson/meuimovel/
│   │   │   |
│   │   │   ├── controller/
│   │   │   │   ├── ImovelController.java
│   │   │   │   ├── CategoriaController.java
│   │   │   │   ├── ContaController.java
│   │   │   │   ├── LancamentoController.java
│   │   │   │   └── ResumoCompetenciaController.java
│   │   │   |
│   │   │   ├── domain/
│   │   │   │   ├── imovel/
│   │   │   │   │   ├── Imovel.java
│   │   │   │   │   ├── ImovelRepository.java
│   │   │   │   │   ├── ImovelService.java
│   │   │   │   │   ├── ValidadorCadastroImovel.java
│   │   │   │   │   ├── ImovelDuplicadoException.java
│   │   │   │   │   ├── ImovelNaoEncontradoException.java
│   │   │   │   │   └── DTOs de imóvel
│   │   │   │   |
│   │   │   │   └── financas/
│   │   │   │       ├── AutorizacaoFamiliaService.java
│   │   │   │       ├── MembroFamiliaRepository.java
│   │   │   │       ├── ResumoCompetenciaService.java
│   │   │   │       ├── ResumoCompetenciaRepository.java
│   │   │   │       ├── DadosResumoCompetencia.java
│   │   │   │       ├── categoria/
│   │   │   │       ├── conta/
│   │   │   │       └── lancamento/
│   │   │   |
│   │   │   ├── enums/
│   │   │   │   └── TipoCategoria.java
│   │   │   |
│   │   │   ├── infra/
│   │   │   │   ├── config/
│   │   │   │   │   └── OpenApiConfig.java
│   │   │   │   ├── exception/
│   │   │   │   │   └── TratadorDeErros.java
│   │   │   │   └── security/
│   │   │   │       ├── SecurityConfig.java
│   │   │   │       ├── UsuarioAutenticado.java
│   │   │   │       └── AutorizacaoFamiliaUsuarioAtual.java
│   │   │   |
│   │   │   └── MeuImovelApiApplication.java
│   │   |
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   │           ├── V1__create_table_imovel.sql
│   │           ├── V2__alter_table_imovel_add_dados_cadastrais.sql
│   │           ├── V3__add_unique_constraints_imovel.sql
│   │           ├── V4__create_financas_familia.sql
│   │           └── V5__views_graficos_financeiros.sql
│   |
│   └── test/
│       └── java/com/wenderson/meuimovel/
│           └── Testes de imóveis, finanças,
│               controllers e segurança
|
├── pom.xml
└── README.md
```

A organização por domínio facilita a manutenção e a evolução incremental do backend.

---

## 🗄️ Banco de dados

O sistema utiliza PostgreSQL local no ambiente de desenvolvimento e PostgreSQL hospedado no Supabase.

A conexão com Supabase pode ser realizada pelo JDBC Session Pooler.

### Controle de schema

O Flyway é responsável pelas alterações estruturais.

```properties
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
```

O Hibernate não deve criar ou alterar tabelas automaticamente.

### Migrations

| Migration | Responsabilidade |
|---|---|
| V1 | Criação da tabela de imóveis |
| V2 | Ampliação dos dados cadastrais do imóvel |
| V3 | Restrições de unicidade e prevenção de duplicidades |
| V4 | Criação do schema financeiro familiar |
| V5 | Views para relatórios e indicadores financeiros |

As migrations V4 e V5 estão na branch de desenvolvimento e não fazem parte da `main` atual.

### Schema `public`

Contém a tabela principal:

```text
public.imovel
```

### Schema `financas`

O módulo financeiro possui oito tabelas:

| Tabela | Responsabilidade |
|---|---|
| fin_familia | Identificação da família |
| fin_membro | Usuários vinculados à família |
| fin_categoria | Categorias de receitas e despesas |
| fin_conta | Contas financeiras |
| fin_lancamento | Receitas, despesas e pagamentos |
| fin_orcamento | Limites mensais de orçamento |
| fin_transferencia | Transferências entre contas |
| fin_imovel_familia | Associação entre imóveis e famílias |

O schema financeiro foi modelado com chaves estrangeiras e restrições para preservar a integridade dos dados entre famílias.

### Views financeiras

A migration V5 disponibiliza:

```text
fin_vw_resumo_competencia
fin_vw_fluxo_caixa_diario
fin_vw_despesas_categoria_mes
fin_vw_gastos_responsavel_mes
fin_vw_orcamento_categoria_mes
```

Essas views permitem construir indicadores sem misturar:

- Valores previstos e efetivados.
- Competência e data real do pagamento.
- Receitas e transferências internas.
- Saldo bancário e orçamento disponível.

As views não substituem a autorização na camada de aplicação.

---

## 🏠 Módulo de imóveis

O domínio de imóveis está implementado e permite:

- Cadastrar imóveis.
- Consultar um imóvel por ID.
- Listar imóveis.
- Atualizar dados parcialmente.
- Validar identificadores únicos.
- Evitar duplicidades cadastrais.
- Tratar recursos não encontrados.

### Dados disponíveis

A entidade `Imovel` contempla:

- Descrição e tipo do imóvel.
- Matrícula e CNM.
- Cartório de registro.
- Cadastro municipal.
- Endereço.
- Lote, quadra e loteamento.
- Área do terreno e área construída.
- Habite-se.
- Valor de aquisição.
- Valor financiado.
- Valor de entrada.
- Data da compra.
- Observações.
- Campos de auditoria.

Os valores monetários utilizam `BigDecimal`.

As datas cadastrais utilizam `LocalDate`.

Os registros de auditoria utilizam `Instant`.

### Proteção contra duplicidade

A aplicação valida:

1. CNM.
2. Combinação de cartório e matrícula.
3. Combinação de cidade, UF e cadastro municipal.

As validações são executadas pela aplicação e reforçadas por constraints no PostgreSQL.

### Endpoints

| Método | Endpoint | Operação |
|---|---|---|
| POST | `/imoveis` | Cadastrar imóvel |
| GET | `/imoveis` | Listar imóveis |
| GET | `/imoveis/{id}` | Consultar imóvel |
| PATCH | `/imoveis/{id}` | Atualizar parcialmente |

### Exemplo de cadastro

```http
POST /imoveis
Content-Type: application/json
Authorization: Bearer SEU_TOKEN
```

```json
{
  "descricao": "Imóvel residencial de exemplo",
  "tipoImovel": "Residencial unifamiliar",
  "matricula": "12345",
  "cnm": "000000.0.0000000-00",
  "cartorio": "Cartório de Registro de Imóveis",
  "cadastroMunicipal": "000000000",
  "logradouro": "Rua Exemplo",
  "numero": "100",
  "cidade": "Cidade Exemplo",
  "uf": "MG",
  "lote": "10",
  "quadra": "5",
  "loteamento": "Residencial Exemplo",
  "areaTerreno": 250.00,
  "areaConstruida": 100.00,
  "numeroHabiteSe": "0000/2026",
  "dataHabiteSe": "2026-01-10",
  "valorImovel": 400000.00,
  "valorFinanciado": 320000.00,
  "valorEntrada": 80000.00,
  "dataCompra": "2026-10-03",
  "observacao": "Cadastro fictício para documentação."
}
```

Em caso de sucesso, retorna `201 Created`.

A operação também devolve o cabeçalho `Location`.

Em caso de duplicidade, retorna `409 Conflict`.

> O Bearer Token é exigido na branch financeira. Na `main` atual, a configuração de segurança ainda não foi integrada.

---

## 👨‍👩‍👧 Módulo financeiro familiar

O módulo financeiro foi projetado para dois ou mais usuários de uma mesma família.

Cada usuário possui identidade independente, enquanto as informações financeiras são associadas à família.

### Regras principais

- Cada pessoa pode utilizar seu próprio login.
- Usuários ativos da mesma família podem acessar os dados financeiros compartilhados.
- A autoria de cada lançamento é determinada pelo JWT.
- O responsável financeiro é registrado separadamente do autor.
- Contas, categorias e lançamentos pertencem a uma família.
- Movimentações não podem referenciar contas ou categorias de outra família.
- Despesas podem ser associadas a imóveis previamente vinculados.
- Transferências entre contas não devem ser contabilizadas como receitas ou despesas.

A estrutura de membros prevê os papéis `ADMIN` e `MEMBRO`.

A existência desses papéis no banco não significa que todas as permissões administrativas específicas já estejam implementadas na API.

---

## 🗂️ Categorias financeiras

O CRUD de categorias está implementado.

### Tipos de categoria

```text
RECEITA
DESPESA
```

### Funcionalidades

- Cadastro de categorias.
- Listagem de categorias ativas.
- Consulta por ID.
- Alteração de nome.
- Desativação lógica.
- Validação de categorias duplicadas.
- Verificação de vínculo ativo com a família.

O sistema não exclui fisicamente uma categoria por meio do endpoint DELETE.

Em vez disso, altera o indicador `ativa` para `false`.

### Endpoints

Prefixo:

```text
/familias/{familiaId}/financas/categorias
```

| Método | Rota | Operação |
|---|---|---|
| POST | `/` | Cadastrar |
| GET | `/` | Listar ativas |
| GET | `/{id}` | Consultar |
| PUT | `/{id}` | Atualizar |
| DELETE | `/{id}` | Desativar |

### Exemplo

```json
{
  "nome": "Despesas cartorárias",
  "tipo": "DESPESA"
}
```

O CRUD foi validado manualmente com Insomnia durante o desenvolvimento.

---

## 🏦 Contas financeiras

O backend possui suporte inicial para cadastro e listagem de contas financeiras.

### Tipos disponíveis

```text
CORRENTE
POUPANCA
CARTEIRA
INVESTIMENTO
OUTRA
```

### Campos

- Nome da conta.
- Tipo.
- Saldo inicial.
- Data do saldo inicial.
- Usuário titular.
- Família.
- Situação de atividade.

O saldo inicial representa o valor informado no início do acompanhamento.

Ele não deve ser confundido com um saldo bancário atualizado automaticamente.

### Endpoints

Prefixo:

```text
/familias/{familiaId}/financas/contas
```

| Método | Rota | Operação |
|---|---|---|
| POST | `/` | Cadastrar conta |
| GET | `/` | Listar contas ativas |

### Exemplo

```json
{
  "nome": "Conta principal",
  "tipo": "CORRENTE",
  "saldoInicial": 0.00,
  "dataSaldoInicial": "2026-11-01"
}
```

A implementação atual utiliza `NamedParameterJdbcTemplate` para acessar o banco de dados.

Ainda não existe CRUD completo de contas.

---

## 💰 Lançamentos financeiros

O módulo de lançamentos permite registrar receitas e despesas vinculadas à família.

### Informações registradas

- Família.
- Autor.
- Responsável financeiro.
- Conta.
- Categoria.
- Tipo.
- Descrição.
- Valor.
- Competência.
- Vencimento.
- Data da efetivação.
- Status.
- Imóvel vinculado, quando aplicável.
- Observações.
- Datas de criação e atualização.

### Status

```text
PREVISTO
EFETIVADO
CANCELADO
```

**PREVISTO:** movimentação planejada ou ainda não efetivada.

**EFETIVADO:** movimentação realizada, com data de efetivação.

**CANCELADO:** movimentação cancelada, que não pode ser efetivada diretamente pelo endpoint de pagamento.

### Regras de validação

- O usuário precisa ter acesso à família.
- A conta informada deve estar ativa e pertencer à família.
- A categoria deve estar ativa e corresponder ao tipo da movimentação.
- O imóvel, quando informado, precisa estar vinculado à família.
- Lançamentos efetivados exigem data de efetivação.
- Lançamentos previstos ou cancelados não podem possuir data de efetivação.
- O autor é extraído do usuário autenticado.

### Endpoints

Prefixo:

```text
/familias/{familiaId}/financas/lancamentos
```

| Método | Rota | Operação |
|---|---|---|
| POST | `/` | Cadastrar lançamento |
| GET | `/` | Listar lançamentos |
| GET | `/{id}` | Consultar lançamento |
| PUT | `/{id}` | Atualizar lançamento |
| PATCH | `/{id}/pagar` | Registrar pagamento |

### Filtros de listagem

O endpoint GET aceita filtros opcionais:

```http
GET /familias/{familiaId}/financas/lancamentos?mes=2026-11
```

Também permite filtrar pelo imóvel:

```http
GET /familias/{familiaId}/financas/lancamentos?imovelId=1
```

Os filtros podem ser combinados.

O parâmetro `mes` utiliza o formato `AAAA-MM`.

A implementação atual limita a listagem a 500 registros. Ainda não há paginação completa.

### Exemplo de lançamento

```http
POST /familias/{familiaId}/financas/lancamentos
Content-Type: application/json
Authorization: Bearer SEU_TOKEN
```

```json
{
  "contaId": "UUID_DA_CONTA",
  "categoriaId": "UUID_DA_CATEGORIA",
  "tipo": "DESPESA",
  "descricao": "Emolumentos cartorários",
  "valor": 250.00,
  "competencia": "2026-11-01",
  "vencimento": "2026-11-15",
  "status": "PREVISTO",
  "efetivadoEm": null,
  "imovelId": null,
  "observacao": "Exemplo de despesa relacionada à aquisição."
}
```

Os identificadores devem ser substituídos por UUIDs existentes e autorizados.

O campo `imovelId` pode receber um ID válido de imóvel vinculado à família.

### Registrar pagamento

```http
PATCH /familias/{familiaId}/financas/lancamentos/{id}/pagar
```

```json
{
  "dataPagamento": "2026-11-15"
}
```

Essa operação altera o lançamento para `EFETIVADO` e registra a data informada.

### Estado da implementação

Os controllers, services, DTOs e repositories foram incorporados à branch de desenvolvimento.

Existem testes unitários de regras de autorização e validação.

A homologação completa com JWT real, PostgreSQL e operações HTTP deve ser concluída antes do uso em produção.

---

## 📊 Resumos e indicadores financeiros

A API disponibiliza uma consulta de resumo por competência.

```http
GET /familias/{familiaId}/financas/resumo
```

O endpoint retorna registros contendo:

- Família.
- Mês de referência.
- Receitas efetivadas.
- Despesas efetivadas.
- Receitas previstas.
- Despesas previstas.

O acesso passa pela verificação do vínculo ativo entre usuário e família.

As outras views financeiras já estão disponíveis na estrutura SQL, mas ainda não possuem todos os endpoints de relatórios correspondentes.

### Indicadores planejados

- Entradas e saídas por período.
- Saldo das contas.
- Despesas por categoria.
- Gastos por responsável.
- Despesas previstas e realizadas.
- Orçamento utilizado e disponível.
- Evolução financeira mensal.
- Custos relacionados à aquisição do imóvel.

---

## 🔐 Autenticação e segurança

A branch de desenvolvimento utiliza Spring Security com OAuth2 Resource Server.

A autenticação foi estruturada para aceitar JWTs emitidos pelo Supabase Auth.

### Configuração

O backend utiliza:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=${SUPABASE_URL}/auth/v1

spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${SUPABASE_URL}/auth/v1/.well-known/jwks.json

spring.security.oauth2.resourceserver.jwt.jws-algorithms=ES256

spring.security.oauth2.resourceserver.jwt.audiences=authenticated
```

### Regras

- API stateless.
- Autenticação por Bearer Token.
- Validação da assinatura JWT.
- Validação do emissor e da audiência.
- Identificação do usuário pelo claim `sub`.
- Verificação de vínculo familiar nos serviços financeiros.
- Bloqueio de acesso financeiro quando o usuário não pertence à família.

### Ponto de atenção

A autenticação não substitui a autorização por recurso.

**Pendência de segurança:** o `ImovelController` ainda utiliza operações globais de cadastro, consulta, listagem e atualização. A proteção por JWT não impede, por si só, que um usuário autenticado consulte ou altere imóveis que não lhe pertencem.

Antes de publicar o sistema para múltiplos usuários, o acesso aos imóveis deverá ser limitado conforme sua associação familiar e a identidade autenticada.

O schema `financas` também não deve ser exposto diretamente pela Data API do Supabase.

---

## ⚙️ Configuração de ambiente

O sistema utiliza variáveis de ambiente para evitar credenciais sensíveis no repositório.

### Variáveis necessárias

```text
DB_URL
DB_USERNAME
DB_PASSWORD
SUPABASE_URL
```

### Exemplo de configuração

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=none

spring.flyway.enabled=true
```

A variável `SUPABASE_URL` deve conter a URL base do projeto Supabase, sem o sufixo `/auth/v1`.

### Segurança

Não armazenar no Git:

- Senhas de banco de dados.
- Tokens de autenticação.
- Chaves privadas.
- Credenciais de serviços externos.
- Arquivos `.env` com dados reais.

---

## ▶️ Executando a aplicação

### Pré-requisitos

- JDK 17.
- Maven compatível com o projeto.
- PostgreSQL disponível.
- Banco com as permissões necessárias.
- Configuração das variáveis de ambiente.
- Acesso às chaves públicas do Supabase Auth.

### Clonar o repositório

```bash
git clone https://github.com/Wendersonjose/meu-imovel-api.git
```

```bash
cd meu-imovel-api
```

### Acessar a branch financeira

```bash
git checkout feature/modulo-financas-casal
```

### Compilar e testar

```bash
mvn clean test
```

### Iniciar a aplicação

```bash
mvn spring-boot:run
```

Com a configuração padrão, a API utiliza a porta 8080.

```text
http://localhost:8080
```

Durante a inicialização, o Flyway valida e aplica as migrations pendentes no banco configurado.

**Atenção:** confirmar o valor de `DB_URL` antes de executar. Evitar aplicar migrations em um banco de produção durante testes de desenvolvimento.

### Testar endpoints

As rotas protegidas exigem:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

O token deve ser emitido pelo provedor Supabase Auth configurado para o projeto.

A API atua como Resource Server e não implementa um endpoint próprio de login.

---

## 📖 Swagger e OpenAPI

A dependência Springdoc está presente no projeto.

Existe configuração OpenAPI específica para o perfil `dev`.

Entretanto, as propriedades atuais desabilitam a documentação por padrão:

```properties
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

Para disponibilizar a documentação em desenvolvimento, é necessário habilitar essas propriedades no ambiente apropriado.

O Spring Security permite acesso público às rotas da documentação somente quando o perfil `dev` está ativo.

Não habilitar o Swagger publicamente em produção sem uma revisão de segurança.

---

## 🧪 Estratégia de testes

O projeto utiliza:

- JUnit 5.
- Mockito.
- Spring Security Test.
- Testes de controllers.
- Testes de serviços.
- Testes de integração de persistência.
- Testes de migrations.
- Insomnia para validação HTTP.

### Cenários já trabalhados

**Imóveis**

- Cadastro.
- Listagem.
- Consulta por ID.
- Atualização parcial.
- Tratamento de imóvel inexistente.
- Verificação de duplicidade.

**Segurança**

- Autenticação JWT.
- Requisições sem autenticação.
- Extração da identidade autenticada.
- Verificação de vínculo familiar.
- Negação de acesso sem permissão.

**Categorias financeiras**

- Cadastro.
- Listagem.
- Consulta.
- Atualização.
- Desativação lógica.
- Prevenção de duplicidade.

**Lançamentos financeiros**

- Negação de acesso sem vínculo familiar.
- Rejeição de conta inexistente.
- Rejeição de imóvel não vinculado.
- Rejeição de lançamento efetivado sem data.

O teste unitário `LancamentoServiceTest` possui quatro cenários implementados.

### Validação pendente

Ainda é necessário consolidar a execução da suíte completa e homologar os fluxos de contas e lançamentos com requisições HTTP reais e JWT válido.

A presença dos testes no repositório não deve ser interpretada como garantia de aprovação de todos os testes em qualquer ambiente.

### Executar testes

```bash
mvn clean test
```

---

## 📁 Módulo de documentos — planejado

O próximo módulo funcional será destinado à gestão de documentos relacionados à aquisição do imóvel.

### Requisitos

- Upload de arquivos.
- Associação a um imóvel.
- Associação a uma despesa ou movimentação.
- Identificação do usuário responsável pelo envio.
- Registro da data do upload.
- Classificação do documento.
- Download controlado por autorização.
- Organização por categoria documental.

### Documentos previstos

- Contratos.
- Matrícula e certidões.
- Comprovantes de pagamento.
- Recibos.
- Documentos cartorários.
- Documentação do financiamento.
- Extratos e comprovantes de FGTS.
- Outros documentos relacionados à aquisição.

### Diretriz de arquitetura

Os arquivos deverão utilizar armazenamento apropriado, com acesso privado.

O PostgreSQL deverá manter os metadados e os vínculos dos documentos.

As operações de upload e download precisam validar a identidade do usuário e sua autorização de acesso ao imóvel ou lançamento correspondente.

**Status:** não implementado.

---

## 🖥️ Frontend — planejado

Será desenvolvida uma aplicação web unificada para acesso pelo computador e celular.

### Tecnologias previstas

- React.
- TypeScript.
- Integração REST.
- Gerenciamento de autenticação.
- Componentização.
- Gráficos financeiros responsivos.
- Interface adaptada para desktop e mobile.

### Telas planejadas

1. Autenticação.
2. Dashboard principal.
3. Cadastro e detalhamento de imóveis.
4. Gestão financeira familiar.
5. Contas e saldos.
6. Receitas e despesas.
7. Calendário financeiro.
8. Pagamentos e comprovantes.
9. Documentos.
10. Relatórios.
11. Configurações de usuário e família.

A interface deverá possuir identidade visual própria, componentes reutilizáveis e fluxos de navegação consistentes.

A referência funcional do módulo financeiro é o NossaGrana, com adaptação de conceitos e gráficos ao sistema Minha Casa, respeitando as licenças de software aplicáveis.

**Status:** ainda não implementado neste repositório.

---

## 🧭 Roadmap

### Fase 1 — Imóveis

- [x] Criar projeto Spring Boot.
- [x] Configurar PostgreSQL.
- [x] Configurar Flyway.
- [x] Criar entidade de imóvel.
- [x] Criar DTOs.
- [x] Implementar cadastro.
- [x] Implementar listagem.
- [x] Implementar consulta.
- [x] Implementar atualização parcial.
- [x] Validar duplicidades.
- [x] Implementar tratamento global de erros.

### Fase 2 — Finanças familiares

- [x] Criar schema financeiro.
- [x] Criar tabelas de famílias e membros.
- [x] Criar tabelas de contas e categorias.
- [x] Criar tabela de lançamentos.
- [x] Criar estrutura de orçamentos.
- [x] Criar estrutura de transferências.
- [x] Criar vínculo entre imóvel e família.
- [x] Criar views financeiras.
- [x] Integrar Spring Security com validação JWT.
- [x] Implementar autorização por vínculo familiar nos serviços financeiros existentes.
- [x] Implementar CRUD de categorias.
- [x] Implementar cadastro e listagem de contas.
- [x] Implementar cadastro e consulta de lançamentos.
- [x] Implementar atualização de lançamentos.
- [x] Implementar registro de pagamentos.
- [x] Implementar consulta de resumo por competência.
- [ ] Concluir homologação integrada dos lançamentos financeiros.
- [ ] Validar o fluxo de despesas vinculadas ao imóvel, incluindo operações relacionadas ao FGTS.
- [ ] Revisar controle de acesso por família nos endpoints de imóveis.
- [ ] Implementar API de gestão de famílias e membros.
- [ ] Implementar operações de transferências.
- [ ] Implementar operações de orçamento.
- [ ] Implementar saldo financeiro consolidado.
- [ ] Implementar paginação e filtros avançados.
- [ ] Consolidar testes de integração e segurança.

### Fase 3 — Documentos

- [ ] Definir modelagem dos documentos.
- [ ] Criar migration de metadados.
- [ ] Definir armazenamento privado.
- [ ] Implementar upload.
- [ ] Implementar download autenticado.
- [ ] Vincular documentos aos imóveis.
- [ ] Vincular documentos aos lançamentos.
- [ ] Implementar listagem e classificação.
- [ ] Validar acesso entre famílias.

### Fase 4 — Frontend

- [ ] Criar projeto React com TypeScript.
- [ ] Definir design system.
- [ ] Implementar autenticação.
- [ ] Criar layout responsivo.
- [ ] Criar dashboard.
- [ ] Criar gestão de imóveis.
- [ ] Criar gestão de contas.
- [ ] Criar formulários financeiros.
- [ ] Criar calendário financeiro.
- [ ] Criar gestão de documentos.
- [ ] Criar gráficos e relatórios.
- [ ] Integrar frontend à API.
- [ ] Executar testes de usabilidade.

### Fase 5 — Evolução

- [ ] Despesas recorrentes.
- [ ] Parcelamentos.
- [ ] Cartões de crédito e faturas.
- [ ] Orçamentos mensais.
- [ ] Relatórios históricos.
- [ ] Exportação de dados.
- [ ] Auditoria de alterações.
- [ ] Observabilidade.
- [ ] Estratégia de backup e restauração.
- [ ] Pipeline de CI/CD.
- [ ] Deploy de produção.

---

## 🌿 Estratégia de branches

### `main`

Branch principal do projeto.

Atualmente contém a implementação inicial de imóveis, sem a integração das funcionalidades financeiras desenvolvidas posteriormente.

### `feature/modulo-financas-casal`

Branch de desenvolvimento do módulo financeiro familiar.

Contém:

- Flyway V4 e V5.
- Integração com Spring Security.
- Validação de JWT Supabase.
- Autorização financeira por família.
- Categorias.
- Contas.
- Lançamentos.
- Resumos financeiros.
- Testes automatizados associados.

### Pull requests

**PR #1 — Estrutura do módulo financeiro**

https://github.com/Wendersonjose/meu-imovel-api/pull/1

Status em 04/11/2026: aberto como rascunho, sem integração à `main`.

**PR #2 — V1 de contas e lançamentos**

https://github.com/Wendersonjose/meu-imovel-api/pull/2

Status em 04/11/2026: integrado à branch `feature/modulo-financas-casal`.

O merge do PR #2 na branch financeira não significa que o módulo foi publicado na `main` ou em produção.

---

## ⚠️ Pendências prioritárias

Antes de considerar a API pronta para produção:

1. Corrigir a autorização de leitura e alteração de imóveis.
2. Validar JWT real e acesso cruzado entre famílias.
3. Executar a suíte completa de testes no ambiente configurado.
4. Homologar cadastro, atualização e pagamento dos lançamentos pelo Insomnia.
5. Garantir o tratamento correto de movimentações relacionadas ao FGTS, evitando dupla contabilização.
6. Rever privilégios de banco e exposição do schema financeiro.
7. Definir o armazenamento seguro de documentos e comprovantes.
8. Revisar migrações, backups e procedimentos de recuperação.
9. Integrar as alterações aprovadas à branch principal.

---

## 📐 Princípios de desenvolvimento

O projeto segue as seguintes decisões:

- Separar responsabilidades entre Controller, Service e Repository.
- Utilizar DTOs específicos para entrada e saída.
- Preferir `record` para objetos de transporte.
- Evitar expor entidades JPA diretamente na API.
- Utilizar `BigDecimal` para operações monetárias.
- Evitar setters públicos indiscriminados.
- Usar construtores e métodos de domínio.
- Aplicar injeção de dependência por construtor.
- Centralizar o tratamento de exceções.
- Utilizar validação de entrada com Bean Validation.
- Manter operações transacionais consistentes.
- Validar a identidade do usuário no servidor.
- Impedir acesso financeiro entre famílias diferentes.
- Controlar alterações estruturais exclusivamente pelo Flyway.
- Manter credenciais fora do código-fonte.
- Implementar e testar as funcionalidades incrementalmente.

O desenvolvimento prioriza a compreensão dos conceitos de Java, Spring Boot, banco de dados e arquitetura de software, evitando dependência exclusiva de código gerado automaticamente.

---

## 📌 Estado do desenvolvimento

**Última revisão deste documento:** 04 de novembro de 2026.

O backend possui uma base funcional para cadastro imobiliário e uma primeira versão do módulo financeiro familiar na branch de desenvolvimento.

As próximas prioridades são finalizar a homologação dos lançamentos financeiros, validar as movimentações associadas ao FGTS e iniciar o módulo de documentos.

O frontend React/TypeScript será desenvolvido após a consolidação dos contratos de API necessários.

O sistema permanece em desenvolvimento e não deve ser considerado homologado para uso público ou multiusuário em produção.

---

## 👨‍💻 Autor

**Wenderson José da Silva**

Desenvolvedor Full Stack.

Projeto desenvolvido para aplicação prática de engenharia de software, Java, Spring Boot, PostgreSQL, segurança, arquitetura de APIs REST e gestão financeira.

**GitHub:** https://github.com/Wendersonjose

**LinkedIn:** https://www.linkedin.com/in/wenderson-jose
