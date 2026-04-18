# Quickstart: Home Screen Refresh

**Feature**: `011-refresh-home-screen`

## Setup Instructions

1. **Verify Native Environment**: Ensure the project is building as a pure Android Native project (all Flutter remnants should be gone).
2. **Material 3 Check**: Confirm `com.google.android.material:material:1.11.0` or higher is in `build.gradle.kts` for M3 component support.
3. **osmdroid Config**: Initialize osmdroid in `HomeActivity`'s `onCreate` before `setContentView`:
   ```kotlin
   Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
   ```

## Key Components

- **HomeActivity**: Entry point. Manages the osmdroid MapView and the `BottomSheetBehavior`.
- **HomeViewModel**: Use to observe `workshops` and `searchStatus`.
- **NearbyWorkshopAdapter**: Binds `Workshop` entities to `item_workshop_nearby.xml`.

## Running the Feature

1. Open `HomeActivity`.
2. The map should load with the teal ColorMatrix applied.
3. Pull up the bottom sheet to see mock/live data from the `WorkshopRepository`.
4. Tap the profile avatar in the search bar to verify touch targets.
