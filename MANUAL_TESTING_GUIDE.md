# 🧪 GUIA DE TESTE MANUAL - APLICAÇÃO BUZZ ONBOARDING

## 📋 **PRÉ-REQUISITOS**

### 1. **Aplicação Rodando**
\`\`\`bash
# Iniciar aplicação
mvn spring-boot:run

# Verificar se está rodando
curl http://localhost:8080/actuator/health
\`\`\`

### 2. **MongoDB Rodando**
\`\`\`bash
# Iniciar MongoDB
docker run -d --name mongodb-buzz -p 27017:27017 mongo:4.4.18

# Verificar se está rodando
docker ps | grep mongodb-buzz
\`\`\`

---

## 🌐 **TESTE 1: CONECTIVIDADE COM API EXTERNA**

### **1.1 Testar DNS e Conectividade**
\`\`\`bash
# Testar DNS
nslookup api.auth.sand.antecipa.com
nslookup api.anticipation.sand.antecipa.com

# Testar conectividade HTTPS
curl -I https://api.auth.sand.antecipa.com/v1/authentication/signin
curl -I https://api.anticipation.sand.antecipa.com/v1
\`\`\`

### **1.2 Testar Autenticação Direta na API**
\`\`\`bash
# Autenticação na API Antecipa
curl -X POST \
  -H "Authorization: Basic MjM2Y2VhOGEtNzBlMS03MGMzLWM1OGQtODRiMTFmNTI3NmNhOjI5NGZmMzAwLTIyMTAtNDU3YS1iODIyLWJkNGM1ZDIwY2ZmZA==" \
  -H "Content-Type: application/json" \
  https://api.auth.sand.antecipa.com/v1/authentication/signin
\`\`\`

**Resposta Esperada (HTTP 200):**
\`\`\`json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "read write"
}
\`\`\`

### **1.3 Testar Endpoint da API com Token**
\`\`\`bash
# Usar o token obtido acima
TOKEN="SEU_TOKEN_AQUI"

# Testar endpoint de fornecedor
curl -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  https://api.anticipation.sand.antecipa.com/v1/Originators/CapitalSources/58408679000132/Suppliers/12345678000195
\`\`\`

---

## 🧪 **TESTE 2: ENDPOINTS DA APLICAÇÃO**

### **2.1 Health Check**
\`\`\`bash
# Teste básico de saúde
curl http://localhost:8080/actuator/health
\`\`\`

**Resposta Esperada (HTTP 200):**
\`\`\`json
{
  "status": "UP",
  "components": {
    "mongo": {
      "status": "UP"
    }
  }
}
\`\`\`

### **2.2 Info Endpoint**
\`\`\`bash
# Informações da aplicação
curl http://localhost:8080/actuator/info
\`\`\`

### **2.3 Endpoint Principal - Onboarding**

#### **Teste com Documento Válido:**
\`\`\`bash
curl -X GET \
  -H "X-Supplier-Document: 12345678000195" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  http://localhost:8080/onboarding
\`\`\`

#### **Teste com Documento Inexistente:**
\`\`\`bash
curl -X GET \
  -H "X-Supplier-Document: 00000000000000" \
  -H "Content-Type: application/json" \
  http://localhost:8080/onboarding
\`\`\`

#### **Teste sem Header (deve dar erro):**
\`\`\`bash
curl -X GET \
  -H "Content-Type: application/json" \
  http://localhost:8080/onboarding
\`\`\`

---

## 📊 **TESTE 3: ANÁLISE DE RESPOSTAS**

### **Códigos HTTP Esperados:**

| Código | Significado | Quando Acontece |
|--------|-------------|-----------------|
| **200** | ✅ Sucesso | Fornecedor encontrado com onboarding |
| **204** | ✅ No Content | Fornecedor não encontrado (normal) |
| **400** | ❌ Bad Request | Header X-Supplier-Document ausente |
| **401** | 🔐 Unauthorized | Credenciais inválidas na API externa |
| **403** | 🚫 Forbidden | Sem permissão na API externa |
| **500** | ❌ Internal Error | Erro interno da aplicação |

### **Exemplos de Respostas:**

#### **HTTP 200 - Sucesso:**
\`\`\`json
{
  "status": "APPROVED"
}
\`\`\`

#### **HTTP 204 - No Content:**
\`\`\`
(corpo vazio)
\`\`\`

#### **HTTP 401 - Erro de Autenticação:**
\`\`\`json
{
  "error": "Unauthorized",
  "message": "Token de autenticação inválido"
}
\`\`\`

---

## 🔍 **TESTE 4: VERIFICAÇÃO DE LOGS**

### **4.1 Logs da Aplicação**
\`\`\`bash
# Ver logs em tempo real
tail -f application.log

# Buscar logs específicos
grep -i "antecipa" application.log
grep -i "auth" application.log
grep -i "error" application.log
\`\`\`

### **4.2 Logs Importantes a Procurar:**

#### **✅ Logs de Sucesso:**
\`\`\`
INFO - Consultando status de onboarding na API Antecipa para documento: 12345678000195
INFO - Token obtido para autenticação
INFO - Status mapeado com sucesso: 'Active' (ID: 1) -> APPROVED
\`\`\`

#### **❌ Logs de Erro:**
\`\`\`
ERROR - Erro ao consultar status de onboarding na API. Status: 401
ERROR - Token de autenticação inválido
ERROR - Falha na comunicação com o serviço de onboarding
\`\`\`

---

## 📱 **TESTE 5: CONFIGURAÇÃO POSTMAN**

### **5.1 Criar Collection no Postman**

#### **Request 1: Health Check**
- **Method:** GET
- **URL:** `http://localhost:8080/actuator/health`
- **Headers:** Nenhum necessário

#### **Request 2: Onboarding - Documento Válido**
- **Method:** GET
- **URL:** `http://localhost:8080/onboarding`
- **Headers:**
  \`\`\`
  X-Supplier-Document: 12345678000195
  Content-Type: application/json
  Accept: application/json
  \`\`\`

#### **Request 3: Onboarding - Documento Inexistente**
- **Method:** GET
- **URL:** `http://localhost:8080/onboarding`
- **Headers:**
  \`\`\`
  X-Supplier-Document: 00000000000000
  Content-Type: application/json
  Accept: application/json
  \`\`\`

#### **Request 4: Onboarding - Sem Header (Erro)**
- **Method:** GET
- **URL:** `http://localhost:8080/onboarding`
- **Headers:**
  \`\`\`
  Content-Type: application/json
  Accept: application/json
  \`\`\`

### **5.2 Testes Automatizados no Postman**

#### **Para Request de Sucesso (HTTP 200):**
\`\`\`javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has status field", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('status');
});

pm.test("Status is valid", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.be.oneOf(['STARTED', 'PENDING', 'APPROVED', 'REJECTED']);
});
\`\`\`

#### **Para Request No Content (HTTP 204):**
\`\`\`javascript
pm.test("Status code is 204", function () {
    pm.response.to.have.status(204);
});

pm.test("Response body is empty", function () {
    pm.expect(pm.response.text()).to.be.empty;
});
\`\`\`

---

## 🗄️ **TESTE 6: VERIFICAÇÃO MONGODB**

### **6.1 Conectar no MongoDB**
\`\`\`bash
# Conectar no container
docker exec -it mongodb-buzz mongo

# Usar database
use buzz_onboarding_test

# Ver collections
show collections

# Ver dados
db.onboarding_status.find().pretty()
\`\`\`

### **6.2 Queries Úteis**
\`\`\`javascript
// Contar documentos
db.onboarding_status.count()

// Buscar por documento específico
db.onboarding_status.find({"supplier_document": "12345678000195"})

// Ver todos os status
db.onboarding_status.distinct("status")

// Ver documentos criados hoje
db.onboarding_status.find({
  "created_at": {
    $gte: new Date(new Date().setHours(0,0,0,0))
  }
})
\`\`\`

---

## 📊 **TESTE 7: MÉTRICAS E PERFORMANCE**

### **7.1 Testar Performance**
\`\`\`bash
# Medir tempo de resposta
time curl -H "X-Supplier-Document: 12345678000195" http://localhost:8080/onboarding

# Teste de carga simples (10 requisições)
for i in {1..10}; do
  curl -w "%{time_total}s\n" -o /dev/null -s \
    -H "X-Supplier-Document: 12345678000195" \
    http://localhost:8080/onboarding
done
\`\`\`

### **7.2 Verificar Recursos**
\`\`\`bash
# Uso de CPU e memória da aplicação
ps aux | grep java

# Conexões de rede ativas
netstat -tln | grep 8080

# Verificar se está fazendo conexões externas
netstat -tn | grep -E "(api\.auth|api\.anticipation)"
\`\`\`

---

## 🎯 **CHECKLIST DE TESTE MANUAL**

### **✅ Conectividade Externa:**
- [ ] DNS resolve para APIs da Antecipa
- [ ] HTTPS conecta nas URLs da API
- [ ] Autenticação retorna token válido
- [ ] Token funciona em endpoints da API

### **✅ Aplicação Local:**
- [ ] Health check retorna HTTP 200
- [ ] Endpoint onboarding é acessível
- [ ] Headers obrigatórios funcionam
- [ ] Respostas estão no formato correto

### **✅ Integração:**
- [ ] Logs mostram chamadas para API externa
- [ ] Autenticação automática funciona
- [ ] Mapeamento de status está correto
- [ ] Erros são tratados adequadamente

### **✅ Persistência:**
- [ ] MongoDB está conectado
- [ ] Dados são salvos corretamente
- [ ] Atualizações funcionam
- [ ] Consultas retornam dados corretos

---

## 🚨 **TROUBLESHOOTING**

### **Problema: HTTP 401 (Unauthorized)**
\`\`\`bash
# Verificar credenciais
grep -i "basic" src/main/resources/application.properties

# Testar autenticação manual
curl -X POST \
  -H "Authorization: Basic MjM2Y2VhOGEtNzBlMS03MGMzLWM1OGQtODRiMTFmNTI3NmNhOjI5NGZmMzAwLTIyMTAtNDU3YS1iODIyLWJkNGM1ZDIwY2ZmZA==" \
  https://api.auth.sand.antecipa.com/v1/authentication/signin
\`\`\`

### **Problema: HTTP 500 (Internal Error)**
\`\`\`bash
# Verificar logs detalhados
tail -50 application.log | grep ERROR

# Verificar se MongoDB está rodando
docker ps | grep mongodb-buzz

# Testar conexão MongoDB
docker exec mongodb-buzz mongo --eval "db.runCommand('ping')"
\`\`\`

### **Problema: Timeout/Sem Resposta**
\`\`\`bash
# Verificar se aplicação está rodando
ps aux | grep java

# Verificar porta
netstat -tln | grep 8080

# Testar conectividade local
curl -I http://localhost:8080/actuator/health
\`\`\`

---

## 📝 **DOCUMENTAÇÃO PARA EQUIPE**

### **Endpoints Disponíveis:**

| Endpoint | Method | Headers | Descrição |
|----------|--------|---------|-----------|
| `/actuator/health` | GET | - | Health check da aplicação |
| `/actuator/info` | GET | - | Informações da aplicação |
| `/onboarding` | GET | `X-Supplier-Document` | Consulta status de onboarding |

### **Códigos de Resposta:**

| Código | Status | Descrição |
|--------|--------|-----------|
| 200 | OK | Onboarding encontrado |
| 204 | No Content | Fornecedor não encontrado |
| 400 | Bad Request | Header obrigatório ausente |
| 401 | Unauthorized | Erro de autenticação |
| 500 | Internal Error | Erro interno |

### **Exemplos de Uso:**

#### **cURL:**
\`\`\`bash
curl -H "X-Supplier-Document: 12345678000195" http://localhost:8080/onboarding
\`\`\`

#### **JavaScript/Fetch:**
\`\`\`javascript
fetch('http://localhost:8080/onboarding', {
  headers: {
    'X-Supplier-Document': '12345678000195',
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
\`\`\`

#### **Python/Requests:**
```python
import requests

headers = {
    'X-Supplier-Document': '12345678000195',
    'Content-Type': 'application/json'
}

response = requests.get('http://localhost:8080/onboarding', headers=headers)
print(response.json())
