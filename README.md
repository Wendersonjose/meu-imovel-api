# 🏠 Minha Casa API

API REST desenvolvida com Java e Spring Boot para controle e acompanhamento da compra de um imóvel.

O projeto possui dois objetivos principais:

1. Organizar informações financeiras, cadastrais e documentais relacionadas à aquisição de um imóvel.
2. Servir como projeto prático de estudo de Java, Spring Boot, PostgreSQL e arquitetura backend.

---

## 🚀 Tecnologias

- Java 17
- Spring Boot 4.1.1
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- Flyway
- PostgreSQL
- Supabase
- Lombok
- IntelliJ IDEA
- Insomnia

O frontend será desenvolvido futuramente utilizando React e TypeScript.

---

## 📌 Objetivo do sistema

A aplicação será utilizada para registrar e acompanhar:

- dados do imóvel;
- valor de compra;
- entrada;
- financiamento;
- pagamentos;
- despesas relacionadas à aquisição;
- recibos;
- comprovantes;
- contratos;
- documentos;
- histórico das movimentações.

O desenvolvimento está sendo realizado de forma incremental, começando pelo domínio de imóvel.

---

## 🏗️ Arquitetura

A aplicação segue a separação:

```text
Controller
    ↓
DTO
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
PostgreSQL
```

### Responsabilidades

**Controller**

Responsável pela camada HTTP da aplicação.

Recebe as requisições, delega os casos de uso para o service e devolve as respostas HTTP.

**DTO**

Responsável pelo transporte de dados de entrada e saída da API.

A aplicação utiliza Java `record` sempre que apropriado.

**Service**

Responsável por coordenar os casos de uso da aplicação.

**Repository**

Responsável pelo acesso aos dados utilizando Spring Data JPA.

**Componentes de domínio**

Regras específicas podem ser delegadas para componentes especializados.

Exemplo:

```text
ValidadorCadastroImovel
```

Esse componente concentra as regras de validação de duplicidade durante o cadastro e a atualização de imóveis.

**JPA / Hibernate**

Responsável pelo mapeamento entre objetos Java e tabelas relacionais.

**PostgreSQL**

Banco de dados utilizado para persistência das informações.

---

## 📂 Estrutura atual do projeto

```text
src/main/java/com/wenderson/meuimovel
│
├── controller
│   └── ImovelController.java
│
├── domain
│   └── imovel
│       ├── Imovel.java
│       ├── ImovelRepository.java
│       ├── ImovelService.java
│       ├── ValidadorCadastroImovel.java
│       ├── ImovelDuplicadoException.java
│       ├── ImovelNaoEncontradoException.java
│       ├── DadosCadastroImovel.java
│       ├── DadosAtualizacaoImovel.java
│       └── DadosDetalhamentoImovel.java
│
├── infra
│   └── exception
│       └── TratadorDeErros.java
│
└── MeuImovelApiApplication.java
```

Os futuros domínios de pagamento, documento e usuário serão adicionados conforme essas funcionalidades forem desenvolvidas.

---

## 🗄️ Banco de dados

O projeto utiliza PostgreSQL hospedado no Supabase.

A conexão da aplicação é realizada através do JDBC Session Pooler.

As credenciais não ficam armazenadas diretamente no código-fonte.

São utilizadas as variáveis de ambiente:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

O `application.properties` utiliza:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Dessa forma, informações sensíveis não precisam ser versionadas no GitHub.

---

## 🔄 Controle do schema com Flyway

A estrutura do banco de dados é controlada exclusivamente pelo Flyway.

O Hibernate está configurado com:

```properties
spring.jpa.hibernate.ddl-auto=none
```

As migrations ficam em:

```text
src/main/resources/db/migration
```

Migrations implementadas:

```text
V1__create_table_imovel.sql
V2__alter_table_imovel_add_dados_cadastrais.sql
V3__add_unique_constraints_imovel.sql
```

### V1

Criação inicial da tabela `imovel`.

### V2

Inclusão dos dados cadastrais do imóvel e adequação dos campos de auditoria para `TIMESTAMPTZ`.

### V3

Inclusão de constraints de unicidade para impedir cadastros duplicados utilizando:

- CNM;
- cartório + matrícula;
- cidade + UF + cadastro municipal.

---

## 🏠 Domínio de imóvel

A entidade `Imovel` representa os dados principais de um imóvel.

Entre os campos existentes estão:

- descrição;
- tipo do imóvel;
- matrícula;
- CNM;
- cartório;
- cadastro municipal;
- endereço;
- lote;
- quadra;
- loteamento;
- área do terreno;
- área construída;
- habite-se;
- valor do imóvel;
- valor financiado;
- valor da entrada;
- data da compra;
- observações;
- data de criação;
- data de atualização.

Para valores monetários e áreas é utilizado:

```java
BigDecimal
```

Para datas sem horário:

```java
LocalDate
```

Para os campos de auditoria:

```java
Instant
```

---

## ✨ Lombok

A entidade `Imovel` utiliza Lombok para reduzir código repetitivo.

Atualmente são utilizadas:

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
```

O `@Getter` gera automaticamente os métodos de leitura dos atributos.

O `@NoArgsConstructor` gera o construtor sem argumentos necessário para o funcionamento do JPA/Hibernate.

Não é utilizado `@Data`, pois não queremos gerar setters públicos indiscriminadamente para a entidade.

As alterações da entidade são realizadas através de métodos de domínio.

Exemplo:

```java
imovel.atualizar(dados);
```

---

## 📦 DTOs

As entidades JPA não são expostas diretamente pela API.

Atualmente existem:

```text
DadosCadastroImovel
DadosAtualizacaoImovel
DadosDetalhamentoImovel
```

Os DTOs são implementados utilizando Java `record`.

### DadosCadastroImovel

Utilizado como DTO de entrada no cadastro de um imóvel.

```text
POST /imoveis
```

### DadosAtualizacaoImovel

Utilizado como DTO de entrada na atualização parcial de um imóvel.

```text
PATCH /imoveis/{id}
```

Os campos são opcionais porque apenas os dados enviados devem ser alterados.

### DadosDetalhamentoImovel

Utilizado como DTO de saída da API.

O fluxo de saída segue:

```text
Imovel
    ↓
DadosDetalhamentoImovel
    ↓
JSON
```

---

## ✅ Validação de duplicidade

Antes de persistir ou atualizar determinadas informações de um imóvel, o sistema executa validações através do componente:

```text
ValidadorCadastroImovel
```

São verificadas três formas de identificação:

```text
CNM

Cartório + Matrícula

Cidade + UF + Cadastro Municipal
```

Caso seja encontrada uma duplicidade, é lançada:

```text
ImovelDuplicadoException
```

O tratamento global converte essa exceção em:

```http
409 Conflict
```

Exemplo:

```json
{
  "mensagem": "Já existe um imóvel cadastrado com este CNM."
}
```

Além da validação feita pela aplicação, o PostgreSQL também possui constraints `UNIQUE`, garantindo uma segunda camada de proteção da integridade dos dados.

---

## 🔄 Validação durante atualização

Durante uma atualização, o imóvel que está sendo alterado não pode ser considerado uma duplicidade dele mesmo.

Por exemplo, se o imóvel de ID `1` já possui determinado CNM, enviar novamente esse mesmo CNM no `PATCH` deve ser permitido.

Para isso, o repository possui consultas que desconsideram o ID atual.

Exemplo:

```java
existsByCnmAndIdNot(cnm, id)
```

O mesmo princípio é utilizado para:

```text
cartório + matrícula

cidade + UF + cadastro municipal
```

Assim, a validação procura somente outro imóvel com os mesmos identificadores.

---

## 🔎 Imóvel não encontrado

Ao consultar ou atualizar um imóvel por ID, o service utiliza a busca pelo repository.

```java
repository.findById(id)
```

Caso o imóvel não exista, é lançada:

```text
ImovelNaoEncontradoException
```

O tratamento global converte essa exceção em:

```http
404 Not Found
```

Exemplo:

```json
{
  "mensagem": "Imóvel não encontrado com o id: 999"
}
```

---

## 🌐 Endpoints

### Resumo

| Método | Endpoint | Descrição | Sucesso |
|---|---|---|---|
| POST | `/imoveis` | Cadastrar imóvel | `201 Created` |
| GET | `/imoveis` | Listar imóveis | `200 OK` |
| GET | `/imoveis/{id}` | Detalhar imóvel | `200 OK` |
| PATCH | `/imoveis/{id}` | Atualizar parcialmente um imóvel | `200 OK` |

---

## ➕ Cadastrar imóvel

```http
POST /imoveis
```

Exemplo de requisição utilizando dados fictícios:

```json
{
  "descricao": "Imóvel residencial",
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
  "observacao": "Exemplo fictício para documentação da API."
}
```

Resposta de sucesso:

```http
201 Created
```

A resposta também possui o header `Location` apontando para o recurso criado.

Exemplo:

```text
/imoveis/1
```

Em caso de duplicidade:

```http
409 Conflict
```

---

## 📋 Listar imóveis

```http
GET /imoveis
```

Retorna uma lista contendo os imóveis cadastrados.

Resposta de sucesso:

```http
200 OK
```

Exemplo simplificado:

```json
[
  {
    "id": 1,
    "descricao": "Imóvel residencial",
    "tipoImovel": "Residencial unifamiliar",
    "cidade": "Cidade Exemplo",
    "uf": "MG",
    "valorImovel": 400000.00
  }
]
```

A API retorna o DTO completo de detalhamento para cada imóvel.

---

## 🔍 Detalhar imóvel por ID

```http
GET /imoveis/{id}
```

Exemplo:

```http
GET /imoveis/1
```

Resposta quando encontrado:

```http
200 OK
```

Caso não exista:

```http
404 Not Found
```

Exemplo:

```json
{
  "mensagem": "Imóvel não encontrado com o id: 999"
}
```

---

## ✏️ Atualizar imóvel por ID

```http
PATCH /imoveis/{id}
```

O endpoint permite atualizar parcialmente os dados de um imóvel.

Somente os campos enviados na requisição são alterados.

Exemplo:

```http
PATCH /imoveis/1
```

Body:

```json
{
  "observacao": "Contrato assinado."
}
```

Resposta de sucesso:

```http
200 OK
```

A atualização utiliza o DTO:

```text
DadosAtualizacaoImovel
```

O service primeiro localiza o imóvel:

```java
var imovel = buscarPorId(id);
```

Depois valida possíveis duplicidades:

```java
validador.validarAtualizacao(imovel, dados);
```

Por fim, delega a alteração para a própria entidade:

```java
imovel.atualizar(dados);
```

O método de domínio divide as alterações em grupos:

```text
dados cadastrais
endereço
características do imóvel
dados financeiros
```

Como o objeto recuperado pelo JPA permanece gerenciado dentro da transação, o Hibernate identifica as alterações realizadas e executa o `UPDATE` através do mecanismo de dirty checking.

Não é necessário chamar explicitamente:

```java
repository.save(imovel);
```

para essa atualização.

O campo:

```text
updatedAt
```

é atualizado automaticamente pelo Hibernate através de:

```java
@UpdateTimestamp
```

Caso o imóvel informado não exista:

```http
404 Not Found
```

---

## 🧪 Testes realizados no PATCH

O endpoint de atualização foi testado manualmente através do Insomnia.

Foram verificados os seguintes cenários:

```text
alteração parcial de observação
→ 200 OK

manutenção do mesmo CNM do próprio imóvel
→ 200 OK

manutenção da mesma matrícula + cartório
→ 200 OK

manutenção do mesmo cadastro municipal
→ 200 OK

tentativa de atualizar imóvel inexistente
→ 404 Not Found
```

Os testes confirmaram que a validação de atualização não considera o próprio imóvel como duplicado.

---

## ⚠️ Tratamento de erros

O tratamento global de exceções está localizado em:

```text
src/main/java/com/wenderson/meuimovel/infra/exception
```

Classe responsável:

```text
TratadorDeErros.java
```

Atualmente são tratados:

```text
ImovelDuplicadoException
→ 409 Conflict

ImovelNaoEncontradoException
→ 404 Not Found
```

Esse modelo evita concentrar tratamento de exceções dentro dos controllers.

---

## 🔐 Segurança das configurações

Credenciais, senhas, tokens, certificados e outras informações sensíveis não devem ser versionados.

O projeto utiliza variáveis de ambiente para as configurações do banco.

O `.gitignore` também protege arquivos locais como:

```text
.env
.env.*
*.pem
*.key
*.p12
*.pfx
*.jks
*.keystore
```

As credenciais do PostgreSQL/Supabase não ficam diretamente no `application.properties`.

---

## 📐 Decisões de arquitetura

O projeto segue as seguintes regras:

- DTO de entrada separado de DTO de saída;
- uso de `record` para DTOs quando apropriado;
- entidades JPA não são expostas diretamente pela API;
- Lombok utilizado apenas para reduzir boilerplate controlado;
- setters públicos indiscriminados são evitados;
- alterações da entidade são feitas através de métodos de domínio;
- injeção de dependência por construtor;
- controllers concentram responsabilidades HTTP;
- services coordenam os casos de uso;
- repositories concentram persistência;
- regras específicas podem ser delegadas a componentes especializados;
- tratamento de exceções é centralizado;
- schema é controlado pelo Flyway;
- alterações estruturais são feitas através de migrations;
- credenciais não são armazenadas diretamente no repositório.

---

## 📊 Evolução futura

A modelagem está sendo preparada para permitir análises históricas e futura integração com ferramentas como Power BI.

A intenção é manter dados relativamente estáveis do imóvel separados de movimentações como:

```text
pagamentos
despesas
avaliações
documentos
eventos financeiros
histórico de valores
```

Isso permitirá preservar histórico e construir análises temporais futuramente.

Esses domínios serão implementados posteriormente.

---

## ✅ Estado atual

Já implementado:

- projeto Spring Boot;
- Java 17;
- conexão PostgreSQL/Supabase;
- configuração através de variáveis de ambiente;
- Flyway;
- migrations V1, V2 e V3;
- entidade `Imovel`;
- Lombok na entidade;
- DTO de cadastro `DadosCadastroImovel`;
- DTO de atualização `DadosAtualizacaoImovel`;
- DTO de detalhamento `DadosDetalhamentoImovel`;
- `ImovelRepository`;
- `ImovelService`;
- `ImovelController`;
- `ValidadorCadastroImovel`;
- validação de duplicidade;
- validação de duplicidade durante atualização;
- validação que desconsidera o próprio imóvel durante atualização;
- `ImovelDuplicadoException`;
- `ImovelNaoEncontradoException`;
- `TratadorDeErros`;
- `POST /imoveis`;
- `GET /imoveis`;
- `GET /imoveis/{id}`;
- `PATCH /imoveis/{id}`;
- retorno `201 Created`;
- retorno `200 OK`;
- retorno `409 Conflict`;
- retorno `404 Not Found`;
- atualização parcial dos dados do imóvel;
- métodos de domínio para atualização controlada;
- dirty checking do Hibernate na atualização;
- atualização automática de `updatedAt`;
- testes manuais realizados através do Insomnia;
- compilação Maven validada com `BUILD SUCCESS`.

---

## 🛣️ Próximos passos

O fluxo inicial do domínio de imóvel já possui:

```text
cadastro
consulta por ID
listagem
atualização parcial
validação de duplicidade
tratamento de imóvel não encontrado
```

Antes de iniciar os módulos de pagamentos, documentos e demais funcionalidades, ainda serão avaliadas as próximas operações necessárias para concluir o domínio inicial de imóvel.

O desenvolvimento continuará de forma incremental, mantendo a separação entre responsabilidades e evitando avançar para outros domínios antes de finalizar corretamente o fluxo de imóvel.

---

## 👨‍💻 Autor

**Wenderson José da Silva**

Projeto desenvolvido como aplicação prática de estudos em Java, Spring Boot, PostgreSQL e arquitetura de APIs REST.