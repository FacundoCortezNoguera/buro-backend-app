# buro-backend-app
 
Backend for **Buró**, a management system built for a nightclub: door access
control and payments. It replaces an old Visual Basic desktop application the
venue was still running on.
 
The web client lives in
[buro-frontend](https://github.com/FacundoCortezNoguera/buro-frontend).
 
## What it does
 
**Door access control.** The system integrates with an **Anviz biometric
fingerprint reader** installed at the entrance, so staff and guests are
identified by fingerprint instead of a printed list or a manual check.
 
<!-- TODO: una o dos frases sobre cómo funciona. ¿El backend le carga las huellas
     al lector, o solo lee los eventos que el lector genera? ¿Registra cada
     ingreso con hora? -->
 
**Payments.**
 
<!-- TODO: qué maneja exactamente. ¿Cobro de entradas? ¿Consumiciones?
     ¿Cuentas por mesa? ¿Cierre de caja? Una o dos frases. -->
 
<!-- TODO: si hay otros módulos (usuarios y permisos, reportes, listas de
     invitados), agregalos acá. -->
 
## Architecture
 
Angular web client → REST API → relational database, with the Anviz reader
<!-- TODO: cómo se comunica el backend con el lector: ¿por red con su SDK?
     ¿leyendo la base de datos del dispositivo? ¿un archivo que exporta? -->
 
## Stack
 
- Java <!-- TODO: versión --> with <!-- TODO: ¿Spring Boot? ¿qué versión? -->
- <!-- TODO: base de datos -->
- Maven
- Docker and Docker Compose
## Running it locally
 
```bash
cp .env.example .env     # fill in your own values
docker compose up
```
 
The API starts on `http://localhost:8080`.
 
## Project status
 
Built end to end — backend, frontend and data model. The client did not move
forward with the production rollout, so the system was never deployed to a
live venue.
 
## Why I built it
 
The venue was running its operation on a Visual Basic desktop application built
years earlier: tied to one machine, hard to change, and with no way to connect
it to anything else.
 
Buró replaces it with a web system. The data lives in one place, staff work from
a browser instead of a single desktop, and door access runs through a biometric
reader wired into the same application rather than sitting in a separate device
nobody could query.
 
Most of the interesting work was not the CRUD — it was modelling the domain
properly and getting the reader to talk to the backend reliably at the door,
where a failure means a queue of people waiting outside.
