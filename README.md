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
- [Diagramas](#-diagramas)

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
        │ PostgreSQL  │
        │  (2 DBs)    │
        └─────────────┘
```

---

## 🛠️ Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Cloud 2023.0.0**
  - Spring Cloud Gateway
  - Netflix Eureka (Service Discovery)
- **Spring Security**
- **Spring Data JPA**

### Database
- **PostgreSQL 15**

### Authentication
- **JWT (JSON Web Tokens)**
- **BCrypt** (password hashing)

### DevOps
- **Docker & Docker Compose**
- **Maven**

---

## 🎯 Microserviços

### 1. Eureka Server (Service Discovery)
**Porta:** 8761  
**Função:** Registro e descoberta de serviços

**Features:**
- ✅ Dashboard de monitoramento
- ✅ Health checks automáticos
- ✅ Load balancing
- ✅ Failover handling

**Dashboard:** http://localhost:8761

---

### 2. API Gateway
**Porta:** 8080  
**Função:** Ponto de entrada único para todos os serviços

**Features:**
- ✅ Roteamento inteligente
- ✅ Autenticação JWT
- ✅ CORS configuration
- ✅ Rate limiting
- ✅ Load balancing
- ✅ Circuit breaker (Resilience4j)

**Rotas:**
- `/auth/**` → Auth Service
- `/users/**` → User Service (protegido por JWT)

---

### 3. Auth Service
**Porta:** 8081  
**Função:** Autenticação e gestão de tokens JWT

**Features:**
- ✅ Login com username/password
- ✅ Registro de novos usuários
- ✅ Geração de JWT tokens
- ✅ Validação de tokens
- ✅ Refresh tokens
- ✅ Password hashing (BCrypt)

**Database:** `auth_db` (PostgreSQL)

**Endpoints:**
```
POST /auth/login      - Login
POST /auth/register   - Registro
POST /auth/refresh    - Refresh token
GET  /auth/validate   - Validar token
```

---

### 4. User Service
**Porta:** 8082  
**Função:** Gerenciamento de usuários (CRUD)

**Features:**
- ✅ CRUD completo de usuários
- ✅ Busca por ID, username, email
- ✅ Perfis e permissões
- ✅ Soft delete
- ✅ Auditoria (created_at, updated_at)

**Database:** `user_db` (PostgreSQL)

**Endpoints:**
```
GET    /users          - Listar todos
GET    /users/{id}     - Buscar por ID
POST   /users          - Criar usuário
PUT    /users/{id}     - Atualizar usuário
DELETE /users/{id}     - Deletar usuário
GET    /users/search   - Buscar por critérios
```

---

## 🚀 Como Executar

### Pré-requisitos
- Docker e Docker Compose
- Java 17+ (se for rodar localmente sem Docker)
- Maven 3.8+ (se for rodar localmente)

### Opção 1: Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/tharsis-soares/microservices-gateway-auth.git
cd microservices-gateway-auth

# Suba todos os serviços
docker-compose up -d

# Verifique os logs
docker-compose logs -f

# Para parar
docker-compose down
```

**Tempo de inicialização:** ~2-3 minutos

---

### Opção 2: Execução Local

```bash
# 1. Inicie o PostgreSQL
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# 2. Crie os bancos de dados
docker exec -it postgres psql -U postgres -c "CREATE DATABASE auth_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE user_db;"

# 3. Inicie os serviços na ordem:

# Eureka Server
cd eureka-server
mvn spring-boot:run

# Auth Service (em outro terminal)
cd auth-service
mvn spring-boot:run

# User Service (em outro terminal)
cd user-service
mvn spring-boot:run

# API Gateway (em outro terminal)
cd api-gateway
mvn spring-boot:run
```

---

## 📡 Endpoints

### Eureka Dashboard
```
http://localhost:8761
```

### API Gateway (Ponto de Entrada)
```
http://localhost:8080
```

### Exemplo de Uso

#### 1. Registro de Usuário

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "secret123"
  }'
```

**Resposta:**
```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

---

#### 2. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "secret123"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "username": "johndoe",
  "role": "USER"
}
```

---

#### 3. Acessar User Service (com token)

```bash
TOKEN="seu_token_aqui"

curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN"
```

**Resposta:**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER",
    "createdAt": "2026-01-11T10:30:00"
  }
]
```

---

## ✨ Features

### Segurança
- ✅ **JWT Authentication** - Tokens seguros com expiração
- ✅ **Password Hashing** - BCrypt com salt
- ✅ **CORS Configuration** - Controle de origens
- ✅ **Authorization** - Role-based access control (RBAC)
- ✅ **Input Validation** - Bean Validation

### Resiliência
- ✅ **Service Discovery** - Eureka Server
- ✅ **Load Balancing** - Client-side LB
- ✅ **Health Checks** - Spring Actuator
- ✅ **Circuit Breaker** - Resilience4j (configurável)
- ✅ **Retry Logic** - Configurável

### Observabilidade
- ✅ **Centralized Logging** - SLF4J + Logback
- ✅ **Distributed Tracing** - Spring Cloud Sleuth (opcional)
- ✅ **Health Endpoints** - /actuator/health
- ✅ **Metrics** - Micrometer (opcional)

### DevOps
- ✅ **Containerization** - Docker
- ✅ **Orchestration** - Docker Compose
- ✅ **Configuration** - Externalized config
- ✅ **Multi-stage builds** - Otimização de imagens

---

## 📊 Diagramas

### Fluxo de Autenticação

```
1. Client → API Gateway: POST /auth/login {username, password}
2. API Gateway → Auth Service: Forward request
3. Auth Service → Database: Validate credentials
4. Database → Auth Service: User data
5. Auth Service: Generate JWT token
6. Auth Service → API Gateway: JWT token
7. API Gateway → Client: JWT token
```

### Fluxo de Requisição Protegida

```
1. Client → API Gateway: GET /users (with JWT token)
2. API Gateway: Validate JWT token
3. API Gateway: Extract userId from token
4. API Gateway → User Service: Forward request + X-User-Id header
5. User Service: Process request
6. User Service → API Gateway: Response
7. API Gateway → Client: Response
```

---

## 🔧 Configuração

### Variáveis de Ambiente

**Auth Service:**
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/auth_db
JWT_SECRET=your-secret-key-must-be-at-least-256-bits
JWT_EXPIRATION=86400000  # 24 horas em ms
```

**API Gateway:**
```env
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
JWT_SECRET=your-secret-key-must-be-at-least-256-bits
```

---

## 🧪 Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify

# Cobertura de código
mvn jacoco:report
```

---

## 🚀 Próximos Passos / Roadmap

- [ ] Implementar Refresh Tokens
- [ ] Adicionar Redis para cache de tokens
- [ ] Implementar Rate Limiting
- [ ] Adicionar Kafka para eventos assíncronos
- [ ] Implementar Circuit Breaker pattern
- [ ] Adicionar Swagger/OpenAPI documentation
- [ ] Implementar Distributed Tracing (Zipkin)
- [ ] Adicionar Monitoring (Prometheus + Grafana)
- [ ] CI/CD com GitHub Actions
- [ ] Deploy em Kubernetes

---

## 📚 Referências

- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [JWT.io](https://jwt.io/)
- [Microservices Patterns](https://microservices.io/patterns/index.html)

---

## 🤝 Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

---

## 📝 Licença

Este projeto está sob a licença Apache 2.0 - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👤 Autor

**Tharsis Soares**
- GitHub: [@tharsis-soares](https://github.com/tharsis-soares)
- LinkedIn: [linkedin.com/in/tharsis-soares](https://linkedin.com/in/tharsis-soares)
- Email: tharsissoares@hotmail.com

---

## ⭐ Mostre seu Apoio

Se este projeto te ajudou, considere dar uma ⭐ no repositório!

---

**Desenvolvido com ❤️ usando Spring Boot e Microservices**
# reefsys-gateway
