# Research: Modular Structure Strategies

**Feature ID**: 014-modular-structure

## Current Fragmentation

- **ViewModels**: Found in `com.tambal_ban.viewmodel`, `com.tambal_ban.ui.auth`, `com.tambal_ban.ui.detail`, `com.tambal_ban.ui.add`, and `com.tambal_ban.ui.main`.
- **Data Layer**: Centralized in `com.tambal_ban.data`, but many repositories are feature-specific (e.g. `AuthRepository` is only for auth).
- **Utils**: Mixture of domain-specific (e.g. `AuthPrefs`) and generic (e.g. `GeoUtils`).

## Proposed Module Boundaries

### 1. `core` (Shared Infrastructure)
Contains code that is required by multiple features and doesn't belong to a specific user journey.
- **Sub-packages**: `network`, `ui` (common components), `utils`, `location`, `ads`.

### 2. `features.auth` (Account & Identity)
Handles everything related to user identity.
- **Components**: Login, Registration, Profile, Edit Profile.
- **Data**: `AuthRepository`, `ProfileRepository`, `AuthModels`, `Profile`.

### 3. `features.workshop` (Core Domain)
Handles the lifecycle of a workshop and user interactions with it.
- **Components**: Detail, Add Workshop, Reviews, Submissions.
- **Data**: `WorkshopRepository`, `ReviewRepository`, `SubmissionRepository`, `Workshop`, `Review`, `WorkshopSubmission`.
- **Local Cache**: `WorkshopDbHelper`, `WorkshopMapper`.

### 4. `features.home` (Discovery & Navigation)
The primary entry point of the app.
- **Components**: MainActivity (Map), Search Suggestion, Nearby Workshops.
- **Data**: Uses `WorkshopRepository` from core or shared. (Note: `WorkshopRepository` is heavily used here, might need to stay in a shared data layer if it's too central).

## Technical Decisions

1. **Naming Strategy**: Use `com.tambal_ban.features.<name>` for user-facing features to clearly distinguish them from infrastructure.
2. **ViewModel Placement**: Move ViewModels into the same package as their respective Activities to maximize "locality".
3. **Data Layer**: Keep feature-specific models and repositories within the feature package. If a repository is used by >2 major features, move it to `core.data`.
4. **Refactoring Tooling**: Use `git mv` to preserve history. Update imports using IDE refactoring.

## Migration Sequence

1. Move `core` elements (utils, common ui, network).
2. Refactor `auth` feature.
3. Refactor `workshop` feature.
4. Refactor `home` feature.
5. Update `AndroidManifest.xml` and Layout imports.
