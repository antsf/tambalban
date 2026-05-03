# Workshop Schema Update — Align to tambal_ban Table

## Overview

Migrate app data layer from old `workshops` table to new `tambal_ban` Supabase table.
Scraper data uses new schema (`lat`/`lon`, `city`, `province`, `opening_hours`, `rating`).
App must display new fields and user add-workshop form must collect them.
Keep only user-relevant fields. Internal scraper metadata retained where useful for data provenance and display.

## User Stories

- [P1] As a user, I see workshop city and province on the detail screen
- [P1] As a user, I see opening hours (e.g. "Mo-Fr 08:00-17:00" or "24/7") instead of separate open/close times
- [P1] As a user submitting a workshop, I can enter city, province, and opening hours
- [P2] As a user submitting a workshop, I can optionally attach a photo of the shop
- [P2] As a user, I see workshop rating sourced from scraper data (read-only, reviews come later)
- [P2] As a user searching workshops, I can find by name or city (single search query matches either)

## Functional Requirements

- FR-001: `Workshop` model maps to `tambal_ban` table; coords use `lat`/`lon`
- FR-002: Display fields: name, address, city, province, phone, opening_hours, rating, total_reviews, image_url
- FR-003: Keep in model: source (data provenance), total_reviews (count display), rating (read-only)
- FR-003b: Drop from model: osm_id, shop_type, brand, website, osm_url (no user value)
- FR-004: Add-workshop form required fields: name, address, city, lat/lon (map pin), phone
- FR-004b: Add-workshop optional fields: province, opening_hours, image (photo picker → Supabase Storage upload → image_url)
- FR-010: Image upload to Supabase Storage **public** bucket `workshops`; path `{userId}/{uuid}.jpg`; resulting public CDN URL stored as `image_url` in POST body
- FR-011: WorkshopDetailActivity displays `image_url` via Coil (placeholder shown if null)
- FR-005: User-submitted workshops saved to `tambal_ban` table with `source = 'user'` and `verified = false`
- FR-005b: Map queries filter `verified = eq.true` — user submissions hidden until admin sets `verified = true` in Supabase
- FR-005c: `tambal_ban` table requires `verified boolean DEFAULT false` column (Supabase migration needed before deploy)
- FR-006: Local SQLite cache updated to match new schema
- FR-007: Bounding box queries use `lat`/`lon` column names (not `latitude`/`longitude`)

## Clarifications

### Session 2026-05-03 (round 2)

- Q: Should source, total_reviews, and rating be kept in the model? → A: Yes — all three retained. source for data provenance, total_reviews and rating for display (read-only until review feature ships).
- Q: User-submitted workshop visibility — immediate or hidden until admin approval? → A: Hidden. Insert with `verified = false`; map queries filter `verified = eq.true`. Admin approves via Supabase dashboard.
- Q: Required fields in Add Workshop form? → A: C — name + lat/lon + phone + address + city required; province + opening_hours optional.
- Q: Search scope? → A: B — search by name OR city (OR query against both columns).
- Q: Post-submission UX? → A: B now
- Q: Images per workshop — single or multiple? → A: Single `image_url text` column in `tambal_ban`. No separate images table.
- Q: Who uploads images? → A: B — user uploads optional photo during add-workshop flow (Supabase Storage); scrapers populate `image_url` from external URL.
- Q: Where is workshop image displayed? → A: B — hero image on detail screen + small thumbnail in WorkshopListActivity list items. — show "Terkirim, sedang ditinjau admin" then navigate back to map. "My Submissions" list screen deferred to future feature.
- Q: Supabase Storage bucket access — public or private? → A: A — public bucket; `image_url` stored as direct CDN URL; Coil.load() works without auth.

## Assumptions

- ⚠️ ASSUMPTION: User-submitted workshops insert directly into `tambal_ban` (no separate submissions table). `source = 'user'` distinguishes them. Admin can filter/remove via Supabase dashboard. Separate submission workflow deferred.

- FR-008: On successful submission, show Snackbar/dialog "Terkirim, sedang ditinjau admin" then finish() back to map
- FR-009: Search queries `tambal_ban` with OR filter on `name` and `city` fields

## Out of Scope

- Reviews table and rating submission (future feature)
- Admin approval workflow
- `opening_hours` parser — displayed as raw OSM string for now
- Province dropdown selector — free text input for now
- `workshop_submissions` table migration (table retired if assumption holds)
- "My Submissions" list screen (future feature — deferred from Q4)
