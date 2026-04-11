# Research: Design System: The Responsive Guardian

## Decision: Implementation of "The Responsive Guardian" on Android

### 1. Tonal Layering & Borderless UI
- **Decision**: Map the "No-Line" rule to Material 3 semantic surface tokens.
- **Rationale**: Android Material 3 natively supports a wide range of surface roles (`surface`, `surfaceContainerLow`, `surfaceContainerHigh`, etc.). By explicitly mapping these to the requested hex codes, we can achieve structural integrity without borders.
- **Implementation**:
    - `surface`: `#f8f9fa`
    - `surfaceContainerLow`: `#f3f4f5`
    - `surfaceContainerLowest`: `#ffffff`
    - `onSurface`: `#191c1d` (Strictly avoid #000000 per requirements).

### 2. Glassmorphism & Backdrop Blur (Min SDK 24)
- **Glassmorphism Strategy**: Utilize semi-transparent backgrounds (`#D9FFFFFF`) and high elevation to achieve a "Flat Glass" effect. This replaces the `BlurView` library to prioritize build stability and minimize external dependencies (Simplicity First).
- **Rationale**: Native `RenderEffect` only works on Android 12+. To maintain the "Digital Concierge" premium feel for users on older devices (Min SDK 24), a library based on bitmap capturing is necessary for the Live-Status drawer.
- **Alternatives Considered**: 
    - *Static blurred background*: Rejected as it breaks map spatial awareness during user movement.
    - *Semi-transparent background without blur*: Rejected as it fails the "premium tactile soul" requirement.

### 3. Typography: Editorial Pairing
- **Decision**: Package **Plus Jakarta Sans** and **Inter** as localized font resources.
- **Rationale**: Localized resources ensure consistent rendering across different OEMs and provide offline reliability (per Constitution Principle IV).
- **Scale**:
    - Headlines/Display: Plus Jakarta Sans (High character, authoritative).
    - Body/Utility: Inter (Pure utility, high legibility).

### 4. Component Geometry
- **Decision**: Implement `xl` corner radius as `3rem` (approx `48dp` in Android standard density) for pill-shaped buttons.
- **Touch Targets**: Strictly enforce `minHeight="56dp"` for all interactive elements via custom attributes or style overrides.

## Summary of Technical Choices
| Feature | Choice | Rationale |
|---------|--------|-----------|
| Architecture | XML/Themes + Material 3 | Standard project stack, simple to maintain |
| Blur Effect | BlurView (Dimezis) | Best-in-class performance for Min SDK 24 |
| Font Strategy | Localized Font Resources | Offline safety and OEM consistency |
| Shadow System | Custom Ambient Shadows | Prevents "muddy" default Material shadows |
