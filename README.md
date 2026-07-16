# decathlon-scoring-api

A web application for calculating decathlon points from event results. Users can log a result (name, event, performance, date) and the app calculates the official World Athletics score for that event, then displays all logged results in a table.
Built with a Spring Boot REST API backend, a PostgreSQL database, and a Vue.js frontend. The entire stack runs with a single docker-compose up command.

# Dev log:
16.07.2026 - 


# Scope Decisions

1) MVP:

Input for a result: name (text), event (dropdown), result, date
Output: name, event, result, points, date
The 10 decathlon events are a fixed list:
  Day 1 - 100 metres, Long jump, Shot put, High jump, 400 metres
  Day 2 - 110 metres hurdles, Discus throw, Pole vault, Javelin throw, 1500 metres
Each event enforces a max number of attempts: 1 for track events (100m, 400m, 110m Hurdles, 1500m), 3 for Long Jump, Shot Put, Discus, Javelin. High Jump and Pole Vault are not enforced (see note below). The check counts existing results with the same name string + event — a documented simplification, since there's no verified athlete registry yet in the MVP (see stretch #1)
Single page: results table + the result form next to it (no tabs needed yet — tabs only become relevant if you build stretch #1)
Point calculation follows actual IAAF/World Athletics formulas (not an arbitrary number)
Unit tests for the point calculation logic
Data persists in PostgreSQL
Everything runs via docker-compose up
README with setup instructions + architecture decisions + dev log


2) Stretch:

1. Athlete registry (one cohesive package, but each step below is independently useful — do them in order, a partial version is fine to leave partial):
  a. Athlete entity (id, name, createdAt) + GET /api/athletes (optional ?search=) + POST /api/athletes
  b. Athletes tab in the UI: small add-athlete form (name only) + table (name, registered date, events logged X/10 — a distinct-event count, purely informational)
  c. Switch the Results form's name field from free text to a simple filter-as-you-type list sourced from registered athletes (a text input + a filtered list below it, click to select — nothing fancier)
  d. Switch the attempt-limit check from matching on the name string to matching on athleteId (more robust than string matching)
  e. Two-tab layout (Athletes / Results) — only needed once (b) exists; a simple local "active tab" toggle, no router needed
2. Table filtering (by name/event/date)
3. Sorting in the results table
4. Frontend unit tests (Vitest + Vue Test Utils) — form validation, table rendering
5. Unit tests also for the service layer (not just the calculation logic)
6. GitHub Actions CI (tests run on every push)

3) Future improvements:

- A "competition" concept (a dated event that groups results together) and winner calculation across all 10 events.
Adding that would need new entities (Competition, a Competition↔Athlete relationship) and an aggregation step (sum an athlete's points across all 10 events for one competition, compare totals). The task asked for point calculation for a single event result — not a full competition/standings system. Spell out clearly in the     README why you left this out and how you'd add it given more time. This itself is a strong signal to the employer that you know how to scope work.
- User authentication/authorization
- Separate repos for backend and frontend (see section 2 for why)
- High Jump / Pole Vault attempt-limit enforcement. These two events don't reduce to a simple max-attempts number — the bar rises in fixed increments (3cm / 10cm) and an athlete is eliminated after 3 consecutive misses at one height, so the real attempt total depends on which heights they clear. Modeling that properly is a small state machine, not a count comparison. Leave attempts on these two events unlimited/unenforced and document it as a conscious simplification.


Short description — what the app does - done
Running it — docker-compose up, which ports things run on
Architecture — backend/frontend/DB structure, briefly
Scope decisions — what's in the MVP, what's stretch, what's deliberately left out and why (see section 1) - done
API overview — short list of endpoints
Running the tests
Dev log — 3-5 sentences per day about what you did and why (this is what shows the employer your working pattern)
Future improvements — what you'd do next given more time
