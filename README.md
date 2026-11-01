# 💰 Smart Budgeting API - Assistente Financeiro com Spring AI

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0--M4-blue.svg)](https://docs.spring.io/spring-ai/reference/index.html)
[![Groq](https://img.shields.io/badge/Groq-Whisper%20%2B%20Llama%203.3-purple.svg)](https://groq.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Projeto desenvolvido como parte do Desafio de Projeto da Trilha **Spring Boot** da **Digital Innovation One (DIO)**. O projeto evolui o módulo base `05-spring-ai`, elevando a aplicação para um nível corporativo completo, fundamentado em **Clean Architecture** e **Domain-Driven Design (DDD)**, com inteligência artificial multimodal, comando de **voz direto em tempo real** e execução de funções de negócio reais (*Tool Calling*).

---

## 1. O que o Projeto Faz

A **Smart Budgeting API** é um assistente financeiro pessoal inteligente que permite ao usuário gerenciar suas finanças (receitas e despesas) falando diretamente pelo microfone ou digitando em uma interface web interativa.

### Fluxo de Execução Inteligente (Voz Direta em Tempo Real):
1. **Fale Diretamente:** O usuário acessa a aplicação web (`http://localhost:8080`), clica no botão de microfone e fala naturalmente (ex.: *"Gastei 45 reais na farmácia comprando remédios"*). Não é necessário gravar ou enviar arquivos de áudio manualmente!
2. **Compreensão e Orquestração (LLM):** O modelo **Llama 3.3 70B** analisa a intenção, identifica se é uma receita ou despesa, infere a categoria adequada e decide qual função da aplicação deve ser acionada.
3. **Execução de Função Real (Tool Calling):** A IA não inventa dados; ela executa casos de uso reais da aplicação (`@Tool`) para persistir, consultar balanços ou realizar estornos no banco de dados MySQL.
4. **Memória de Conversação Persistente no MySQL:** O assistente retém o histórico da sessão gravado em banco de dados (`chat_messages`), permitindo perguntas encadeadas (ex.: *"Quanto me resta de saldo agora?"*).
5. **Resposta em Voz Alta:** O sistema exibe o resultado na tela, atualiza o saldo e as métricas em tempo real, e **fala a resposta em voz alta** em português brasileiro.

---

## 2. Tecnologias Utilizadas

* **Linguagem & Plataforma:** Java 25 (LTS)
* **Framework Principal:** Spring Boot 4
* **Inteligência Artificial:** 
  * **Spring AI (2.0.0-M4):** `ChatClient`, `TranscriptionModel`, `MessageChatMemoryAdvisor` e `@Tool`
  * **Groq Cloud API:** Whisper Large v3 (áudio) e Llama 3.3 70B Versatile (raciocínio e tool calling) — **100% gratuito**
* **Persistência de Dados:** Spring Data JPA & Hibernate
* **Banco de Dados:** MySQL 9 em container Docker
* **Interface & Voz:** Web Speech API nativa (captura de voz sem arquivos e síntese de voz em português)
* **Documentação Interativa:** SpringDoc OpenAPI 3 / Swagger UI
* **Testes Automatizados:** JUnit 5, Mockito e Spring Boot Test
* **Build & Automação:** Gradle 9.4

---

## 3. Melhorias Implementadas em Relação ao Projeto Base

Partindo do projeto inicial do expert Poiani, as seguintes melhorias foram desenhadas e implementadas:

| Aspecto | Projeto Base DIO | Nossa Versão Evoluída ("Outro Nível") |
| :--- | :--- | :--- |
| **Experiência de Voz (UX)** | Gravar arquivo de áudio manual, salvar em disco e subir no Swagger | **Voz Direta em Tempo Real:** Clica no microfone e fala; a IA entende e fala de volta na hora! |
| **Escopo Financeiro** | Apenas despesas/gastos | Suporte completo a **Receitas** (`INCOME`) e **Despesas** (`EXPENSE`) |
| **Categorias** | 3 categorias (`GROCERIES`, `PHARMA`, `AUTO`) | 11 categorias realistas (`ALIMENTACAO`, `SAUDE`, `SALARIO`, `INVESTIMENTOS`, etc.) |
| **Ferramentas de IA (@Tool)** | Apenas 2 (salvar e listar categoria) | **4 Ferramentas:** Registro inteligente, Balanço consolidado (`get-financial-summary`), Listagem com filtros e Estorno/Remoção (`delete-transaction`) |
| **Memória Contextual** | Sem memória entre requisições | **Persistência no MySQL:** Histórico gravado em banco com `JpaChatMemoryRepository` |
| **Interface de Usuário** | Nenhuma (somente endpoints) | **Dashboard Web Completo** com saldo, receitas, despesas e chat com voz |
| **Custo de Execução** | Exigia créditos pagos da OpenAI | **100% Gratuito:** Groq API sem custo + Web Speech nativo |
| **Perfil Standalone/Demo** | Sem fallback offline | Perfil `@Profile("demo")` para rodar sem chaves externas em apresentações |

---

## 4. Como Executar a Aplicação

### Pré-requisitos
* **Java 25** instalado
* **Docker** e **Docker Compose** instalados
* **Chave Gratuita da Groq:** Gere sua chave em [console.groq.com/keys](https://console.groq.com/keys) (gratuito, sem cartão de crédito).

### Passo 1: Subir o banco de dados MySQL
```bash
docker compose up -d
```
> O MySQL será iniciado na porta `3307` com o banco `transaction`.

### Passo 2: Configurar a variável de ambiente
No Linux/macOS:
```bash
export GROQ_API_KEY="gsk_sua_chave_aqui"
```

No Windows (PowerShell):
```powershell
$env:GROQ_API_KEY="gsk_sua_chave_aqui"
```

### Passo 3: Executar a aplicação
No Windows:
```powershell
.\gradlew.bat bootRun
```

No Linux/macOS:
```bash
./gradlew bootRun
```

---

## 5. Como Testar o Fluxo Principal

### Opção A: Pela Interface Web com Voz Direta (Recomendado)
Acesse no seu navegador: **[http://localhost:8080](http://localhost:8080)**

1. Clique no botão circular de **Microfone** (🎙️).
2. Diga: *"Recebi 5000 reais do meu salário hoje."*
3. A IA transcreverá sua fala instantaneamente, chamará a ferramenta de registro e falará em voz alta a confirmação!
4. O saldo e as métricas na tela serão atualizados automaticamente para R$ 5.000,00.
5. Em seguida, clique no microfone e diga: *"Gastei 150 reais no supermercado comprando mantimentos. Quanto me resta de saldo?"*
6. A IA responderá usando o contexto da memória e informará seu novo saldo de R$ 4.850,00!

---

### Opção B: Pelo Swagger UI
Acesse: **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**
* Explore e execute tanto os endpoints REST convencionais (`/api/v1/transactions`) quanto os inteligentes (`/api/v1/assistant/chat`).

---

### Opção C: Testando via cURL (Terminal)

```bash
curl -X POST "http://localhost:8080/api/v1/assistant/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Paguei 200 reais na conta de luz e internet.",
    "conversationId": "minha-sessao-1"
  }'
```

---

### Opção D: Executar os Testes Automatizados
```bash
./gradlew test
```
> Todos os testes unitários de domínio e casos de uso rodam isoladamente, validando as invariantes de negócio sem depender de rede externa.

---

## 6. O que Aprendi Durante o Desafio

* **Arquitetura Limpa com IA:** Como manter o modelo de domínio 100% puro (Java puro), posicionando anotações como `@Tool` estritamente na camada de aplicação (casos de uso).
* **Experiência de Voz Fluida (Voice UX):** Como eliminar o atrito de arquivos de áudio manuais, integrando captura e síntese de voz nativas em tempo real no frontend com backend Spring AI.
* **Persistência de Memória de Chat:** Como desacoplar o armazenamento de contexto da memória volátil (RAM), implementando `ChatMemoryRepository` com Spring Data JPA para persistência durável no MySQL.
* **Tool Calling na Prática:** O LLM atua como um planejador e orquestrador de chamadas, enquanto o código determinístico em Java executa as validações, cálculos e regras de banco.
