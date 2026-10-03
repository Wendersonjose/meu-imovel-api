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
- IntelliJ IDEA

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

Recebe as requisições, valida os dados de entrada e devolve as respostas HTTP.

**DTO**

Objetos utilizados para transportar dados entre a API e seus consumidores.

A aplicação utiliza `record` sempre que apropriado.

**Service**

Responsável por coordenar os casos de uso e regras de negócio.

**Repository**

Responsável pelo acesso aos dados utilizando Spring Data JPA.

**JPA / Hibernate**

Responsável pelo mapeamento entre objetos Java e tabelas relacionais.

**PostgreSQL**

Banco de dados utilizado para persistência das informações.

---

## 📂 Estrutura do projeto

```text
src/main/java/com/wenderson/meuimovel
│
├── controller
│   └── ImovelController.java
│
├── domain
│   ├── imovel
│   │   ├── Imovel.java
│   │   ├── ImovelRepository.java
│   │   ├── ImovelService.java
│   │   ├── ValidadorCadastroImovel.java
│   │   ├── ImovelDuplicadoException.java
│   │   ├── DadosCadastroImovel.java
│   │   └── DadosDetalhamentoImovel.java
│   │
│   ├── pagamento
│   ├── documento
│   └── usuario
│
├── infra
│   └── exception
│       └── TratadorDeErros.java
│
└── MeuImovelApiApplication.java
```

A arquitetura principal adotada é:

```text
Controller
    ↓
Service
    ↓
Repository
```

Regras específicas podem ser delegadas a componentes especializados, como o `ValidadorCadastroImovel`.

---

## 🗄️ Banco de dados

O projeto utiliza PostgreSQL hospedado no Supabase.

A conexão da aplicação é realizada através do JDBC Session Pooler.

As credenciais não ficam armazenadas diretamente no código-fonte.

São utilizadas as seguintes variáveis de ambiente:

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

Migrations implementadas até o momento:

```text
V1__create_table_imovel.sql
V2__alter_table_imovel_add_dados_cadastrais.sql
V3__add_unique_constraints_imovel.sql
```

### V1

Criação inicial da tabela `imovel`.

### V2

Inclusão de informações cadastrais do imóvel e adequação dos campos de auditoria para `TIMESTAMPTZ`.

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

Para valores monetários é utilizado:

```java
BigDecimal
```

Para datas sem informação de horário:

```java
LocalDate
```

Para campos de auditoria:

```java
Instant
```

---

## 📦 DTOs

As entidades JPA não são expostas diretamente pela API.

Atualmente existem:

```text
DadosCadastroImovel
DadosDetalhamentoImovel
```

Os DTOs são implementados utilizando Java `record`.

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

O tratamento global da aplicação converte a exceção em:

```http
409 Conflict
```

Exemplo:

```json
{
  "mensagem": "Já existe um imóvel cadastrado com este CNM."
}
```

Além da validação realizada pela aplicação, o PostgreSQL possui constraints `UNIQUE` para proteger a integridade dos dados.

---

## 🌐 Endpoints

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

Em caso de sucesso:

```http
201 Created
```

Em caso de imóvel já cadastrado:

```http
409 Conflict
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

O objetivo é evitar que controllers concentrem lógica de tratamento de exceções e manter respostas HTTP padronizadas.

---

## 🔐 Segurança das configurações

Credenciais, senhas, tokens e outras informações sensíveis não devem ser versionados.

O projeto utiliza variáveis de ambiente para as configurações do banco de dados.

Arquivos locais contendo credenciais devem permanecer protegidos pelo `.gitignore`.

---

## 📐 Decisões de arquitetura

O projeto segue algumas regras:

- DTO de entrada separado de DTO de saída;
- uso de `record` para DTOs quando apropriado;
- entidades JPA não são expostas diretamente na API;
- injeção de dependência por construtor;
- controllers concentram somente responsabilidades HTTP;
- services coordenam regras de negócio;
- repositories concentram persistência;
- validadores podem ser separados em componentes específicos;
- schema controlado pelo Flyway;
- uso de migrations para alterações estruturais;
- credenciais não são armazenadas diretamente no repositório;
- setters públicos indiscriminados são evitados nas entidades.

---

## 📊 Evolução futura

A modelagem está sendo preparada para permitir análises históricas e integração futura com ferramentas como Power BI.

O objetivo é manter movimentações financeiras e eventos históricos separados dos dados cadastrais do imóvel.

---

## ✅ Estado atual

Já implementado:

- projeto Spring Boot;
- Java 17;
- conexão PostgreSQL/Supabase;
- configuração por variáveis de ambiente;
- Flyway;
- migrations do domínio de imóvel;
- entidade `Imovel`;
- DTO de cadastro;
- DTO de detalhamento;
- repository;
- service;
- controller de cadastro;
- validador de duplicidade;
- exceção de domínio para imóvel duplicado;
- tratamento global de erro;
- retorno `409 Conflict` para duplicidades;
- testes manuais através do Insomnia.

---

## 🛣️ Próximo passo

Implementação do endpoint:

```http
GET /imoveis/{id}
```

para consultar o detalhamento de um imóvel pelo identificador.

Os módulos de pagamento, documentos e demais funcionalidades serão desenvolvidos posteriormente, após a conclusão do fluxo inicial de imóvel.

---

## 👨‍💻 Autor

**Wenderson José da Silva**

Projeto desenvolvido como aplicação prática de estudos em Java e Spring Boot.