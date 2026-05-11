# Dark Mode Toggle

## Overview
Add dark/light theme toggle in Profile settings. Users can switch between light and dark themes. Preference persists across app restarts. Uses Android's `AppCompatDelegate.setDefaultNightMode()` and resource qualifier (`values-night/`).

## User Stories
- [P1] As a user, I can toggle between light and dark theme from Profile settings
- [P1] As a user, my theme preference persists after closing and reopening the app
- [P2] As a user, the theme applies immediately without restarting the activity

## Functional Requirements
- FR-001: ProfileActivity has a "Tema Gelap" toggle row with Switch
- FR-002: Toggle state is saved to SharedPreferences
- FR-003: Theme applies immediately via `AppCompatDelegate.setDefaultNightMode()`
- FR-004: Theme preference loaded on app start via `BaseActivity`
- FR-005: Dark mode colors defined in `values-night/` resource qualifier

## Assumptions
- Uses Android resource qualifier (`values-night/`) for theme definition, not a custom theming engine
- Orchid palette adapted for dark: dark surfaces, lighter text
- BottomSheet dialogs also get dark variant

## Out of Scope
- AMOLED-optimized dark mode
- Auto-theme (follow system)
- Per-screen theme override
