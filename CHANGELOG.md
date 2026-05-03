# Changelog

All notable changes to TambalBan documented here.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

---

## [Unreleased]

### Changed
- Migrated data layer from `workshops` table to `tambal_ban` Supabase table
- `Workshop` model: renamed coords to `lat`/`lon`; replaced open/close time fields with `openingHours`; replaced `ratingAvg`/`ratingCount` with `rating`/`totalReviews`; added `city`, `province`, `verified`
- `WorkshopSubmission`: new fields `city` (required), `province`, `openingHours`, `imageUrl`; hardcodes `source="user"`, `verified=false`
- All workshop API endpoints now target `rest/v1/tambal_ban` with `verified=eq.true` filter
- Search now queries name OR city via PostgREST OR filter
- User-submitted workshops insert directly to `tambal_ban` with `verified=false`; hidden until admin approves
- Workshop detail screen now shows city/province and opening hours
- Add Workshop form now collects city (required), province, opening hours, optional photo
- Photo upload to Supabase Storage public bucket `workshops/`; stored as `image_url`
- On submission success: Snackbar "Terkirim, sedang ditinjau admin" then back to map

### Removed
- `SubmissionRepository` (retired; add-workshop now via `WorkshopRepository.addWorkshop()`)
- `workshop_submissions` endpoint usage

### Added
- `CLAUDE.md`: Complete Android agent setup with tech stack, custom components, code style, and do-not rules
- `.specify/memory/constitution.md`: Rewritten v2.0.0 with package-by-feature rule, XML-first, MVVM chain, build verification
- `.claude/agents/brief.md`: Design agent — produces spec.md, contracts, tasks.md
- `.claude/agents/build.md`: Implementer agent — executes tasks.md phase by phase
- `.claude/agents/test.md`: QA agent — writes and runs JUnit4 + MockK tests
- `.claude/context/stack.md`: Single source of truth for agents (tech stack, architecture, key files)
- `.claude/context/android-layout.md`: Package → file mapping for all modules
- `CHANGELOG.md`: This file
- caveman: Claude Code plugin for token-efficient responses (~75% output token reduction)
- speckit: GitHub Spec-Kit v0.8.4 Claude integration (14 skills in `.claude/skills/`)

---

## [0.16.0] — 2026-04-xx

### Changed
- Workshop detail UI: premium design refresh, Indonesian localization

## [0.15.0] — 2026-04-xx

### Added
- Workshop list: infinite scroll, search, edge-to-edge support
