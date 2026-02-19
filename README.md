# 🚀 Microservices Gateway Auth

[![Java](https://img.shields.io/badge/Java-17+-red)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-blue)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg)](https://opensource.org/licenses/Apache-2.0)

Arquitetura completa de microserviços com **API Gateway**, **Service Discovery (Eureka)**, **Autenticação JWT** e **PostgreSQL**.

---

## 📋 Índice

- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Microserviços](#-microserviços)
- [Como Executar](#-como-executar)
- [Endpoints](#-endpoints)
- [Features](#-features)

---

## 🏗️ Arquitetura

```
┌─────────────────┐
│     Client      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   API Gateway   │ :8080
│   (Routing &    │
│    JWT Filter)  │
└────────┬────────┘
         │
    ┌────┴────┐
    │ Eureka  │ :8761
    │ Server  │
    └────┬────┘
         │
    ┌────┴────────────┐
    │                 │
    ▼                 ▼
┌────────────┐   ┌────────────┐
│   Auth     │   │   User     │
│  Service   │   │  Service   │
│   :8081    │   │   :8082    │
└─────┬──────┘   └─────┬──────┘
      │                │
      └────────┬───────┘
               ▼
        ┌─────────────┐
        │ PostgreSQL  │ :5433
        │  (auth_db)  │
        └─────────────┘
```

> **Nota:** auth-service e user-service compartilham o banco `auth_db` nesta versão.

---

## 🛠️ Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Cloud 2023.0.0**
  - Spring Cloud Gateway
  - Netflix Eureka (Service Discovery)
- **Spring Security**
- **Spring Data JPA / Hibernate 6**
- **Spring Boot Actuator**

### Database
- **PostgreSQL 15 Alpine**

### Authentication
- **JWT (HS512)** com expiração de 24h
- **BCrypt** para hash de senhas

### DevOps
- **Docker & Docker Compose**
- **Maven 3.9**
- **Multi-stage Docker builds**

---

## 🎯 Microserviços

### 1. Eureka Server — Service Discovery
**Porta:** 8761
- Dashboard de monitoramento
- Registro automático de serviços
- Health checks
- **Dashboard:** http://localhost:8761

### 2. API Gateway
**Porta:** 8080
- Roteamento: `/auth/**` → Auth Service, `/users/**` → User Service
- Validação de JWT antes de repassar para os serviços
- CORS configurado

### 3. Auth Service
**Porta:** 8081
- Login e registro de usuários
- Geração de JWT (HS512)
- BCrypt para senhas
- **Endpoints:**
  ```
  POST /auth/register
  POST /auth/login
  ```

### 4. User Service
**Porta:** 8082
- CRUD de usuários (requer JWT válido)
- Auditoria com `createdAt` / `updatedAt`
- **Endpoints:**
  ```
  GET    /users
  GET    /users/{id}
  POST   /users
  PUT    /users/{id}
  DELETE /users/{id}
  ```

---

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados

### 1. Clone o repositório
```bash
git clone https://github.com/tharsis-soares/reefsys-gateway.git
cd reefsys-gateway
```

### 2. Crie o arquivo `.env`
```bash
cat > .env << 'EOF'
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=auth_db
JWT_SECRET=zdtlY9V7X8Pq2M5N6B3C4V5B6N7M8J9K0L1A2S3D4F5G6H7J8K9L0Q1W2E3R4T5Y
TZ=America/Sao_Paulo
EOF
```

### 3. Suba os serviços
```bash
docker compose up -d --build
```

**Tempo de inicialização:** ~3-4 minutos (Maven build incluído)

### 4. Verifique o Eureka Dashboard
http://localhost:8761 — você deve ver 3 serviços: `API-GATEWAY`, `AUTH-SERVICE`, `USER-SERVICE`

---

## 📡 Endpoints

### Registrar usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","email":"john@example.com","password":"secret123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","password":"secret123"}'
```

### Listar usuários (com token)
```bash
TOKEN="seu_token_aqui"
curl http://localhost:8080/users -H "Authorization: Bearer $TOKEN"
```

### Teste completo automatizado
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","password":"secret123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

curl http://localhost:8080/users -H "Authorization: Bearer $TOKEN"
curl http://localhost:8080/users/1 -H "Authorization: Bearer $TOKEN"
```

---

## 📊 Portas dos Serviços

| Serviço | Porta | URL |
|---------|-------|-----|
| API Gateway | 8080 | http://localhost:8080 |
| Eureka Server | 8761 | http://localhost:8761 |
| Auth Service | 8081 | http://localhost:8081 (direto) |
| User Service | 8082 | http://localhost:8082 (direto) |
| PostgreSQL | 5433 | localhost:5433 |

---

## 🔧 Comandos Úteis

```bash
# Ver status dos containers
docker compose ps

# Ver logs de um serviço
docker compose logs -f auth-service

# Reiniciar um serviço
docker compose restart auth-service

# Parar tudo
docker compose down

# Parar e limpar banco de dados
docker compose down -v

# Rebuild completo
docker compose up -d --force-recreate --build
```

---

## ✨ Features Implementadas

- ✅ JWT Authentication (HS512, 24h de validade)
- ✅ Password Hashing (BCrypt)
- ✅ Service Discovery (Eureka)
- ✅ API Gateway com roteamento e validação JWT
- ✅ Health Checks (Spring Actuator + Docker healthcheck)
- ✅ Restart automático (`restart: unless-stopped`)
- ✅ Configuração via `.env` (secrets fora do código)
- ✅ Persistência de dados (volume Docker nomeado)
- ✅ Multi-stage Docker builds (imagens otimizadas)
- ✅ RBAC básico (roles: ADMIN, MANAGER, SUB, USER)

## 🚀 Roadmap

- [ ] Refresh Tokens
- [ ] Redis para cache de tokens invalidados
- [ ] Rate Limiting no Gateway
- [ ] Swagger/OpenAPI documentation
- [ ] CI/CD com GitHub Actions
- [ ] Deploy em Oracle Cloud / Kubernetes
- [ ] Distributed Tracing (Zipkin)
- [ ] Monitoring (Prometheus + Grafana)

---

## 👤 Autor

**Tharsis Soares**
- GitHub: [@tharsis-soares](https://github.com/tharsis-soares)
- LinkedIn: [linkedin.com/in/tharsis-soares](https://linkedin.com/in/tharsis-soares)
- Email: tharsissoares@hotmail.com

---

**Desenvolvido com ❤️ usando Spring Boot e Microservices**