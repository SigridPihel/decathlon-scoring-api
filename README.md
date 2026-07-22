# decathlon-scoring-api

A web application for calculating decathlon points from event results. Users can log a result (name, event, performance, date) and the app calculates the official World Athletics score for that event, then displays all logged results in a table.

Built with a Spring Boot REST API backend, a PostgreSQL database, and a Vue.js frontend. The entire stack runs with a single `docker-compose up` command.

## Setup / running it

```
docker-compose up --build
```

This starts three containers:

| Service | URL | Notes |
|---|---|---|
| Frontend (Vue, served by nginx) | http://localhost:8081 | The actual app UI |
| Backend (Spring Boot) | http://localhost:8080 | REST API |
| Swagger UI | http://localhost:8080/swagger-ui.html | Interactive API docs/testing |
| PostgreSQL | localhost:5432 | db/user/password all `decathlon` |

**Note:** the result-entry form in the UI isn't finished yet (see Scope decisions below). Until it is, **use Swagger UI to add results** — open http://localhost:8080/swagger-ui.html, try out `POST /api/decathlon-results`, then refresh http://localhost:8081 to see it appear in the table (the frontend currently fetches the results list once, on page load).

## Architecture

- **Backend** — Spring Boot (Web, Spring Data JPA, Validation), layered as `controller → service → mapper → repository`. Request/response DTOs are kept separate from the JPA entity so the API contract doesn't break if persistence details change. Schema is derived from the entity via Hibernate (`spring.jpa.hibernate.ddl-auto=update`) — no migration tool yet (see Future improvements).
- **Frontend** — Vue 3 + Vite + TypeScript, a single page (no routing yet): a results table and, soon, a result-entry form next to it. Talks to the backend directly over `axios` using its full URL (`http://localhost:8080/...`), with CORS enabled on the backend to allow this.
- **Database** — PostgreSQL, started via `docker-compose`.
- **Docker** — both backend and frontend use multi-stage Dockerfiles (backend: JDK to compile → JRE to run the jar; frontend: Node to build static files → nginx to serve them), so the shipped images don't carry build tooling they no longer need. `docker-compose.yml` at the repo root starts all three services together.
- **Repo layout** — one repo, with `/backend` and `/frontend` folders, rather than two separate repos (see Future improvements for the reasoning).

### Point calculation

Official World Athletics scoring formulas (in effect since 1985), applied per event:

- Track events (lower is better): `Points = ⌊A × (B − P)^C⌋`, P = performance in seconds
- Field events (higher is better): `Points = ⌊A × (P − B)^C⌋`, P = performance in cm (jumps) or metres (throws)

Result is truncated (never rounded) and clamped at 0.

| Event | A | B | C | Unit |
|---|---|---|---|---|
| 100m | 25.4347 | 18 | 1.81 | seconds |
| Long Jump | 0.14354 | 220 | 1.40 | cm |
| Shot Put | 51.39 | 1.5 | 1.05 | metres |
| High Jump | 0.8465 | 75 | 1.42 | cm |
| 400m | 1.53775 | 82 | 1.81 | seconds |
| 110m Hurdles | 5.74352 | 28.5 | 1.92 | seconds |
| Discus | 12.91 | 4 | 1.10 | metres |
| Pole Vault | 0.2797 | 100 | 1.35 | cm |
| Javelin | 10.14 | 7 | 1.08 | metres |
| 1500m | 0.03768 | 480 | 1.85 | seconds |

Each event is also tagged as `TRACK` or `FIELD` (an `EventType` alongside the constants), since the two formulas subtract in opposite directions — the constants alone aren't enough to know which shape to apply. The three jumps (Long Jump, High Jump, Pole Vault) use **centimetres** while the three throws use **metres** — an easy unit slip to make by habit, so it's covered by a dedicated unit-conversion test.

Verified example: 100m in 10.83s → `25.4347 × (18 − 10.83)^1.81 ≈ 899` points, matching this app's actual output.

**Known simplification**: a real decathlon requires an athlete to complete all 10 events for one combined score — this app scores a single event result at a time, not a combined decathlon total (see "competition concept" under Future improvements).

## Scope decisions

### MVP

- Input for a result: name (text), event (dropdown), result, date
- Output: name, event, result, points, date
- The 10 decathlon events are a fixed list:
  - Day 1 — 100 metres, Long jump, Shot put, High jump, 400 metres
  - Day 2 — 110 metres hurdles, Discus throw, Pole vault, Javelin throw, 1500 metres
- Point calculation follows the actual IAAF/World Athletics formulas (not arbitrary numbers), with unit tests covering all 10 events
- Only creating and listing results is supported (no edit/delete yet — see Future improvements)
- Single page: results table + result form next to it (no tabs needed yet)
- Data persists in PostgreSQL; everything runs via `docker-compose up`
- **Current status: the result-entry form UI is not finished yet.** The backend fully supports creating results (validated, scored, persisted) and Swagger UI is enabled specifically so results can be added in the meantime — see Setup above. Finishing the form is the immediate next task.

### Future improvements

Roughly in the order I'd tackle them, given more time:

1. **Finish the result-entry form** — local form state, a dropdown fed by the fixed event list, submit handler emitting up to a parent handler that POSTs and refreshes the table.
2. **Athlete registry** (one cohesive feature, but independently useful in steps):
   - `Athlete` entity + `GET /api/athletes` (optional `?search=`) + `POST /api/athletes`
   - An "Athletes" tab: small add-athlete form + table (name, registered date, distinct events logged)
   - Switch the result form's name field from free text to a filter-as-you-type list of registered athletes
   - Switch attempt-limit matching from the name string to `athleteId`
   - Two-tab layout (Athletes / Results)
3. **Attempt-limit enforcement**: 1 attempt for track events (100m, 400m, 110m Hurdles, 1500m), 3 for Long Jump/Shot Put/Discus/Javelin. Until the athlete registry exists, this would count existing results by matching the name string + event — a documented simplification.
4. **Full CRUD**: `PUT`/`PATCH` and `DELETE` on `/api/decathlon-results/{id}`, plus corresponding UI actions, so a bad entry can be fixed or removed without touching the database directly.
5. Table filtering (by name/event/date) and sorting in the results table
6. Frontend unit tests (Vitest + Vue Test Utils) — form validation, table rendering
7. Controller-layer tests (`@WebMvcTest`) — the existing tests cover the calculation and service layers, but nothing yet proves the HTTP layer itself (routing, `@Valid`, the exception handler) end to end
8. GitHub Actions CI (tests run on every push)
9. Basic logging (SLF4J) around result creation and point calculation
10. **High Jump / Pole Vault attempt-limit enforcement** — deliberately left out. These two events don't reduce to a simple max-attempts number: the bar rises in fixed increments and an athlete is eliminated after 3 consecutive misses at one height, so the real attempt total depends on which heights they clear. Modeling that properly is a small state machine, not a count comparison — leaving it unenforced and documented rather than half-solving it.
11. **A "competition" concept and winner calculation across all 10 events** — deliberately left out. This app scores a single event result at a time; a real decathlon standings feature would need new entities (`Competition`, a Competition↔Athlete relationship) and an aggregation step. The task asked for scoring a single result, not a full standings system, so this is scoped out rather than half-built.
12. **Schema management** — currently Hibernate's `ddl-auto=update`, chosen to keep one fewer new tool in scope under the deadline. With more time (or in a team/production setting) I'd use Liquibase changesets instead, for an explicit, auditable, reversible schema history.
13. User authentication/authorization — out of scope for a homework assignment like this.
14. **Separate repos for frontend and backend** — deliberately kept as one repo instead. Two repos would mean two READMEs, two git histories, and CORS/docker-compose wiring across repos, for no benefit to a reviewer who wants one link to review everything.

## API summary

Base URL: `http://localhost:8080`. Full interactive docs at `/swagger-ui.html` (raw OpenAPI spec at `/v3/api-docs`).

### `GET /api/decathlon-results`
Returns all logged results.

```json
[
  {
    "id": "uuid",
    "athleteName": "string",
    "event": "RUN_100M",
    "performanceValue": 10.83,
    "resultDate": "2026-07-21",
    "points": 899,
    "unit": "SECONDS"
  }
]
```

### `POST /api/decathlon-results`
Creates a result — validates input, calculates points server-side, persists it, and returns the created result (`201 Created`) in the same shape as above.

Request body:

```json
{
  "athleteName": "string, required",
  "event": "one of the 10 fixed event names, required",
  "performanceValue": "positive number, required",
  "resultDate": "date, required, must be today or in the past"
}
```

The 10 valid `event` values: `RUN_100M`, `LONG_JUMP`, `SHOT_PUT`, `HIGH_JUMP`, `RUN_400M`, `HURDLES_110M`, `DISCUS_THROW`, `POLE_VAULT`, `JAVELIN_THROW`, `RUN_1500M`. `unit` in the response is derived automatically from the event (`SECONDS`, `METERS`, or `CENTIMETERS`) — it isn't part of the request.

### Errors

Validation failures and unexpected errors are both handled by a global exception handler, returning a consistent shape:

```json
{
  "message": "string",
  "timestamp": "string",
  "fieldErrors": { "fieldName": "message" }
}
```

- `400 Bad Request` — request validation failed (e.g. missing `athleteName`, negative `performanceValue`); `fieldErrors` maps each invalid field to a message.
- `500 Internal Server Error` — an unexpected failure (e.g. a failed save); `fieldErrors` is empty, and full detail is logged server-side rather than exposed to the client.

## Running the tests

From `backend/`:

```
./gradlew test
```

To run a single test class:

```
./gradlew test --tests "com.sigridpihel.decathlonscoring.service.PointsCalculatorTest"
```

Current coverage: point calculation logic (all 10 events + edge cases) and the service layer (`create` happy path + exception propagation, `findAll` empty/non-empty). Frontend and controller-layer tests are not written yet (see Future improvements).

## Dev log

**Thursday, 16.07.2026 — Planning and scaffolding.** Researched the decathlon scoring domain, used AI to draft an initial project plan, then reviewed it and locked the MVP/stretch scope against the actual task. Created a single GitHub repo with `/backend` and `/frontend` folders, cloned it locally, and generated the Spring Boot project (Web, JPA, Validation, PostgreSQL) directly inside `/backend`.

**Friday, 17.07.2026 — Backend core.** Verified the generated backend built and ran (`./gradlew build`, `./gradlew bootRun`), got PostgreSQL running via `docker-compose up -d`, and pointed Spring Boot at it through `application.properties`. Committed the working skeleton as a baseline checkpoint, set up Claude Code as an assistant, created the initial package structure, and added the `DecathlonEvent` enum with the 10 fixed events.

**Saturday, 18.07.2026 — Backend core.** Built the `DecathlonEventResult` entity and let Hibernate auto-create the schema, verifying the table via IntelliJ's Database tool window. Added the repository interface, request/response DTOs, a mapper, the service's `create` method, and the `POST /api/decathlon-results` endpoint. Added `springdoc-openapi` so Swagger UI was available for manual testing from the start.

**Sunday, 19.07.2026 — Backend core.** Wrote unit tests for the point calculation logic covering all 10 events and edge cases, and checked code coverage. Added `GET /api/decathlon-results` for listing and verified both endpoints through Swagger. Added a `PerformanceUnit` enum (seconds/metres/centimetres) and a `unit` field on the response, so results can be labeled correctly (e.g. "7.23 m" vs "10.83 s").

**Monday, 20.07.2026 — Backend polish.** Added a global exception handler covering both validation failures (400, field-level messages) and unexpected errors (500). Wrote service-layer tests for `create` (happy path, and confirming an exception from `save()` propagates rather than being swallowed) and for `findAll` (empty and non-empty cases).

**Tuesday, 21.07.2026 — Docker + frontend start.** Wrote the backend's multi-stage Dockerfile, confirmed it built and ran standalone, then wired it into `docker-compose.yml` alongside Postgres and verified `docker-compose up --build` from a clean clone. Scaffolded the Vue 3 + Vite + TypeScript frontend and got the dev server running. Sent the backend to a colleague for review.

**Wednesday, 22.07.2026 (today) — Frontend build, Docker, polish, README.** Built the single-page layout (centered title, "Add result" button, results table below via CSS Grid), a `Result` type matching the backend's response shape, and a `ResultsTable.vue` component rendering results with `v-for`. Wired up `axios` with an `onMounted` fetch replacing hardcoded fake data with real data from the backend, and enabled CORS on the backend for the frontend's origin. Wrote a Dockerfile for the frontend (Node build stage → nginx serving stage), added it to `docker-compose.yml`, and verified the full three-service stack end-to-end. Decided to send the current state for review now rather than delay for the form — see Scope decisions.
