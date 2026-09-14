# buro-buro-backend-app

Backend for a nightclub management system: door access control and payments.

## What it does

Buró is a custom management system built for a nightclub. This repository holds
the backend API; the web client lives in
[buro-frontend](https://github.com/FacundoCortezNoguera/buro-frontend).

**Door access control.** The system integrates with an Anviz biometric
fingerprint reader installed at the entrance. [Cómo funciona: el lector valida
la huella y el backend registra el ingreso? ¿El backend le carga las huellas
al lector, o solo lee los eventos?]

**Payments.** [¿Qué maneja exactamente: cobro de entradas, consumiciones,
cuentas por mesa, cierre de caja?]

[¿Hay otros módulos? Usuarios, reportes, socios, listas de invitados?]

## Architecture

Angular web client → REST API → [base de datos], with the Anviz reader
[conectado cómo: por red, por SDK, leyendo la base del dispositivo?].

## Stack

- Java [versión] with [¿Spring Boot? ¿qué versión?]
- [base de datos]
- Maven
- Docker and Docker Compose

## Running it locally

​```bash
cp .env.example .env     # fill in your own values
docker compose up
​```

The API starts on [puerto].
