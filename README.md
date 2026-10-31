# 💰 Smart Budgeting API - Assistente Financeiro com Spring AI

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0--M4-blue.svg)](https://docs.spring.io/spring-ai/reference/index.html)
[![Groq](https://img.shields.io/badge/Groq-Whisper%20%2B%20Llama%203.3-purple.svg)](https://groq.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Projeto desenvolvido como parte do Desafio de Projeto da Trilha **Spring Boot** da **Digital Innovation One (DIO)**. O projeto evolui o módulo base `05-spring-ai`, elevando a aplicação para um nível corporativo completo, fundamentado em **Clean Architecture** e **Domain-Driven Design (DDD)**, com inteligência artificial multimodal (texto e voz) e execução de funções de negócio reais (*Tool Calling*).

---

## 1. O que o Projeto Faz

A **Smart Budgeting API** é um assistente financeiro pessoal inteligente que permite ao usuário gerenciar suas finanças (receitas e despesas) utilizando linguagem natural por **comandos de voz** (arquivos de áudio) ou **chat em texto**.

### Fluxo de Execução Inteligente:
1. **Entrada de Voz ou Texto:** O usuário envia uma gravação de voz (ex.: *"Gastei 45 reais na farmácia comprando remédios"*) ou digita em um chat conversacional.
2. **Transcrição de Alta Precisão (STT):** O áudio é transcrito em texto pelo modelo **Whisper Large v3**.
3. **Compreensão e Orquestração (LLM):** O modelo **Llama 3.3 70B** analisa a intenção, identifica se é uma receita ou despesa, infere a categoria adequada e decide qual função da aplicação deve ser acionada.
4. **Execução de Função Real (Tool Calling):** A IA não inventa dados; ela executa casos de uso reais da aplicação (`@Tool`) para persistir, consultar balanços ou realizar estornos no banco de dados.
5. **Memória de Conversação:** O assistente retém o histórico da sessão (`conversationId`), permitindo perguntas encadeadas (ex.: *"Quanto me resta de saldo agora?"*).
6. **Resposta Final:** O sistema devolve uma resposta amigável em português com os dados consolidados e arquivo de áudio.

---

## 2. Tecnologias Utilizadas

* **Linguagem & Plataforma:** Java 25 (LTS)
* **Framework Principal:** Spring Boot 4
* **Inteligência Artificial:** 
  * **Spring AI (2.0.0-M4):** `ChatClient`, `TranscriptionModel`, `MessageChatMemoryAdvisor` e `@Tool`
  * **Groq Cloud API:** Whisper Large v3 (áudio) e Llama 3.3 70B Versatile (raciocínio e tool calling) — **100% gratuito**
* **Persistência de Dados:** Spring Data JPA & Hibernate
* **Banco de Dados:** MySQL 9 em container Docker
* **Documentação Interativa:** SpringDoc OpenAPI 3 / Swagger UI
* **Testes Automatizados:** JUnit 5, Mockito e Spring Boot Test
* **Build & Automação:** Gradle 9.4

---

## 3. Melhorias Implementadas em Relação ao Projeto Base

Partindo do projeto inicial do expert Poiani, as seguintes melhorias foram desenhadas e implementadas:

| Aspecto | Projeto Base DIO | Nossa Versão Evoluída ("Outro Nível") |
| :--- | :--- | :--- |
| **Escopo Financeiro** | Apenas despesas/gastos | Suporte completo a **Receitas** (`INCOME`) e **Despesas** (`EXPENSE`) |
| **Categorias** | 3 categorias (`GROCERIES`, `PHARMA`, `AUTO`) | 11 categorias realistas (`ALIMENTACAO`, `SAUDE`, `SALARIO`, `INVESTIMENTOS`, etc.) |
| **Ferramentas de IA (@Tool)** | Apenas 2 (salvar e listar categoria) | **4 Ferramentas:** Registro inteligente, Balanço consolidado (`get-financial-summary`), Listagem com filtros e Estorno/Remoção (`delete-transaction`) |
| **Canais de Interação** | Somente upload de áudio | **Híbrido:** Endpoint de Voz (`/assistant/audio`) e Endpoint de Chat REST (`/assistant/chat`) |
| **Memória Contextual** | Sem memória entre requisições | Suporte a **`ChatMemory`**, mantendo o contexto de conversas anteriores por sessão |
| **Integração Real e Gratuita** | Exigia créditos pagos da OpenAI | Compatível com **Groq** (Whisper + Llama 3), garantindo execução real e custo zero |
| **Tratamento de Erros** | Erros genéricos | **RFC 7807 (`ProblemDetail`)** com validações de domínio claras |
| **Documentação da API** | Apenas via README | **Swagger UI interativo** no navegador para testar requisições em tempo real |

---

## 4. Como Executar a Aplicação

### Pré-requisitos
* **Java 25** instalado
* **Docker** e **Docker Compose** instalados
* **Chave de API Gratuita da Groq:** Crie uma conta gratuita em [console.groq.com/keys](https://console.groq.com/keys) (leva menos de 1 minuto, sem cartão de crédito).

### Passo 1: Clonar o repositório
```bash
git clone https://github.com/SEU_USUARIO/dio-spring-ai-budgeting.git
cd dio-spring-ai-budgeting
```

### Passo 2: Subir o banco de dados MySQL
```bash
docker compose up -d
```
> O MySQL será iniciado na porta `3307` com o banco `transaction`.

### Passo 3: Configurar a variável de ambiente
No Linux/macOS:
```bash
export GROQ_API_KEY="gsk_sua_chave_aqui"
```

No Windows (PowerShell):
```powershell
$env:GROQ_API_KEY="gsk_sua_chave_aqui"
```

### Passo 4: Executar a aplicação
No Linux/macOS:
```bash
./gradlew bootRun
```

No Windows:
```powershell
.\gradlew.bat bootRun
```

A aplicação iniciará na porta `8080`.

---

## 5. Como Testar o Fluxo Principal

### Opção A: Pelo Swagger UI (Navegador)
Acesse: **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
* Explore e execute tanto os endpoints REST convencionais (`/api/v1/transactions`) quanto os inteligentes (`/api/v1/assistant/chat` e `/api/v1/assistant/audio`).

---

### Opção B: Testando via Chat de Texto (`/api/v1/assistant/chat`)

**1. Registrar uma Receita:**
```bash
curl -X POST "http://localhost:8080/api/v1/assistant/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Recebi 4500 reais do meu salário hoje.",
    "conversationId": "minha-sessao-1"
  }'
```

**2. Registrar uma Despesa:**
```bash
curl -X POST "http://localhost:8080/api/v1/assistant/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Paguei 320 reais de conta de luz e internet.",
    "conversationId": "minha-sessao-1"
  }'
```

**3. Perguntar o Saldo (Uso de Memória de Conversação):**
```bash
curl -X POST "http://localhost:8080/api/v1/assistant/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Quanto sobrou do meu saldo e qual foi o total de despesas?",
    "conversationId": "minha-sessao-1"
  }'
```

---

### Opção C: Testando via Áudio de Voz (`/api/v1/assistant/audio`)

O projeto já inclui 6 gravações de áudio reais em `src/test/resources/audio/` para teste imediato:

```bash
curl -X POST "http://localhost:8080/api/v1/assistant/audio" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@src/test/resources/audio/recording-1.m4a" \
  --output resposta.mp3
```

---

### Opção D: Executar os Testes Automatizados
```bash
./gradlew test
```
> Todos os testes unitários de domínio e de casos de uso rodam isoladamente, validando as invariantes de negócio sem depender de serviços externos.

---

## 6. O que Aprendi Durante o Desafio

* **Arquitetura Limpa com IA:** Como manter o modelo de domínio 100% puro (Java puro), posicionando anotações como `@Tool` estritamente na camada de aplicação (casos de uso).
* **Tool Calling na Prática:** O LLM atua como um planejador e orquestrador de chamadas, enquanto o código determinístico em Java executa as validações, cálculos e regras de banco.
* **Memória em Chatbots Corporativos:** Como utilizar `ChatMemory` com `MessageChatMemoryAdvisor` para sustentar conversas contínuas em que a IA se lembra de dados passados pelo usuário.
* **Flexibilidade de Provedores:** Como o ecossistema do Spring AI permite transitar de provedores proprietários para provedores abertos ou de alta velocidade (como o Groq) apenas alterando configurações, mantendo o mesmo padrão de código.
