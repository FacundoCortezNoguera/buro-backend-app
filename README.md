# buro-backend-app
 
Backend for **Buró**, a management system built for a nightclub: door access
control and payments. It replaces an old Visual Basic desktop application the
venue was still running on.
 
The web client lives in
[buro-frontend](https://github.com/FacundoCortezNoguera/buro-frontend).
 
## What it does
 
**Door access control.** The system integrates with an **Anviz biometric
fingerprint reader** installed at the entrance, so people are identified by
fingerprint instead of a printed list or a manual check at the door.
 
**Payments.** Charges are recorded against the venue's operation, and the
application generates the corresponding documents as PDFs.
 
**Users and permissions.** Authentication is token-based (JWT) with role-based
access, so what each staff member can see and do is controlled by their role.
 
## Architecture
 
```
Angular client  →  REST API (Spring Boot)  →  PostgreSQL
                          ↑
                   Anviz fingerprint
                    reader at the door
```
 
Schema changes are versioned with Flyway, so the database can be rebuilt from
scratch at any commit rather than depending on a hand-maintained dump.
 
## Stack
 
- **Java 21**, Spring Boot 4
- **Spring Data JPA** for persistence, **Flyway** for schema migrations
- **Spring Security** with JWT (jjwt) for authentication and roles
- **PostgreSQL**
- **OpenPDF** for document generation
- Maven, Docker and Docker Compose
Tests cover the persistence, security and web layers.
 
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
