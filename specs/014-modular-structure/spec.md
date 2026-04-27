# Feature Specification: Project Structure Modularization

**Feature ID**: 014-modular-structure
**Status**: [SPECIFY]
**Priority**: P1 (Infrastructure)
**Owner**: Antigravity

## Context & Problem Statement

Currently, the project follows a **layer-based packaging** structure (`data/`, `ui/`, `viewmodel/`). While this works for small projects, it leads to fragmentation as the project grows (e.g., related files like `LoginActivity` and `LoginViewModel` are in different directories). 

**Goals**:
- Improve code discoverability by grouping related files into feature packages.
- **Avoid Over-Abstraction**: Keep the architecture simple (MVVM). Do not add unnecessary layers like Domain/UseCases or separate Gradle modules unless absolutely necessary.
- Reduce cognitive load when working on a specific feature.
- Standardize the location of ViewModels (currently split between `ui/auth` and `viewmodel/`).
- Create a clear `core` package for shared infrastructure.

## User Stories

- **As a Developer**, I want to find all files related to Authentication in one place so I can modify the auth flow faster.
- **As a Maintainer**, I want a standardized project structure that prevents "package soup" as more features are added.

## Functional Requirements

- [ ] **FR1: Feature-Based Packaging**: Reorganize the project into feature modules (e.g., `auth`, `workshop`, `map`).
- [ ] **FR2: Core Module**: Consolidate shared utilities, network configurations, and base classes into a `core` package.
- [ ] **FR3: ViewModel Alignment**: Move all ViewModels into their respective feature packages alongside their Activities.
- [ ] **FR4: Data Encapsulation**: Move feature-specific repositories and models into the feature package.

## Non-Functional Requirements

- [ ] **NFR1: Build Stability**: The project must compile after every move.
- [ ] **NFR2: Resource Linking**: Ensure XML layouts and resource references are updated correctly.
- [ ] **NFR3: Constitutional Alignment**: Update the project constitution to reflect the new modular organization.

## Success Criteria

- [ ] The `ui/` and `data/` directories are replaced by a `features/` (or similar) structure.
- [ ] No mixed layers in the root packages (except for cross-cutting concerns in `core`).
- [ ] Build completes successfully without unresolved references.

## Proposed Structure (Package by Feature)

The project will move from Layer-based (`ui/`, `data/`) to Feature-based packages. This maximizes cohesion and simplifies navigation without adding new architectural layers.

```text
com.tambal_ban.
├── core/
│   ├── network/      (SupabaseService, Interceptors, NetworkModule)
│   ├── ui/           (Shared components: TambalButton, TambalTextField)
│   ├── utils/        (GeoUtils, IntentUtils, Constants, AuthPrefs)
│   ├── ads/          (AdMobManager)
│   └── location/     (LocationService)
├── auth/             (Login, Register, Profile, EditProfile, AuthRepository, AuthModels)
├── workshop/         (Detail, Add, Reviews, WorkshopRepository, Database logic)
└── map/              (MainActivity, MapViewModel, Adapters)
```

## Risks & Mitigations

- **Risk**: Large number of merge conflicts if other features are developed in parallel.
- **Mitigation**: Perform refactoring in a dedicated branch and coordinate with the team.
- **Risk**: Import errors in XML layouts (Data Binding/View Binding).
- **Mitigation**: Update imports in layouts carefully and use IDE refactoring tools where possible.
