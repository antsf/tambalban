# Changelog

All notable changes to TambalBan documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

---

## [Unreleased]

### Added
- AdMob native ads in WorkshopListActivity: injected every 5 workshop items, memory-safe destroy in onDestroy

### Changed
- `ad_native.xml`: restyled to match workshop card using MaterialCardView, theme colors, and "Iklan" label
- `activity_workshop_list.xml`: RecyclerView constrained above adContainer to fix banner overlap
- `WorkshopListAdapter`: refactored to multi-view-type adapter supporting workshop and native ad slots
- `WorkshopListActivity`: wires native ad loading per slot on workshop list update, destroys ads on destroy
- All user-visible strings externalized to string resources (i18n) — no hardcoded UI text
- Docs (README, SPEC) aligned with the real shared `tambal_ban` schema

### Security
- Removed the `admin_delete_review` RLS policy — any authenticated user could delete any
  review. Review deletion is now admin-only via the service role.

### Fixed
- ProGuard/R8 config keeps kotlinx-serialization classes — release builds no longer crash
  on JSON deserialization

---

## [0.22.0] — 2026-05-11

### Added
- Splash screen, signing keystore, and overall UI polish
- Unit and Espresso tests across all features

## [0.21.0] — 2026-05-10

### Changed
- "Bengkel" renamed to "Tambal Ban" across all UI labels and strings

## [0.20.0] — 2026-05-10

### Added
- Firebase Analytics: screen_view tracking on all activities (auto via BaseActivity)
- Firebase Analytics: events for login, register, logout, search, share, theme toggle
- Firebase Analytics: events for call workshop, navigate to workshop, review submit, workshop submit
- Firebase Crashlytics: crash reporting with user ID (set on login, cleared on logout)
- Firebase Crashlytics: non-fatal error logging in all repositories (network errors)
- In-app update: flexible update prompt via Play Core when new version available (3-day cooldown)
- Dark mode toggle in Profile settings with persistent preference (follows system by default)
- Theme toggle label and icon change dynamically with active theme

### Changed
- Theme default from light-only to follow system (`AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM`)
- Dark mode resources: `values-night/colors.xml` and `values-night/themes.xml`
- `item_workshop_nearby.xml` redesigned with MaterialCardView, constraint layout, theme-aware colors
- Bottom sheet header: hardcoded `@color/white` replaced with `@color/surface_container_lowest`
- Workshop detail: hardcoded white bg and button tints replaced with theme-aware color resources
- `TambalTextField` style: hardcoded hex values replaced with color resources with night variants
- `BaseActivity`: applies saved night mode preference before `super.onCreate`
- `AuthPrefs`: theme storage changed from Boolean to Int (supports follow-system)

## [0.19.0] — 2026-05-10

### Added
- AdMob banner ads on MainActivity (home screen) and WorkshopListActivity
- App launcher icons for all densities
- `.opencode/commands/` with /brief, /build, /commit, /test commands

### Changed
- Primary color from orchid `#D672E1` to blue violet `#8A2BE2` with adjusted palette

## [0.18.0] — 2026-05-04

### Added
- Add a workshop directly from the Profile screen (spec 018)
- Image picker on the workshop submission form

## [0.17.0] — 2026-05-04

### Changed
- Data layer migrated to the real shared `tambal_ban` table (new schema with `source`,
  `verified`, `verified_at`, `user_id`, service-flag booleans, OSM provenance) — the app now
  shares one table with TambalBan Web

---

## [0.16.0] — 2026-05-01

### Changed
- Workshop detail UI: premium design refresh, Indonesian localization

## [0.15.0] — 2026-04-30

### Added
- Workshop list: infinite scroll, search, edge-to-edge support
- Nearby search refactored to a bounding-box query instead of an RPC

## [0.14.0] — 2026-04-27

### Changed
- Project structure modularized by feature packages (`auth/`, `workshop/`, `map/`, `core/`)

## [0.13.0] — 2026-04-27

### Added
- User registration (email/password) with MVVM and Supabase Auth

## [0.12.0] — 2026-04-20

### Added
- User profile screen with edit and share features
- Supabase user profile integration with storage (avatar) and camera support

## [0.11.0] — 2026-04-19

### Added
- Home screen hybrid search with skeleton loading (Shimmer) and refined bottom sheet

## [0.10.0] — 2026-04-13

### Added
- Modernized login UI with reusable Tambal components (TambalButton, TambalTextField)

## [0.9.0] — 2026-04-11

### Added
- "The Responsive Guardian" Material 3 design system (orchid palette, Plus Jakarta Sans)
