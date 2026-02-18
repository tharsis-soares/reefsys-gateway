#!/bin/bash

echo "🎯 Testando Microserviços"
echo "=========================="
echo ""

# Cores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Registrar usuário
echo -e "${YELLOW}1. Registrando usuário...${NC}"
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123Pass"
  }')

if echo "$REGISTER_RESPONSE" | jq -e '.token' > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Registro bem-sucedido${NC}"
else
    echo -e "${RED}✗ Falha no registro${NC}"
    echo "$REGISTER_RESPONSE" | jq
fi
echo ""

# 2. Login
echo -e "${YELLOW}2. Fazendo login...${NC}"
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123Pass"
  }' | jq -r '.token')

if [ ! -z "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo -e "${GREEN}✓ Login bem-sucedido${NC}"
    echo "Token: ${TOKEN:0:50}..."
else
    echo -e "${RED}✗ Falha no login${NC}"
    exit 1
fi
echo ""

# 3. Acessar User Service
echo -e "${YELLOW}3. Acessando User Service...${NC}"
USERS_RESPONSE=$(curl -s -w "\n%{http_code}" http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN")

HTTP_CODE=$(echo "$USERS_RESPONSE" | tail -n1)
BODY=$(echo "$USERS_RESPONSE" | sed '$d')

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✓ Acesso autorizado (HTTP $HTTP_CODE)${NC}"
    echo "$BODY" | jq
else
    echo -e "${RED}✗ Falha no acesso (HTTP $HTTP_CODE)${NC}"
    echo "$BODY"
fi
echo ""

# 4. Health Checks
echo -e "${YELLOW}4. Verificando saúde dos serviços...${NC}"

check_health() {
    local service=$1
    local url=$2
    local status=$(curl -s -o /dev/null -w "%{http_code}" "$url")

    if [ "$status" = "200" ]; then
        echo -e "${GREEN}✓ $service: UP${NC}"
    else
        echo -e "${RED}✗ $service: DOWN (HTTP $status)${NC}"
    fi
}

check_health "Gateway" "http://localhost:8080/actuator/health"
check_health "Auth Service" "http://localhost:8081/actuator/health"
check_health "User Service" "http://localhost:8082/actuator/health"

echo ""
echo "=========================="
echo -e "${GREEN}🎉 Teste concluído!${NC}"