# Technical Plan: Project Modularization (Lightweight)

**Feature ID**: 014-modular-structure
**Strategy**: Package by Feature (Simplicity-First)

## Technical Summary

The goal is to move the project from a layer-based structure to a feature-based structure. This will be done in a "lightweight" manner, meaning we are simply reorganizing packages without adding new interfaces, abstraction layers, or Gradle modules.

## Architecture & Data Flow

We maintain the **MVVM** and **Repository** patterns as mandated by the constitution. The only change is the physical location of the files.

- **Feature Modules**: Self-contained packages containing UI, ViewModel, and Data layers for a specific user flow.
- **Core Module**: Shared infrastructure used by all features.

## File Reorganization Map

### 1. Core Module (`com.tambal_ban.core`)
| Category | Source Path | Target Path |
|----------|-------------|-------------|
| Network | `data/api/*` | `core/network/*` |
| UI | `ui/components/*` | `core/ui/*` |
| Utils | `utils/*` | `core/utils/*` |
| Services | `location/*`, `ads/*` | `core/location/*`, `core/ads/*` |

### 2. Auth Feature (`com.tambal_ban.auth`)
| Category | Sub-package | File(s) |
|----------|-------------|---------|
| UI | `auth.ui` | `LoginActivity`, `RegisterActivity`, `ProfileActivity`, `EditProfileActivity` |
| ViewModel | `auth.viewmodel` | `LoginViewModel`, `RegisterViewModel`, `ProfileViewModel` |
| Data | `auth.data` | `AuthRepository`, `ProfileRepository`, `AuthModels`, `Profile` |

### 3. Workshop Feature (`com.tambal_ban.workshop`)
| Category | Sub-package | File(s) |
|----------|-------------|---------|
| UI | `workshop.ui` | `AddWorkshopActivity`, `WorkshopDetailActivity`, `ReviewAdapter` |
| ViewModel | `workshop.viewmodel` | `AddWorkshopViewModel`, `WorkshopDetailViewModel` |
| Data | `workshop.data` | `WorkshopRepository`, `ReviewRepository`, `SubmissionRepository`, `Workshop`, `Review`, `WorkshopSubmission`, `database/` |

### 4. Map Feature (`com.tambal_ban.map`)
| Category | Sub-package | File(s) |
|----------|-------------|---------|
| UI | `map.ui` | `MainActivity`, `NearbyWorkshopAdapter`, `SearchSuggestionAdapter` |
| ViewModel | `map.viewmodel` | `MainViewModel` |

## Constitutional Updates

The **Section VI (Code Organization Rules)** of `constitution.md` will be updated to reflect the package-by-feature structure.

## Migration Steps

1. **Phase 1: Setup & Core** (Low risk, infrastructure only)
2. **Phase 2: Auth Feature** (Group auth files)
3. **Phase 3: Workshop Feature** (Group workshop and database files)
4. **Phase 4: Map Feature** (Finalize root UI)
5. **Phase 5: Validation** (Fix imports in Kotlin and XML)

## Risk Mitigation

- **Build Failures**: Refactor one feature at a time and verify build.
- **Unresolved References**: Use IDE search & replace for package names in `AndroidManifest.xml` and layouts.
- **Git History**: Use `git mv` where possible.
