# 🏗️ Arquitetura Detalhada

## Visão Geral

Arquitetura de microserviços com API Gateway, Service Discovery (Eureka), autenticação JWT e PostgreSQL. Todos os serviços rodam em Docker com healthchecks e restart automático.

---

## Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                         INTERNET / CLIENT                        │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
        ┌───────────────────────────────────────────┐
        │          API GATEWAY (Port 8080)          │
        │  • Routing                                │
        │  • JWT Validation                         │
        │  • CORS Configuration                     │
        └───────────────────────┬───────────────────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
                    ▼                       ▼
        ┌─────────────────────┐ ┌─────────────────────┐
        │   AUTH SERVICE      │ │   USER SERVICE      │
        │   (Port 8081)       │ │   (Port 8082)       │
        │  • Login            │ │  • CRUD Users       │
        │  • Register         │ │  • JWT validation   │
        │  • JWT Generation   │ │  • RBAC             │
        └──────────┬──────────┘ └──────────┬──────────┘
                   │                       │
                   └───────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   EUREKA SERVER      │
                    │   (Port 8761)        │
                    │  • Service Registry  │
                    │  • Health Monitoring │
                    └──────────────────────┘
                               │
                               ▼
                ┌──────────────────────────────┐
                │   PostgreSQL 15 (Port 5433)  │
                │         auth_db              │
                │  (auth-service + user-service│
                │   compartilham o mesmo banco)│
                └──────────────────────────────┘
```

---

## Fluxo de Autenticação

### Registro
```
Client → POST /auth/register → API Gateway → Auth Service
Auth Service: valida input → hash BCrypt → salva no banco
Auth Service → retorna JWT token
```

### Login
```
Client → POST /auth/login → API Gateway → Auth Service
Auth Service: busca usuário → verifica BCrypt → gera JWT (HS512, 24h)
Auth Service → retorna { token, userId, username, role }
```

### Acesso a recurso protegido
```
Client → GET /users (Authorization: Bearer <token>)
→ API Gateway: extrai token → valida assinatura HS512 → valida expiração
→ User Service: recebe request autenticado → processa → retorna dados
```

---

## Estrutura de Diretórios

```
microservices-gateway-auth/
├── docker-compose.yml          # Orquestração dos serviços
├── .env                        # Secrets (não commitado)
├── .gitignore
├── pom.xml                     # POM raiz (parent)
├── scripts/
│   └── init-databases.sql      # Script de inicialização do banco
├── eureka-server/
│   ├── Dockerfile
│   └── src/
├── auth-service/
│   ├── Dockerfile              # Inclui curl para healthcheck
│   └── src/
│       └── main/java/com/tharsis/auth/
│           ├── config/SecurityConfig.java   # /auth/**, /actuator/** público
│           └── security/JwtUtil.java
├── user-service/
│   ├── Dockerfile              # Inclui curl para healthcheck
│   └── src/
│       └── main/java/com/tharsis/user/
│           ├── config/SecurityConfig.java
│           └── security/
│               ├── JwtUtil.java
│               └── JwtAuthFilter.java
└── api-gateway/
    ├── Dockerfile
    └── src/
```

---

## Configuração de Healthchecks

Todos os serviços têm healthcheck configurado:

| Serviço | Endpoint | Start Period |
|---------|----------|-------------|
| postgres | `pg_isready` | — |
| eureka-server | `curl /actuator/health` | 30s |
| auth-service | `curl /actuator/health` | 60s |
| user-service | `curl /actuator/health` | 60s |
| api-gateway | `curl /actuator/health` | 60s |

O `api-gateway` só sobe após auth-service e user-service estarem **healthy**.

---

## Segurança

### Rotas públicas (sem JWT)
- `POST /auth/register`
- `POST /auth/login`
- `GET /actuator/health` (todos os serviços)

### Rotas protegidas (requer JWT válido)
- `GET /users`
- `GET /users/{id}`
- `POST /users`
- `PUT /users/{id}`
- `DELETE /users/{id}`

### JWT
- Algoritmo: **HS512**
- Expiração: **24 horas**
- Claims: `sub` (userId), `username`, `role`
- Secret: configurado via variável de ambiente `JWT_SECRET`

### RBAC — Roles disponíveis
- `ROLE_ADMIN`
- `ROLE_MANAGER`
- `ROLE_SUB`
- `ROLE_USER` / `USER`

---

## Decisões de Design

### Por que um banco compartilhado?
Auth-service e user-service compartilham o `auth_db` nesta versão para simplificar o setup inicial. O ideal para produção em escala seria um banco por serviço.

### Por que Spring Cloud Gateway?
Reativo (WebFlux), integração nativa com Eureka, filtros customizáveis para JWT.

### Por que JWT stateless?
Sem necessidade de sessão no servidor, escalável horizontalmente, claims customizados (role, username).

---

## Melhorias Futuras

- [ ] Banco de dados separado por serviço
- [ ] Refresh Tokens
- [ ] Redis para invalidação de tokens
- [ ] Rate Limiting no Gateway
- [ ] HTTPS/TLS
- [ ] Distributed Tracing (Zipkin)
- [ ] Monitoring (Prometheus + Grafana)
- [ ] CI/CD com GitHub Actions
- [ ] Deploy em Kubernetes