# Research: Home Screen Refinement Patterns

**Feature**: `011-refresh-home-screen`

## Decision 1: osmdroid Map Styling (Mint-Teal Theme)

**Problem**: The provided mockup uses a unique light mint-teal map theme. Standard osmdroid OSM tiles are multi-colored.

**Decision**: Use a **ColorMatrixColorFilter** applied to the map's `TileOverlay`.

**Rationale**: 
- Custom Tile Servers (like Mapbox) are against the constitution if they require proprietary SDKs. 
- Generating a full set of custom tiles is complex. 
- A `ColorMatrix` can shift the entire tile palette to the desired teal/mint spectrum programmatically while keeping the map interactive and lightweight.

**Alternatives considered**: 
- Custom Tile Provider: Rejected due to maintenance complexity.
- Overlays with alpha: Rejected as it washes out street details.

---

## Decision 2: BottomSheet Interaction (Peek to Full)

**Problem**: The spec requires showing exactly 3 items in "Peek" mode and a scrollable list in "Expanded" mode.

**Decision**: Use **`BottomSheetBehavior`** with a dynamic `peekHeight`.

**Rationale**: 
- Standard Android component (`com.google.android.material.bottomsheet.BottomSheetBehavior`).
- Allow the sheet to be dragged. The header ("Nearby Workshops") remains persistent.
- The `NearbyWorkshopAdapter` will use `ListAdapter` for efficient updates if the map center changes.

---

## Decision 3: Floating Search Bar Implementation

**Problem**: Top floating Pill Bar with Avatar.

**Decision**: Implement as a **Custom Layout (ConstraintLayout)** inside a **MaterialCardView**.

**Rationale**: 
- Using a `MaterialCardView` provides easy pill-shaping via `cardCornerRadius`.
- It allows absolute positioning over the `MapView` using XML constraints.
- `CircularProgressIndicator` can be easily added if search is in progress.

---

## Decision 4: Marker Labeling

**Problem**: Image shows names like "Agus Tambal Ban" next to markers.

**Decision**: Use **osmdroid `Marker` with a custom `InfoWindow`** or a `Marker` set with a custom bubble icon.

**Rationale**:
- Standard osmdroid `InfoWindow` is a bit clunky. 
- We will implement a custom `Marker` icon that includes the text label directly in the bitmap generation or use a simplistic specialized overlay for labels to ensure performance with 10k markers.
