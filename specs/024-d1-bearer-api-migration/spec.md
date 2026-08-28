# D1 Bearer API Migration

## Overview

Switch the Android app's entire networking layer from Supabase REST + Supabase Auth to
`tambalban-web`'s D1-backed bearer-token API (`/api/v2/*`), which is already built, tested,
and deployed (`tambalban-web/worker/src/routes-d1.ts`). This is Phase 4d of the workspace's
Supabase→Cloudflare migration (`../tambalban-web/specs/d1-migration-plan.md`) — the last
untouched piece. No UI/UX change: login/register still ask for email+password, the map still
works the same way.

## User Stories

- [P1] As any user, nearby/bounds/search workshop queries hit `/api/v2/workshops` and still
  return results, now sourced from D1 instead of Supabase.
- [P1] As a user, I can register and log in exactly as before — the account now lives in D1
  (same store the web app's contributors use), not Supabase Auth.
- [P1] As a logged-in user, I can add a workshop with a photo — photo upload now goes to
  `POST /api/v2/upload/workshop` (R2), and the workshop insert to `POST /api/v2/workshops`.
- [P1] As a logged-in user, I can view/edit my profile and upload an avatar — now via
  `GET`/`PATCH /api/v2/profile` and `POST /api/v2/upload/avatar`.
- [P2] As a logged-in user, I can submit a review for a workshop via `POST
  /api/v2/workshops/:id/reviews`.
- [P1] As a returning user with an existing Supabase account, logging in still works — the v2
  API's migrate-on-first-login (`legacy-auth.ts`) verifies against Supabase transparently and
  backfills a D1 password hash; no re-registration needed.

## Functional Requirements

- FR-001: Base URL moves to `https://tambalban-web.antsf.workers.dev/` (from
  `xwqckmkjciptlbopmxjl.supabase.co`).
- FR-002: Auth header becomes `Authorization: Bearer <session-token>` only — no `apikey`
  header, no anon-key fallback for anonymous requests (v2's public GETs need no auth header
  at all).
- FR-003: `AuthResponse` shape changes: `{ token, expires_at, user }`, no `refresh_token` (D1
  sessions are single opaque tokens with an expiry, not an access/refresh pair).
- FR-004: `Workshop.verified` and the 8 service-type fields are real JSON booleans from the
  v2 API (server-side fix already shipped, `tambalban-web@a6b530d`) — no client-side coercion
  needed, but this was a real blocker until that fix landed.
- FR-005: `rating`/`total_reviews` no longer exist on the server — `Workshop` keeps them as
  client-only defaults (0.0 / 0), same as today; D1 never sends them, Android never depends
  on non-default values today either (verified: no UI reads a nonzero value from prior API
  responses).
- FR-006: Logout calls `POST /api/v2/auth/logout` (revokes the D1 session row) before clearing
  local prefs — today's `logout()` only clears local prefs, never told the server.
- FR-007: Workshop photo and avatar upload are separate endpoints
  (`/api/v2/upload/workshop`, `/api/v2/upload/avatar`) — today both go through one generic
  `uploadFile(bucket, path)` Supabase Storage call.

## Assumptions

- ⚠️ ASSUMPTION: `WorkshopSubmission.source`/`.verified` fields (currently sent in the
  request body) are dropped from the request — the v2 API always server-sets these and
  silently ignores unknown body fields, so sending them was already inert, not load-bearing.
- ⚠️ ASSUMPTION: No offline queueing changes — `WorkshopDbHelper`'s local-cache/offline-fallback
  behavior in `WorkshopRepository` is preserved as-is, only the network call underneath changes.

## Out of Scope

- No UI/UX changes.
- No changes to `WorkshopDbHelper` (local SQLite cache) schema or logic.
- Not migrating `rating`/`total_reviews` back — they stay dropped, matching the web app.
- Supabase itself stays live and untouched (rollback path, per workspace CLAUDE.md — do not
  pause/delete it after this ships; Phase 5 retirement is separate and later).
