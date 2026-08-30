# Cortex

Cortex is a Spring Boot application that acts as an **MCP (Model Context Protocol) server** for engineering teams using Claude Code. When a teammate finishes analyzing code in a microservice with Claude Code, the session is summarized and stored in a shared, searchable team knowledge base. Later, anyone (or Claude Code itself) can semantically search that knowledge base to surface prior findings before starting a new investigation.

---

## How it works

1. A teammate uses Claude Code to analyze/debug something in a microservice.
2. Claude Code calls the `save_analysis_summary` MCP tool with the raw summary + metadata (microservice, author).
3. Cortex checks for similar existing entries (semantic similarity via pgvector). If a near-duplicate is found, it's merged via an LLM call instead of creating a duplicate entry.
4. The (possibly merged) summary is embedded and stored in Postgres (`pgvector`).
5. Later, Claude Code (or a teammate) calls `search_knowledge_base` with a natural-language query, and Cortex returns the most relevant prior findings.

```
Claude Code (teammate A)         Claude Code (teammate B)
        │                                  │
        ▼                                  ▼
        ┌──────────────────────────────────┐
        │   Cortex MCP Server (Spring Boot) │
        │  save_analysis_summary / search   │
        └─────────────┬──────────┬──────────┘
                       │          │
              ┌────────┘          └────────┐
              ▼                             ▼
      Anthropic / LM Studio          LM Studio (embeddings)
      (summarization, merge)                │
              │                             │
              └──────────────┬──────────────┘
                              ▼
                  PostgreSQL + pgvector
          (vector_store, session_audit, merge_history)
```

---

## Tech stack

| Component | Choice |
|---|---|
| Framework | Spring Boot 4.1.1, Spring AI 2.0.0, Maven |
| Database | PostgreSQL 14/16 with `pgvector` extension |
| Embeddings | `nomic-embed-text-v1.5` via LM Studio (OpenAI-compatible API) |
| Summarization | Gemma (via LM Studio) |
| MCP transport | `spring-ai-starter-mcp-server-webmvc` (SSE) |
| Retry queue | Apache Kafka (single retry attempt on ingestion failure) |
| Containerization | Docker (multi-stage build) |
| Orchestration | Kubernetes (Postgres, Kafka, and the app as separate Deployments/Services) |

---

## Data model

Three tables, all in the `cortex` database:

- **`vector_store`** — auto-created by Spring AI (`initialize-schema: true`). Holds the current, searchable content: `content`, `metadata` (JSONB: `author`, `microservice`, `classification`), `embedding` (vector).
- **`session_audit`** — an unconditional audit log. One row per submission attempt, including permanently-failed ones, capturing `summary`, `author`, `microservice`, `classification`, `documentId`, `isRetry`, and `status`.
- **`merge_history`** — records every merge event: which old `vector_store` document ID(s) got folded into which new one.

---

## Local setup

### 1. PostgreSQL + pgvector

**macOS (Homebrew):**
```bash
brew install postgresql@14
brew services start postgresql@14

git clone --branch v0.8.0 https://github.com/pgvector/pgvector.git
cd pgvector
export PG_CONFIG=/opt/homebrew/opt/postgresql@14/bin/pg_config
make && make install

createdb cortex
psql -d cortex -c "CREATE EXTENSION IF NOT EXISTS vector;"
```
> **Gotcha:** `brew install pgvector` directly links against whatever the *latest unversioned* `postgresql` formula is, not a specific `@14`/`@16` version. Building from source with `PG_CONFIG` pointed explicitly at your target version avoids the mismatch.

**Or via Docker (simpler, recommended):**
```bash
docker run --name cortex-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=cortex \
  -p 5432:5432 \
  -d pgvector/pgvector:pg16

docker exec -it cortex-postgres psql -U postgres -d cortex -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

### 2. LM Studio

1. Load two models: an embedding model (`nomic-embed-text-v1.5`) and a chat model (Gemma) in LM Studio's Local Server tab.
2. Start the server (default `http://localhost:1234`).
3. Get the **exact** model identifier strings from LM Studio's model info panel — don't guess them from the Hugging Face name (they often include quantization suffixes like `@f32`).
4. Verify:
   ```bash
   curl http://localhost:1234/v1/embeddings \
     -H "Content-Type: application/json" \
     -d '{"model": "<exact-model-id>", "input": "test"}'
   ```

### 3. Kafka

```bash
brew install kafka
brew services start kafka
```
> Homebrew's Kafka scripts have **no `.sh` extension** (e.g. `kafka-topics`, not `kafka-topics.sh`) — differs from the raw Apache tarball distribution.

### 4. `application.yml`

Key sections you'll need to fill in:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/cortex
    username: postgres
    password: postgres
  ai:
    openai:
      base-url: http://localhost:1234      # no trailing /v1 — Spring AI appends it
      api-key: lm-studio
      chat:
        options:
          model: <exact chat model id from LM Studio>
      embedding:
        options:
          model: <exact embedding model id from LM Studio>
    vectorstore:
      pgvector:
        initialize-schema: true
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 768   # must match your embedding model's actual output size
    mcp:
      server:
        name: cortex-kb-server
        version: 1.0.0
        sse-endpoint: /mcp/sse
        type: SYNC
  kafka:
    bootstrap-servers:
      - localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: cortex-kb-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.project.*"
```

### 5. Run it

```bash
mvn clean compile
mvn spring-boot:run
```

---

## Connecting Claude Code

```bash
claude mcp add --transport sse cortex-kb http://localhost:8080/mcp/sse
claude mcp list        # confirm it shows as connected
```

Inside a Claude Code session, run `/mcp` to confirm the tools (`save_analysis_summary`, `search_knowledge_base`) are listed. Try a natural-language prompt like *"Has anyone found similar issues before? Check the knowledge base."* — Claude Code should invoke the tool on its own based on the tool descriptions.

> If you get `404` on the SSE endpoint, double-check `spring-ai-starter-mcp-server-webmvc` (not the plain `spring-ai-starter-mcp-server`, which defaults to stdio transport with no HTTP endpoint at all) and confirm the `sse-endpoint` path in your config matches what's actually registered — check `/actuator/mappings` if unsure.

---

## Dashboard API

```
GET /v1/dashboard/session-audit?page=0&size=20
GET /v1/dashboard/session-audit/microservice/{microservice_name}?page=0&size=20
GET /v1/dashboard/merge-history
```

---

## Kubernetes deployment

Manifests live in the project root / `k8s/` folder: `postgres.yml`, `kafka.yml`, `cortex.yml`, plus a `Dockerfile`.

```bash
# Enable Kubernetes in Docker Desktop first (Settings → Kubernetes)

docker build -t cortex-app:local .

kubectl apply -f postgres.yml
kubectl apply -f kafka.yml
kubectl apply -f cortex.yml

kubectl get pods -w
```

Once Postgres is `Running`:
```bash
kubectl exec -it <postgres-pod-name> -- psql -U postgres -d cortex -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

The app is reachable outside the cluster at `http://localhost:30080` (NodePort). Inside the cluster, the app reaches Postgres/Kafka via their Service DNS names (`postgres:5432`, `kafka:9092`) — no IP addresses, no `localhost`. If LM Studio runs on your host machine (not containerized), the app reaches it via `http://host.docker.internal:1234`.

**Scale down without losing data:**
```bash
kubectl scale deployment cortex --replicas=0
kubectl scale deployment kafka --replicas=0
kubectl scale deployment postgres --replicas=0
```

---

## Known limitations / open items

- Kafka retry is single-attempt only — a second failure is logged to `session_audit` with a `FAILED` status but not retried further.
- LM Studio must stay reachable (currently run on a local machine, not containerized) — a single point of failure for embeddings/summarization.
- `session_audit`/`merge_history` reads are paginated but not currently cached (Redis was evaluated and deliberately skipped — the underlying data changes too frequently for caching to pay off without significant eviction complexity).