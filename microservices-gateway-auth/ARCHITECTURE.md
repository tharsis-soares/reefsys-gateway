# 🏗️ Arquitetura Detalhada

## Visão Geral

Este documento descreve a arquitetura de microserviços implementada neste projeto.

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
        │  ┌─────────────────────────────────────┐  │
        │  │  • Routing                          │  │
        │  │  • JWT Validation                   │  │
        │  │  • CORS Configuration               │  │
        │  │  • Rate Limiting                    │  │
        │  │  • Load Balancing                   │  │
        │  └─────────────────────────────────────┘  │
        └───────────────────────┬───────────────────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
                    ▼                       ▼
        ┌─────────────────────┐ ┌─────────────────────┐
        │   AUTH SERVICE      │ │   USER SERVICE      │
        │   (Port 8081)       │ │   (Port 8082)       │
        │                     │ │                     │
        │  • Login            │ │  • CRUD Users       │
        │  • Register         │ │  • Search           │
        │  • JWT Generation   │ │  • Permissions      │
        │  • Token Validation │ │  • Profiles         │
        └──────────┬──────────┘ └──────────┬──────────┘
                   │                       │
                   └───────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   EUREKA SERVER      │
                    │   (Port 8761)        │
                    │                      │
                    │  • Service Registry  │
                    │  • Health Monitoring │
                    │  • Load Balancing    │
                    └──────────────────────┘
                               │
                               ▼
                ┌──────────────────────────────┐
                │      PostgreSQL Database      │
                │        (Port 5432)            │
                │  ┌──────────┐  ┌──────────┐  │
                │  │ auth_db  │  │ user_db  │  │
                │  └──────────┘  └──────────┘  │
                └──────────────────────────────┘
```

---

## Fluxo de Requisição Completo

### 1. Registro de Novo Usuário

```
[Client] 
   ↓ POST /auth/register
   ↓ { username, email, password }
[API Gateway] 
   ↓ Route: /auth/** → AUTH-SERVICE
   ↓ (sem validação JWT - rota pública)
[Auth Service]
   ↓ Validate input
   ↓ Check if user exists
   ↓ Hash password (BCrypt)
   ↓ Save to database
[PostgreSQL - auth_db]
   ↓ INSERT INTO users
[Auth Service]
   ↓ Return success
[API Gateway]
   ↓ Forward response
[Client]
   ✓ User created
```

### 2. Login e Geração de Token

```
[Client]
   ↓ POST /auth/login
   ↓ { username, password }
[API Gateway]
   ↓ Route: /auth/** → AUTH-SERVICE
[Auth Service]
   ↓ Find user by username
[PostgreSQL - auth_db]
   ↓ SELECT * FROM users WHERE username = ?
[Auth Service]
   ↓ Verify password (BCrypt.check)
   ↓ Generate JWT token
   ↓ {
   ↓   sub: userId,
   ↓   username: username,
   ↓   role: role,
   ↓   exp: timestamp + 24h
   ↓ }
   ↓ Return token
[API Gateway]
   ↓ Forward response
[Client]
   ✓ Receives JWT token
```

### 3. Acesso a Recurso Protegido

```
[Client]
   ↓ GET /users
   ↓ Header: Authorization: Bearer <token>
[API Gateway]
   ↓ JwtAuthenticationFilter
   ↓ Extract token from header
   ↓ Validate token signature
   ↓ Validate token expiration
   ↓ Extract userId from token
   ↓ Add header: X-User-Id: <userId>
   ↓ Route: /users/** → USER-SERVICE
[User Service]
   ↓ Read X-User-Id header
   ↓ Process request
[PostgreSQL - user_db]
   ↓ SELECT * FROM users
[User Service]
   ↓ Return data
[API Gateway]
   ↓ Forward response
[Client]
   ✓ Receives user data
```

---

## Padrões de Arquitetura Implementados

### 1. API Gateway Pattern
**Problema:** Múltiplos microserviços com endpoints diferentes  
**Solução:** Ponto de entrada único que roteia para serviços corretos  
**Benefícios:**
- Simplifica cliente (um único endpoint)
- Centraliza autenticação
- Facilita versionamento
- Permite rate limiting centralizado

### 2. Service Discovery (Registry Pattern)
**Problema:** Serviços em endereços dinâmicos  
**Solução:** Eureka Server mantém registro de todos os serviços  
**Benefícios:**
- Load balancing automático
- Failover
- Serviços se registram automaticamente
- Health checks

### 3. Circuit Breaker (opcional, configurável)
**Problema:** Falha em cascata quando um serviço cai  
**Solução:** Resilience4j detecta e previne chamadas a serviços problemáticos  
**Benefícios:**
- Failover gracioso
- Timeout handling
- Retry logic

### 4. Database per Service
**Problema:** Acoplamento via banco compartilhado  
**Solução:** Cada serviço tem seu próprio banco  
**Benefícios:**
- Isolamento de dados
- Escalabilidade independente
- Tecnologias diferentes por serviço

---

## Decisões de Design

### Por que Spring Cloud Gateway?
- ✅ Reativo (WebFlux) - alta performance
- ✅ Filtros customizáveis
- ✅ Integração nativa com Eureka
- ✅ Configuração declarativa

### Por que JWT?
- ✅ Stateless authentication
- ✅ Sem necessidade de sessão no servidor
- ✅ Escalável horizontalmente
- ✅ Pode incluir claims customizados

### Por que PostgreSQL?
- ✅ ACID compliant
- ✅ Suporte a JSON (futuras expansões)
- ✅ Open source
- ✅ Amplamente usado em produção

---

## Escalabilidade

### Escalabilidade Horizontal

Cada serviço pode ser escalado independentemente:

```
┌─────────────┐
│ API Gateway │
│  (3 inst.)  │
└──────┬──────┘
       │
   ┌───┴───┐
   ▼       ▼
┌────┐   ┌────┐   ┌────┐
│ A1 │   │ A2 │   │ A3 │  Auth Service (3 instâncias)
└────┘   └────┘   └────┘

┌────┐   ┌────┐
│ U1 │   │ U2 │  User Service (2 instâncias)
└────┘   └────┘
```

Eureka faz load balancing automático entre instâncias.

### Pontos de Atenção

1. **Database Bottleneck**
   - Solução: Connection pooling, read replicas

2. **Gateway Single Point of Failure**
   - Solução: Múltiplas instâncias do gateway com load balancer

3. **Eureka Downtime**
   - Solução: Cliente tem cache de serviços registrados

---

## Segurança

### Camadas de Segurança

1. **Gateway Level**
   - CORS configuration
   - Rate limiting
   - JWT validation

2. **Service Level**
   - Input validation
   - SQL injection prevention (JPA)
   - XSS prevention

3. **Database Level**
   - Credenciais separadas por serviço
   - Principle of least privilege

### Melhorias Futuras

- [ ] HTTPS/TLS
- [ ] OAuth2/OpenID Connect
- [ ] API Keys para clientes
- [ ] Audit logging
- [ ] Secrets management (Vault)

---

## Monitoramento e Observabilidade

### Health Checks

```
# Gateway
GET http://localhost:8080/actuator/health

# Auth Service
GET http://localhost:8081/actuator/health

# User Service
GET http://localhost:8082/actuator/health
```

### Métricas (Futuro)

- Prometheus + Grafana
- Request rate
- Error rate
- Latency percentiles
- JVM metrics

### Logging

- Centralized logging (ELK stack)
- Correlation IDs para tracing
- Structured logging (JSON)

---

## Deployment

### Kubernetes (Futuro)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: auth-service
        image: auth-service:1.0.0
        ports:
        - containerPort: 8081
```

### Cloud Providers

- **AWS:** ECS, EKS
- **GCP:** GKE, Cloud Run
- **Azure:** AKS

---

## Referências

- [Microservices Patterns](https://microservices.io)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [The Twelve-Factor App](https://12factor.net)
- [API Gateway Pattern](https://microservices.io/patterns/apigateway.html)
