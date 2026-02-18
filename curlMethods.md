# 1. Verificar Eureka e serviços registrados
curl http://localhost:8761/eureka/apps -H "Accept: application/json" | jq

# 2. Verificar Gateway
curl -i http://localhost:8080

# 3. Listar actuators do Gateway (se habilitado)
curl http://localhost:8080/actuator | jq





# Tentar registro pelo Gateway
curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"Test123Pass"}'

# Testar rotas alternativas do Gateway
curl -i -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"Test123Pass"}'

curl -i -X POST http://localhost:8080/AUTH-SERVICE/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"Test123Pass"}'

# Tentar acesso DIRETO ao Auth Service (portas comuns)
curl -i -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"directuser","email":"direct@example.com","password":"Direct123"}'

curl -i -X POST http://localhost:8082/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"directuser","email":"direct@example.com","password":"Direct123"}'

curl -i -X POST http://localhost:9001/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"directuser","email":"direct@example.com","password":"Direct123"}'




  # Login direto (ajuste a porta conforme necessário)
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directuser","password":"Direct123"}' | jq

# Salvar token em variável
TOKEN=$(curl -s -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"directuser","password":"Direct123"}' | jq -r '.token')

# Verificar token
echo $TOKEN

# Testar User Service com token (direto)
curl -i http://localhost:8084/users \
  -H "Authorization: Bearer $TOKEN"

# Testar User Service com token (pelo Gateway)
curl -i http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN"


  #!/bin/bash

echo "=== DIAGNÓSTICO DE MICROSERVIÇOS ==="
echo ""

echo "1. Verificando Eureka..."
curl -s http://localhost:8761/eureka/apps -H "Accept: application/json" | jq '.applications.application[].instance[] | {app: .app, status: .status, port: .port."$"}'
echo ""

echo "2. Verificando Gateway..."
curl -i -s http://localhost:8080 | head -n 1
echo ""

echo "3. Testando Auth Service (porta 8081)..."
curl -i -s -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test1","email":"test1@example.com","password":"Test123"}' | head -n 1

echo "4. Testando Auth Service (porta 8082)..."
curl -i -s -X POST http://localhost:8082/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test2","email":"test2@example.com","password":"Test123"}' | head -n 1

echo "5. Testando Auth Service pelo Gateway..."
curl -i -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test3","email":"test3@example.com","password":"Test123"}' | head -n 1

echo ""
echo "=== FIM DO DIAGNÓSTICO ==="




# Ver todos os serviços no Eureka
curl -s http://localhost:8761/eureka/apps -H "Accept: application/json" | jq -r '.applications.application[].instance[] | "\(.app) - \(.hostName):\(.port."$") - \(.status)"'

# Testar todas as portas comuns para Auth Service
for port in 8081 8082 8083 9001 9002; do
  echo "Testando porta $port..."
  curl -i -s -X POST http://localhost:$port/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username":"test","email":"test@example.com","password":"Test123"}' | head -n 1
done




# PASSO 1: Ver serviços registrados
curl -s http://localhost:8761/eureka/apps -H "Accept: application/json" | jq

# PASSO 2: Identificar porta do AUTH-SERVICE
# (olhe no output acima)

# PASSO 3: Registrar usuário (ajuste a porta)
curl -X POST http://localhost:PORTA_AUTH/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","email":"john@example.com","password":"Secret123Pass"}' | jq

# PASSO 4: Fazer login e salvar token
TOKEN=$(curl -s -X POST http://localhost:PORTA_AUTH/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","password":"Secret123Pass"}' | jq -r '.token')

# PASSO 5: Testar endpoint protegido
curl http://localhost:PORTA_USER/users \
  -H "Authorization: Bearer $TOKEN" | jq













###

# ============================================
# SERVIÇOS DESCOBERTOS:
# - API-GATEWAY: porta 8080
# - AUTH-SERVICE: porta 8081 (IP: 172.20.0.5)
# - USER-SERVICE: porta 8082 (IP: 172.20.0.4)
# ============================================

# 1️⃣ REGISTRAR USUÁRIO (pelo Gateway)
curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "Secret123Pass"
  }'

# 2️⃣ FAZER LOGIN (pelo Gateway)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "Secret123Pass"
  }' | jq

# 3️⃣ SALVAR TOKEN EM VARIÁVEL
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "Secret123Pass"
  }' | jq -r '.token')

# Verificar token
echo "Token: $TOKEN"

# 4️⃣ ACESSAR USER SERVICE (pelo Gateway com token)
curl -i http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN"

# 5️⃣ BUSCAR USUÁRIO ESPECÍFICO
curl http://localhost:8080/users/1 \
  -H "Authorization: Bearer $TOKEN" | jq
  ###