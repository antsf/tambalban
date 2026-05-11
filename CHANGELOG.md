# Changelog

All notable changes to TambalBan documented here.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

---

## [Unreleased]

### Added
- Dark mode toggle in Profile settings with persistent preference (follows system by default)
- Theme toggle label and icon change dynamically with active theme
- App launcher icons for all densities
- AdMob banner ads on MainActivity (home screen) and WorkshopListActivity
- `.opencode/commands/` with /brief, /build, /commit, /test commands

### Changed
- Primary color from orchid `#D672E1` to blue violet `#8A2BE2` with adjusted palette
- Theme default from light-only to follow system (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
- Dark mode resources: `values-night/colors.xml` and `values-night/themes.xml`
- `item_workshop_nearby.xml` redesigned with MaterialCardView, constraint layout, theme-aware colors
- Bottom sheet header: hardcoded `@color/white` replaced with `@color/surface_container_lowest`
- Workshop detail: hardcoded white bg and button tints replaced with theme-aware color resources
- `TambalTextField` style: hardcoded hex values replaced with color resources with night variants
- `BaseActivity`: applies saved night mode preference before super.onCreate
- `AuthPrefs`: theme storage changed from Boolean to Int (supports follow-system)

---

## [0.16.0] — 2026-04-xx

### Changed
- Workshop detail UI: premium design refresh, Indonesian localization

## [0.15.0] — 2026-04-xx

### Added
- Workshop list: infinite scroll, search, edge-to-edge support
