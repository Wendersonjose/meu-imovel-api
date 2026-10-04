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

Esse componente concentra as regras de validação de duplicidade durante o cadastro.

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

---

## 📦 DTOs

As entidades JPA não são expostas diretamente pela API.

Atualmente existem:

```text
DadosCadastroImovel
DadosDetalhamentoImovel
```

Os DTOs são implementados utilizando Java `record`.

O fluxo de saída da API segue:

```text
Imovel
    ↓
DadosDetalhamentoImovel
    ↓
JSON
```

---

## ✅ Validação de duplicidade

Antes de persistir um imóvel, o sistema executa validações através do componente:

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

## 🔎 Imóvel não encontrado

Ao consultar um imóvel por ID, o service utiliza:

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

---

### Cadastrar imóvel

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

Em caso de duplicidade:

```http
409 Conflict
```

---

### Listar imóveis

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

### Detalhar imóvel por ID

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

---

## 📐 Decisões de arquitetura

O projeto segue as seguintes regras:

- DTO de entrada separado de DTO de saída;
- uso de `record` para DTOs quando apropriado;
- entidades JPA não são expostas diretamente pela API;
- Lombok utilizado apenas para reduzir boilerplate controlado;
- setters públicos indiscriminados são evitados;
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
- DTO de cadastro;
- DTO de detalhamento;
- `ImovelRepository`;
- `ImovelService`;
- `ImovelController`;
- `ValidadorCadastroImovel`;
- validação de duplicidade;
- `ImovelDuplicadoException`;
- `ImovelNaoEncontradoException`;
- `TratadorDeErros`;
- `POST /imoveis`;
- `GET /imoveis`;
- `GET /imoveis/{id}`;
- retorno `201 Created`;
- retorno `200 OK`;
- retorno `409 Conflict`;
- retorno `404 Not Found`;
- testes manuais realizados através do Insomnia;
- compilação Maven validada com `BUILD SUCCESS`.

---

## 🛣️ Próximos passos

O próximo passo do domínio de imóvel será implementar a atualização dos dados do imóvel.

A ideia é adicionar um fluxo semelhante a:

```http
PATCH /imoveis/{id}
```

com DTO específico de atualização e métodos de domínio para alterar apenas os campos permitidos.

Depois disso, ainda poderemos avaliar listagem paginada e outras operações antes de iniciar os módulos de pagamentos, documentos e demais funcionalidades.

---

## 👨‍💻 Autor

**Wenderson José da Silva**

Projeto desenvolvido como aplicação prática de estudos em Java, Spring Boot, PostgreSQL e arquitetura de APIs REST.