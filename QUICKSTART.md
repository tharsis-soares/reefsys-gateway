# 🚀 Quick Start Guide

## Início Rápido (5 minutos)

### 1. Clone o Repositório
```bash
git clone https://github.com/tharsis-soares/microservices-gateway-auth.git
cd microservices-gateway-auth
```

### 2. Suba os Serviços
```bash
docker-compose up -d
```

### 3. Aguarde os Serviços Iniciarem (~2 minutos)
```bash
docker-compose logs -f
```

Aguarde até ver:
- ✅ Eureka Server iniciado (porta 8761)
- ✅ Auth Service registrado no Eureka
- ✅ User Service registrado no Eureka  
- ✅ API Gateway iniciado (porta 8080)

### 4. Verifique o Eureka Dashboard
Abra no navegador: http://localhost:8761

Você deve ver 3 serviços registrados:
- API-GATEWAY
- AUTH-SERVICE
- USER-SERVICE

### 5. Teste a API

#### Registrar um usuário:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "pass123"
  }'
```

#### Fazer login:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "pass123"
  }'
```

Guarde o token retornado!

#### Acessar Users Service (com token):
```bash
TOKEN="seu_token_aqui"

curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN"
```

---

## Portas dos Serviços

| Serviço | Porta | URL |
|---------|-------|-----|
| API Gateway | 8080 | http://localhost:8080 |
| Eureka Server | 8761 | http://localhost:8761 |
| Auth Service | 8081 | http://localhost:8081 (direto) |
| User Service | 8082 | http://localhost:8082 (direto) |
| PostgreSQL | 5432 | localhost:5432 |

---

## Comandos Úteis

```bash
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f api-gateway

# Reiniciar um serviço
docker-compose restart auth-service

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (limpa banco)
docker-compose down -v

# Rebuild dos serviços
docker-compose up -d --build
```

---

## Testando com Postman

1. Importe o arquivo `postman_collection.json` no Postman
2. Configure a variável `baseUrl` para `http://localhost:8080`
3. Execute a request "Register" para criar um usuário
4. Execute a request "Login" (o token será salvo automaticamente)
5. Execute as requests de "Users" (já autenticadas)

---

## Troubleshooting

### Serviços não sobem
```bash
# Verifique se as portas estão livres
netstat -an | grep "8080\|8081\|8082\|8761\|5432"

# Limpe e reinicie
docker-compose down -v
docker-compose up -d --build
```

### Eureka não mostra os serviços
Aguarde 30-60 segundos. Os serviços levam tempo para se registrar.

### Auth Service não conecta ao banco
```bash
# Verifique se o PostgreSQL está rodando
docker-compose ps postgres

# Veja os logs do banco
docker-compose logs postgres
```

---

## Próximos Passos

- 📖 Leia o [README.md](README.md) completo
- 🔧 Customize as configurações em `application.yml`
- 🚀 Deploy em cloud (AWS, GCP, Azure)
- 📊 Adicione monitoring (Prometheus + Grafana)
- 🧪 Escreva testes de integração

---

**Problemas?** Abra uma issue no GitHub!
