# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]
**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implement a flat-layout User Profile and Editing screen as part of "The Responsive Guardian" design refresh. Users can access the profile from a dedicated icon in the Home Screen search bar. The feature includes viewing profile details, editing name/email/phone, and updating the avatar via Supabase Storage. Unauthenticated access is restricted by a redirect to the Login Screen.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Kotlin 1.9+, Android SDK (Min 24)
**Primary Dependencies**: Retrofit 2, OkHttp 4, Supabase Auth/Rest, Coil (Image Loading)
**Storage**: Supabase (PostgreSQL `profiles` table, Storage `avatars` bucket)
**Testing**: JUnit 4, Mockito, Espresso
**Target Platform**: Android (Min SDK 24)
**Project Type**: Mobile App
**Performance Goals**: <300ms profile render, 60fps scrolling
**Constraints**: No-Line design rule, Offline Safety (error/loading states)
**Scale/Scope**: 1 user profile, 2 screens (View, Edit)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Requirement | Status |
|------|-------------|--------|
| MVVM Boundaries | Logic in ViewModels, UI in Fragments/Activities | PASS |
| Repository Pattern | All profile data via `ProfileRepository` | PASS |
| API-Driven | No hardcoded profile data (Supabase) | PASS |
| Offline Safety | Explicit handling of Loading/Error/Empty states | PASS |
| No-Line Rule | Use tonal shifts, not borders | PASS |

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

app/src/main/
├── java/com/tambal_ban/
│   ├── data/
│   │   ├── api/SupabaseService.kt (Update)
│   │   ├── model/Profile.kt (New)
│   │   └── repository/ProfileRepository.kt (New)
│   ├── viewmodel/
│   │   └── ProfileViewModel.kt (New)
│   └── ui/
│       └── auth/
│           ├── ProfileActivity.kt (New)
│           └── EditProfileActivity.kt (New)
└── res/layout/
    ├── activity_profile.xml (New)
    ├── activity_edit_profile.xml (New)
    └── view_search_overlay.xml (Update)

**Structure Decision**: [Document the selected structure and reference the real
directories captured above]

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| [e.g., 4th project] | [current need] | [why 3 projects insufficient] |
| [e.g., Repository pattern] | [specific problem] | [why direct DB access insufficient] |
