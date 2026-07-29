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

**Note:** the result-entry form in the UI is now built and functional — use it directly at http://localhost:8081. Swagger UI remains available as an alternative way to add results (e.g. for quick manual testing without the UI) — open http://localhost:8080/swagger-ui.html and try out `POST /api/decathlon-results`; refresh http://localhost:8081 afterward to see it appear in the table if you added it this way.

## Architecture

- **Backend** — Spring Boot (Web, Spring Data JPA, Validation), layered as `controller → service → mapper → repository`. Request/response DTOs are kept separate from the JPA entity so the API contract doesn't break if persistence details change. Schema is derived from the entity via Hibernate (`spring.jpa.hibernate.ddl-auto=update`) — no migration tool yet (see Future improvements).
- **Frontend** — Vue 3 + Vite + TypeScript, a single page (no routing yet): a results table and a result-entry form next to it. Talks to the backend directly over `axios` using its full URL (`http://localhost:8080/...`), with CORS enabled on the backend to allow this.
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

Each event is also tagged as `TRACK` or `FIELD` (an `EventType` alongside the constants), since the two formulas subtract in opposite directions — the constants alone aren't enough to know which shape to apply. The three jumps (Long Jump, High Jump, Pole Vault) use **centimetres** while the three throws use **metres** — an easy unit slip to make by habit. **Note: the app does no unit conversion at all** — whoever submits a result (the UI or a direct API call) must already provide the value in that event's expected unit; there's currently no test or validation catching a mixed-up unit (e.g. entering metres where centimetres are expected) — see Future improvements.

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
- **Current status: the result-entry form is built and working** (create + list flow functional end-to-end). One known gap: a failed submission (e.g. backend validation error) is currently only logged to the console, not shown to the user — see Future improvements. Swagger UI remains available as an alternative way to add results — see Setup above.
- **Visual polish is still in progress.** Functionality (fetching, creating, validating) is done, but the UI's look and feel isn't finished yet — actively being worked on now.

### Future improvements

1. **Polished error handling in the result-entry form** — the form itself is built (local state, event dropdown, submit handler emitting up to a parent handler that POSTs and updates the table). If the POST fails (e.g. a 400 validation error or unexpected 500 from the backend's `GlobalExceptionHandler`), it's currently only logged to the console — no error is shown to the user yet. A polished version would surface the general error message in a banner, and map the backend's per-field `fieldErrors` onto each specific input.
2. **Athlete registry** (one cohesive feature, but independently useful in steps):
   - `Athlete` entity + `GET /api/athletes` (optional `?search=`) + `POST /api/athletes`
   - An "Athletes" tab: small add-athlete form + table (name, registered date, distinct events logged)
   - Switch the result form's name field from free text to a filter-as-you-type list of registered athletes
   - Switch attempt-limit matching from the name string to `athleteId`
   - Two-tab layout (Athletes / Results)
3. **Attempt-limit enforcement**: 1 attempt for track events (100m, 400m, 110m Hurdles, 1500m), 3 for Long Jump/Shot Put/Discus/Javelin. Until the athlete registry exists, this would count existing results by matching the name string + event — a documented simplification.
4. **Full CRUD**: `PUT`/`PATCH` and `DELETE` on `/api/decathlon-results/{id}`, plus corresponding UI actions, so a bad entry can be fixed or removed without touching the database directly.
5. Table filtering (by name/event/date) and sorting in the results table
5a. Pagination for the results table — currently the frontend fetches and renders every result at once; this doesn't scale as the number of logged results grows.
6. Frontend unit tests (Vitest + Vue Test Utils) — form validation, table rendering
7. Controller-layer tests (`@WebMvcTest`) — the existing tests cover the calculation and service layers, but nothing yet proves the HTTP layer itself (routing, `@Valid`, the exception handler) end to end
8. GitHub Actions CI (tests run on every push)
9. Basic logging (SLF4J) around result creation and point calculation
10. **High Jump / Pole Vault attempt-limit enforcement** — deliberately left out. These two events don't reduce to a simple max-attempts number: the bar rises in fixed increments and an athlete is eliminated after 3 consecutive misses at one height, so the real attempt total depends on which heights they clear. Modeling that properly is a small state machine, not a count comparison — leaving it unenforced and documented rather than half-solving it.
11. **A "competition" concept and winner calculation across all 10 events** — deliberately left out. This app scores a single event result at a time; a real decathlon standings feature would need new entities (`Competition`, a Competition↔Athlete relationship) and an aggregation step. The task asked for scoring a single result, not a full standings system, so this is scoped out rather than half-built.
12. **A `ScoringFormula` record for `DecathlonEvent`**: the enum currently holds `aConstant`, `bConstant`, `cConstant` as three separate loose fields. Grouping them into a single `ScoringFormula(a, b, c)` record would give the formula its own proper domain concept, instead of three anonymous doubles sitting side by side on the enum.
13. **Duplication in `PointsCalculator`**: `calculateTrackPoints` and `calculateFieldPoints` both independently call `(int) Math.floor(...)` to truncate the result. Worth extracting into one shared helper function instead of repeating the same rounding logic in two places.
14. **A unit test proving truncation, not rounding**: `PointsCalculatorTest`'s existing values don't actually distinguish flooring from standard rounding — none of them use a performance value whose raw calculated result lands just under a whole number (e.g. `X.9-something`), which is the one scenario where flooring and rounding would produce different answers. A proper test needs a hand-picked event/performance combination landing in that range, to actually prove the result is floored rather than coincidentally correct.
15. **Catching mismatched units on submission**: the app doesn't convert or validate units at all — a result is scored using whatever number is submitted, trusting it's already in that event's expected unit (cm for jumps, metres for throws, seconds for track). There's no test or validation catching an easy mistake like entering `5.008` instead of `500.8` for a long jump. Would need either input validation (e.g. plausible-range checks per event) or, at minimum, a documented/tested example of the failure mode.
16. **Schema management** — currently Hibernate's `ddl-auto=update`, chosen to keep one fewer new tool in scope under the deadline. With more time (or in a team/production setting) I'd use Liquibase changesets instead, for an explicit, auditable, reversible schema history.
17. **Separate repos for frontend and backend** — deliberately kept as one repo instead. Two repos would mean two READMEs, two git histories, and CORS/docker-compose wiring across repos, for no added benefit. **Update:** there are plans to split them into separate repositories later.
18. **A standalone "just calculate points" tool** — a simpler mode on the site for calculating points from a score without logging a full result (no name/date required, purely a calculator).
19. **An info button showing each event's baseline performance (B constant)** — the "zero-point" mark for that event: a performance threshold (a time or distance) below which an athlete scores no points at all. An athlete has to clear this baseline just to get on the scoreboard for that event.

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

### `GET /api/decathlon-events`
Returns all 10 fixed events, with a friendly display label — used to populate the event dropdown in the result-entry form.

```json
[
  {
    "event": "RUN_100M",
    "displayName": "100 m",
    "unit": "SECONDS"
  }
]
```

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

**Wednesday, 22.07.2026 — Frontend build, Docker, polish, README.** Built the single-page layout (centered title, "Add result" button, results table below via CSS Grid), a `Result` type matching the backend's response shape, and a `ResultsTable.vue` component rendering results with `v-for`. Wired up `axios` with an `onMounted` fetch replacing hardcoded fake data with real data from the backend, and enabled CORS on the backend for the frontend's origin. Wrote a Dockerfile for the frontend (Node build stage → nginx serving stage), added it to `docker-compose.yml`, and verified the full three-service stack end-to-end. Decided to send the current state for review now rather than delay for the form — see Scope decisions.

**Thursday, 23.07.2026 — Frontend core.** Reviewed everything built on the frontend so far and decided the existing split (`App.vue` as the container holding state, `ResultsTable.vue` as a plain display component) was fine as-is, with only the form needing to become a new component. Pinned down the form's payload shape to match the backend's `DecathlonEventRequestDto` exactly (`athleteName`, `event`, `performanceValue`, `resultDate` — no `unit`/`id`/`points`). Added a new backend endpoint (`DecathlonEventController`, `GET /api/decathlon-events`) returning each event's name, a new `displayName` field added to the `DecathlonEvent` enum, and its unit, so the form's dropdown could be populated from real data instead of a hardcoded list. Declared the form's four local refs (athlete name, event, performance value, date) and started building the template field by field, beginning with the name input.

**Friday, 24.07.2026 — Frontend core.** Finished the form's remaining fields — the event dropdown (populated from the fetched event options) and the performance/date inputs — and verified in the browser that the dropdown correctly listed all 10 events and that each field updated its ref correctly. Decided on an emit-based submission architecture: the form builds the payload and emits it upward via `defineEmits`, rather than calling `axios` itself, keeping `App.vue` as the single place responsible for API calls. Wired up the emit and the corresponding `@submit.prevent` listener on both the form and its parent.

**Sunday, 26.07.2026 — Make the frontend functional, push to GitHub.** Implemented `App.vue`'s `handleCreateResult` handler: added a `NewResult` type for the payload, made the function `async`, and wired the real `axios.post` call to the create endpoint. On success, the newly created result (returned directly in the response, already including its generated `id`/`points`/`unit`) is pushed straight into the existing results array rather than refetching, and the form closes. Wrapped the POST call in a `try/catch`, deciding to just log failures to the console for now rather than build a polished error UI — documented that trade-off explicitly in Future improvements. Added lightweight client-side validation using native HTML attributes: a dynamically computed max date (today, blocking future dates) and a `min="0.01"` on the performance input (blocking zero/negative values), as simpler alternatives to a full backend-error-driven validation UI. Pushed the current progress to GitHub.
