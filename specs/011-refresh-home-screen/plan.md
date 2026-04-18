# Implementation Plan: Refresh Home Screen Design & Search

**Branch**: `011-refresh-home-screen` | **Date**: 2026-04-18 | **Spec**: [specs/011-refresh-home-screen/spec.md](file:///Users/antasofa/Devs/Java%20&%20Kotlin/Project/tambalban/specs/011-refresh-home-screen/spec.md)

## Summary

This plan covers the modernization of the Home Screen UI (now consolidated into `MainActivity`) and the addition of a **Dynamic Search Suggestion** system. The architecture uses a hybrid debounced-search approach: providing live suggestions as the user types and a full results refresh upon submission.

## Technical Context

**Language/Version**: Kotlin 1.9  
**Debounce Mechanism**: `StateFlow` + `debounce(500)` in `MainViewModel`  
**Search Filter**: Supabase `ilike` query on `workshops.name`  
**Overlay View**: Floating `RecyclerView` anchored below the Search Overlay pill.

## Project Structure (Modernized)

```text
app/src/main/java/com/tambal_ban/
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt           # Central coordinator for Map, Sheet, and Search
│   │   ├── MainViewModel.kt          # Search state and suggestion pipeline
│   │   ├── NearbyWorkshopAdapter.kt  # Bottom sheet list
│   │   └── SearchSuggestionAdapter.kt # [NEW] Dropdown overlay list
├── data/
│   ├── api/
│   │   └── SupabaseService.kt        # Search endpoint integration
│   └── repository/
│       └── WorkshopRepository.kt    # Add fuzzy search logic
```

## Phase 3: Dynamic Search Infrastructure

1. **ViewModel Pipeline**:
   - `searchQuery: MutableStateFlow<String>`
   - `suggestions: Flow<List<Workshop>>` derived via `debounce(500)` and `switchMap` to repository calls.
2. **Search Overlay Fragment/Component**:
   - Update `view_search_overlay.xml` to include a `RecyclerView` with a `white` background and `20dp` rounded corners.
   - Synchronize overlay visibility with keyboard focus and query length (min 3 chars).

## Phase 4: Interactions & Snapping

1. **Selection Event**: Tapping a suggestion triggers a `CameraSnap` event.
2. **Acceptance Criteria Check**:
   - Verify that tapping a result snaps the map and centers the workshop.
   - Verify that pressing the keyboard "Search" key triggers `viewModel.refreshAllResults(query)`.
tar into a `TextInputLayout` or `MaterialToolbar` overlay without breaking accessibility.

**Output**: `research.md` resolving the osmdroid styling approach and BottomSheet interaction specifics.
