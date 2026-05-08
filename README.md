# API Addresses | Checkpoint 5

API REST em Java para cálculo da melhor rota entre uma origem e múltiplos destinos, com integração a APIs externas e persistência em banco Oracle.

## 1. Objetivo do Projeto

Desenvolver uma aplicação capaz de:

- Consumir APIs externas em Java
- Tratar e organizar dados para apresentação ao usuário
- Expor endpoints REST funcionais
- Persistir e consultar dados em banco de dados

O cenário implementado foi de roteirização de endereços: a API recebe origem e destinos, consulta o ViaCEP para normalizar endereços e usa o Google Routes Distance Matrix para identificar a menor distância.

## 2. Requisitos da Matéria Atendidos

- Acesso a API externa: ViaCEP e Google Routes
- Consumo e tratamento de dados: normalização de endereço e seleção da menor rota
- Implementação de endpoints: 5 endpoints REST
- Persistência em banco: Oracle com Hibernate ORM + Panache
- Organização em camadas: Controller -> Service -> Repository
- Separação de responsabilidades: clientes HTTP, serviços de domínio e camada de acesso a dados
- Funcionamento correto do fluxo principal: cálculo, retorno e persistência da melhor rota

## 3. Arquitetura e Stack

### Tecnologias

- Java 21
- Quarkus 3.34.6
- Quarkus REST + Jackson
- Quarkus REST Client + Jackson
- Hibernate ORM + Panache
- Oracle JDBC
- OpenAPI/Swagger

### Camadas

- Controller: recebe requisições e define respostas HTTP
- Service: validações, regras de negócio e orquestração das integrações
- Repository: persistência e consulta das rotas salvas
- Client: integração com ViaCEP e Google Routes

## 4. Fluxo Funcional

1. Valida CEP de origem com regex de 8 dígitos.
2. Consulta ViaCEP para montar endereço de origem e destinos no formato textual.
3. Monta request para Google Distance Matrix v2.
4. Filtra apenas rotas com condição ROUTE_EXISTS.
5. Seleciona a rota com menor distanceMeters.
6. Converte metros para quilômetros formatados (ex: 1.50km).
7. Retorna resposta final e, opcionalmente, persiste no Oracle.

## 5. Endpoints

Base path: /endereco

### 5.1 Buscar endereço por CEP

- Método: GET
- Rota: /endereco/{cep}
- Descrição: consulta dados do CEP no ViaCEP.

Exemplo:

```bash
curl -X GET "http://localhost:8080/endereco/01310100"
```

Resposta 200 (exemplo):

```json
{
	"cep": "01310-100",
	"logradouro": "Avenida Paulista",
	"numero": "",
	"bairro": "Bela Vista",
	"cidade": "São Paulo",
	"uf": "SP",
	"estado": "São Paulo",
	"regiao": "Sudeste",
	"ddd": "11",
	"erro": null
}
```

### 5.2 Preparar endereço para consulta de rota

- Método: GET
- Rota: /endereco/{cep}/enderecoPreparado?numero={numero}
- Descrição: retorna endereço formatado para uso na API do Google.

Exemplo:

```bash
curl -X GET "http://localhost:8080/endereco/01310100/enderecoPreparado?numero=1000"
```

Resposta 200 (text/plain):

```text
Avenida Paulista, 1000, São Paulo, SP
```

Resposta 400 (text/plain):

```text
Endereço inválido
```

### 5.3 Calcular menor rota

- Método: POST
- Rota: /endereco/calculaRotas
- Descrição: calcula a melhor rota sem salvar no banco.

Body de entrada:

```json
{
	"cepOrigem": "01310100",
	"numeroOrigem": "1000",
	"destinos": [
		{
			"cep": "01311100",
			"numero": "500"
		},
		{
			"cep": "01310200",
			"numero": "2000"
		}
	]
}
```

Resposta 200:

```json
{
	"idRota": null,
	"cepOrigem": "01310100",
	"enderecoOrigem": "Avenida Paulista, 1000, São Paulo, SP",
	"cepDestino": "01311100",
	"enderecoDestino": "Avenida Paulista, 500, São Paulo, SP",
	"distancia": "1.50km"
}
```

### 5.4 Calcular e salvar rota

- Método: POST
- Rota: /endereco/salvarRota
- Descrição: calcula a melhor rota e persiste no banco.

Body: mesmo do endpoint anterior.

Resposta 201:

```json
{
	"idRota": 1,
	"cepOrigem": "01310100",
	"enderecoOrigem": "Avenida Paulista, 1000, São Paulo, SP",
	"cepDestino": "01311100",
	"enderecoDestino": "Avenida Paulista, 500, São Paulo, SP",
	"distancia": "1.50km"
}
```

### 5.5 Listar rotas salvas

- Método: GET
- Rota: /endereco/listarRotas
- Descrição: retorna todas as rotas persistidas.

Resposta 200:

```json
[
	{
		"idRota": 1,
		"cepOrigem": "01310100",
		"enderecoOrigem": "Avenida Paulista, 1000, São Paulo, SP",
		"cepDestino": "01311100",
		"enderecoDestino": "Avenida Paulista, 500, São Paulo, SP",
		"distancia": "1.50km"
	}
]
```

Resposta 204: sem conteúdo.

## 6. Integrações Externas

### ViaCEP

- Base URL: https://viacep.com.br/ws
- Endpoint consumido: GET /{cep}/json/
- Uso: normalização e enriquecimento de endereço

### Google Routes Distance Matrix v2

- Base URL: https://routes.googleapis.com
- Endpoint consumido: POST /distanceMatrix/v2:computeRouteMatrix
- Headers usados:
	- X-Goog-Api-Key
	- X-Goog-FieldMask: originIndex,destinationIndex,distanceMeters,condition
- Uso: cálculo de distâncias entre origem e destinos

## 7. Persistência

Entidade persistida: ResponseMelhorRotaDTO

- Tabela: rota_completa
- Campos principais:
	- id_rota
	- cep_origem
	- endereco_origem
	- cep_destino
	- endereco_destino
	- distancia

## 8. Tratamento de Erros na Aplicação

Respostas implementadas atualmente:

- 400: entrada inválida ou falha durante cálculo/salvamento
- 404: nenhuma rota válida encontrada para retorno/persistência
- 204: listagem sem registros

Observação: existem classes de exceção/mappers no projeto, porém ainda estão vazias. O tratamento vigente está concentrado no controller e no fluxo de serviços.

## 9. Como Executar

### Pré-requisitos

- JDK 21+
- Maven 3.8+
- Oracle Database acessível
- Chave de API Google válida

### Configuração

Edite src/main/resources/application.properties com seus valores:

```properties
# ViaCEP
quarkus.rest-client.viacep-api.url=https://viacep.com.br/ws

# Google Routes
quarkus.rest-client.google-maps-api.url=https://routes.googleapis.com
google-api-key=SUA_API_KEY_AQUI

# Oracle
quarkus.datasource.db-kind=oracle
quarkus.datasource.username=SEU_USUARIO
quarkus.datasource.password=SUA_SENHA
quarkus.datasource.jdbc.url=jdbc:oracle:thin:@//HOST:PORT/SERVICE_NAME
```

### Rodar em desenvolvimento

Windows:

```bash
./mvnw.cmd quarkus:dev
```

Linux/Mac:

```bash
./mvnw quarkus:dev
```

### Build do projeto

```bash
./mvnw clean package
```

## 10. Documentação e Teste Manual

- Swagger UI: http://localhost:8080/q/swagger-ui
- OpenAPI JSON: http://localhost:8080/q/openapi

## 11. Pontos Técnicos Observados na Análise

- O fluxo principal de cálculo e persistência está funcional.
- Não há suíte de testes automatizados no diretório de testes neste momento.
- O endpoint de cálculo trabalha com menor distância entre rotas válidas retornadas pela API externa.
- Para produção, recomenda-se mover credenciais e chave de API para variáveis de ambiente/secret manager.

## 12. Autores

- Murilo Ayabe Severino - RM567479
- Paulo Cavalcante Caroba - RM566667
- Renan da Silva Paulino - RM566610
