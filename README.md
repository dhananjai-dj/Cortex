# 🧠 Cortex

**A shared, searchable memory for your team's Claude Code sessions.**

Cortex is a Spring Boot MCP server that plugs into [Claude Code](https://claude.com/claude-code). When someone on your team finishes analyzing or debugging code in a microservice, Cortex summarizes the session and stores it in a team-wide knowledge base. Next time someone hits something similar — in the same repo or a different one — Claude Code can search that knowledge base and surface the prior finding automatically.

No more re-solving the same bug in three different microservices because nobody knew someone already figured it out.

---

## ✨ Features

- **Two MCP tools** exposed to Claude Code: `save_analysis_summary` and `search_knowledge_base`
- **Semantic search** over past findings via pgvector — not just keyword matching
- **Automatic deduplication & merging** — near-duplicate findings get combined via LLM rather than cluttering the knowledge base
- **Full audit trail** — every submission is logged, even ones that later get merged or fail
- **Retry safety net** — failed ingestions get one automatic retry via Kafka before being marked as a permanent failure
- **Runs entirely on your infra** — Postgres, Kafka, and a local LLM via LM Studio; nothing has to leave your network except summarization/embedding calls to whatever model you configure
- **Dashboard API** for browsing audit history and merge history, paginated

---

## 🏗️ How it fits together

```
Claude Code  ──MCP (SSE)──►  Cortex (Spring Boot)
                                    │
                    ┌───────────────┼────────────────┐
                    ▼               ▼                ▼
              LM Studio        PostgreSQL          Kafka
          (embeddings +        + pgvector        (retry queue)
           summarization)
```

---

## 🚀 Quick start

```bash
# 1. Postgres + pgvector
docker run --name cortex-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=cortex -p 5432:5432 -d pgvector/pgvector:pg16
docker exec -it cortex-postgres psql -U postgres -d cortex -c "CREATE EXTENSION IF NOT EXISTS vector;"

# 2. Kafka
brew install kafka && brew services start kafka

# 3. LM Studio — load an embedding model + a chat model, start the local server

# 4. Configure application.yml (datasource, LM Studio base-url + model names, MCP sse-endpoint)

# 5. Run
mvn clean compile
mvn spring-boot:run

# 6. Connect Claude Code
claude mcp add --transport sse cortex-kb http://localhost:8080/mcp/sse
```

Full setup instructions, config reference, and troubleshooting notes: **[Help.md](./Help.md)**

---

## 🐳 Docker / Kubernetes

A `Dockerfile` and Kubernetes manifests (`postgres.yml`, `kafka.yml`, `cortex.yml`) are included for running the whole stack in a cluster. See [Help.md](./Help.md#kubernetes-deployment) for deployment steps.

---

## 📊 Tech stack

Spring Boot 4.1.1 · Spring AI 2.0.0 · PostgreSQL + pgvector · Apache Kafka · LM Studio (Gemma + nomic-embed-text) · Docker · Kubernetes

---
