# Guia de Testes Manuais - API Onboarding

## Configuração Inicial

### 1. Iniciar a Aplicação
\`\`\`bash
mvn spring-boot:run
\`\`\`
A aplicação rodará na porta **8080** (padrão do Spring Boot).

### 2. Verificar se a Aplicação Está Rodando
\`\`\`
GET http://localhost:8080/actuator/health
\`\`\`
**Resposta esperada:** Status 200 com informações de saúde da aplicação.

## Endpoints Disponíveis

### 1. Consultar Status de Onboarding (Novo Endpoint)

#### Opção A: Usando Header
\`\`\`
GET http://localhost:8080/onboarding
Headers:
  X-Supplier-Document: 12345678000195
  Content-Type: application/json
\`\`\`

#### Opção B: Usando Query Parameter
\`\`\`
GET http://localhost:8080/onboarding?supplierDocument=12345678000195
\`\`\`

**Respostas Possíveis:**
- **204 No Content**: Não há status de onboarding para o fornecedor
- **200 OK**: Status encontrado
  \`\`\`json
  {
    "status": "PENDING"
  }
  \`\`\`

### 2. Consultar Status por Documento (Endpoint Legado)
\`\`\`
GET http://localhost:8080/onboarding/12345678000195
\`\`\`

### 3. Monitoramento

#### Circuit Breakers
\`\`\`
GET http://localhost:8080/actuator/circuitbreakers
\`\`\`

#### Métricas
\`\`\`
GET http://localhost:8080/actuator/metrics
\`\`\`

## Cenários de Teste

### Cenário 1: Primeiro Acesso (Sem Dados no Banco)
1. Chame o endpoint GET /onboarding com um documento novo
2. **Resultado esperado**: 
   - Consulta na API externa da Antecipa
   - Salva o status no banco local
   - Retorna 200 OK com o status ou 204 se não encontrado

### Cenário 2: Dados Já Existem no Banco
1. Chame o endpoint novamente com o mesmo documento
2. **Resultado esperado**:
   - Consulta primeiro no banco local
   - Consulta na API externa para atualizar
   - Atualiza o banco se necessário
   - Retorna 200 OK com o status atualizado

### Cenário 3: API Externa Indisponível
1. Desconecte a internet ou configure um proxy para bloquear a API
2. Chame o endpoint
3. **Resultado esperado**:
   - Retorna dados do banco local se existirem
   - Ou retorna 204 se não houver dados locais

## Documentos de Teste Sugeridos

- **12345678000195** - CNPJ válido para testes
- **11222333000181** - Outro CNPJ válido
- **00000000000000** - CNPJ inválido para testar erro

## Status Possíveis

- `STARTED` - Processo iniciado
- `REVIEW` - Em análise
- `PENDING` - Pendente
- `APPROVED` - Aprovado
- `REJECTED` - Rejeitado

## Logs para Acompanhar

A aplicação está configurada com logs DEBUG para:
- `br.com.experian.buzz.infrastructure.integration.feign`
- `br.com.experian.buzz.infrastructure.repository.adapter`
- `feign`

Acompanhe os logs no console para ver as chamadas sendo feitas.
