# ADC Projeto Individual

Projeto individual de ADC 2025/2026 para desenvolvimento de um servidor cloud-enabled com Google App Engine e Google Cloud Datastore, seguindo o enunciado da unidade curricular.

## Objetivo
Implementar um conjunto de 10 operacoes REST para gestao de contas, autenticacao e controlo de sessoes, com persistencia na cloud e controlo de acessos por role.

O projeto inclui:
- componente servidor em Java
- persistencia em Google Cloud Datastore
- deploy em Google App Engine
- interface web simples para teste manual dos endpoints

## Tecnologias
- Java 21
- Maven
- Jersey / JAX-RS
- Jakarta Servlet
- Google App Engine Standard
- Google Cloud Datastore

## Estrutura funcional
Todos os endpoints REST usam `POST` e ficam disponiveis sob a raiz:

```text
/rest/*
```

A aplicacao tambem inclui uma pagina web simples de teste na raiz `/`, com formularios para invocar todos os endpoints manualmente.

## Endpoints implementados

| Operacao | Endpoint | Token |
| --- | --- | --- |
| CreateAccount | `/rest/createaccount` | Nao |
| Login | `/rest/login` | Nao |
| ShowUsers | `/rest/showusers` | Sim |
| DeleteAccount | `/rest/deleteaccount` | Sim |
| ModifyAccountAttributes | `/rest/modaccount` | Sim |
| ShowAuthenticatedSessions | `/rest/showauthsessions` | Sim |
| ShowUserRole | `/rest/showuserrole` | Sim |
| ChangeUserRole | `/rest/changeuserrole` | Sim |
| ChangeUserPassword | `/rest/changeuserpwd` | Sim |
| Logout | `/rest/logout` | Sim |

## Formato geral dos pedidos

Pedidos sem token:

```json
{
  "input": {
    "...": "..."
  }
}
```

Pedidos com token:

```json
{
  "input": {
    "...": "..."
  },
  "token": {
    "tokenId": "string",
    "username": "string",
    "role": "USER | BOFFICER | ADMIN",
    "issuedAt": 0,
    "expiresAt": 0
  }
}
```

## Formato geral das respostas

Sucesso:

```json
{
  "status": "success",
  "data": {}
}
```

Erro:

```json
{
  "status": "9906",
  "data": "The call is using input data not following the correct specification"
}
```

## Regras principais do sistema
- O `username` funciona como identificador unico da conta e deve ter formato de email.
- Os roles suportados sao `USER`, `BOFFICER` e `ADMIN`.
- O token de autenticacao tem duracao prevista de 15 minutos.
- Todas as operacoes protegidas validam existencia e expiracao do token.
- Contas e sessoes autenticadas sao guardadas de forma persistente no Datastore.

## Politica de acesso por role
- `CreateAccount`: USER, BOFFICER, ADMIN
- `Login`: USER, BOFFICER, ADMIN
- `ShowUsers`: BOFFICER, ADMIN
- `DeleteAccount`: ADMIN
- `ModifyAccountAttributes`: USER, BOFFICER, ADMIN com restricoes por ownership/role
- `ShowAuthenticatedSessions`: ADMIN
- `ShowUserRole`: BOFFICER, ADMIN
- `ChangeUserRole`: ADMIN
- `ChangeUserPassword`: USER, BOFFICER, ADMIN apenas na propria conta
- `Logout`: USER e BOFFICER apenas nas proprias sessoes; ADMIN pode terminar qualquer sessao

## Principais codigos de erro

| Codigo | Nome |
| --- | --- |
| 9900 | `INVALID_CREDENTIALS` |
| 9901 | `USER_ALREADY_EXISTS` |
| 9902 | `USER_NOT_FOUND` |
| 9903 | `INVALID_TOKEN` |
| 9904 | `TOKEN_EXPIRED` |
| 9905 | `UNAUTHORIZED` |
| 9906 | `INVALID_INPUT` |
| 9907 | `FORBIDDEN` |

Nota: de acordo com o enunciado, estes erros sao devolvidos ao nivel do corpo da resposta, mantendo `HTTP 200 OK`.

## Exemplo de pedidos

### Criar conta

```json
{
  "input": {
    "username": "user@adc.pt",
    "password": "123456",
    "confirmation": "123456",
    "phone": "912345678",
    "address": "Lisboa",
    "role": "USER"
  }
}
```

### Login

```json
{
  "input": {
    "username": "user@adc.pt",
    "password": "123456"
  }
}
```

### ShowUsers

```json
{
  "input": {},
  "token": {
    "tokenId": "uuid",
    "username": "admin@adc.pt",
    "role": "ADMIN",
    "issuedAt": 1710000000,
    "expiresAt": 1710000900
  }
}
```

## Como correr localmente

### Pre-requisitos
- Java 21
- Maven
- Google Cloud SDK
- projeto GCP com App Engine criado
- Datastore em modo Datastore ativo

### Build

```bash
mvn clean package
```

### Execucao local

```bash
mvn appengine:run
```

Depois, a aplicacao fica normalmente disponivel em:

```text
http://localhost:8080/
```

Interface de teste:
- `http://localhost:8080/`

Base REST:
- `http://localhost:8080/rest/`

## Deploy no Google App Engine

Autenticacao e selecao de projeto:

```bash
gcloud auth login
gcloud config set project <projectId>
```

Deploy:

```bash
mvn clean package appengine:deploy "-Dapp.deploy.projectId=<projectId>" "-Dapp.deploy.version=<version>"
```

Exemplo:

```bash
mvn clean package appengine:deploy "-Dapp.deploy.projectId=adc-individual-65702" "-Dapp.deploy.version=v1"
```

## Persistencia
Os dados sao guardados no Google Cloud Datastore com duas entidades principais:
- `User`
- `Session`

## Interface de teste incluida
O projeto inclui uma pequena interface web para demonstracao manual das operacoes REST:
- formulario para cada endpoint
- construcao automatica do payload JSON
- visualizacao da resposta devolvida pelo servidor

## Autor
Projeto desenvolvido no ambito da unidade curricular de Arquitetura e Desenvolvimento Cloud.
