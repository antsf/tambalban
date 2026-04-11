# Data Model: Design System Tokens

This document defines the semantic tokens and constants used in "The Responsive Guardian" design system.

## Color Tokens (Semantic Mapping)

| Token Name | Hex Code | Android Theme Attribute | Usage |
|------------|----------|-------------------------|-------|
| `primary` | `#973497` | `colorPrimary` | High-impact brand moments |
| `primary_container` | `#DA70D6` | `colorPrimaryContainer` | "Emergency Orchid" beacons |
| `surface` | `#f8f9fa` | `android:colorBackground` | Main app background |
| `surface_low` | `#f3f4f5` | `colorSurfaceContainerLow` | Large content blocks (sections) |
| `surface_lowest` | `#ffffff` | `colorSurfaceContainerLowest` | Interactive nested cards |
| `on_surface` | `#191c1d` | `colorOnSurface` | Primary text (High contrast, non-black) |
| `on_surface_variant` | `#454748` | `colorOnSurfaceVariant` | Secondary labels / Tertiary info |
| `tertiary_container` | `#D1E8D1` | `colorTertiaryContainer` | Emergency Chips (Safety Green) |

## Typography Scale

| Scale Role | Font Family | Size (sp) | Weight | Use Case |
|------------|-------------|-----------|--------|----------|
| `display-lg` | Plus Jakarta Sans | 57 | Bold | Empty states |
| `headline-lg` | Plus Jakarta Sans | 32 | SemiBold | Status updates ("Help is 5 mins") |
| `display-sm` | Plus Jakarta Sans | 24 | Bold | Key Metrics (e.g. "1.2 km") |
| `title-lg` | Inter | 22 | Medium | Shop names |
| `body-lg` | Inter | 16 | Regular | Addresses / Descriptions |
| `label-md` | Inter | 12 | Medium | Secondary information |

## Shape & Elevation

| Constant | Value | Description |
|----------|-------|-------------|
| `radius_xl` | `48dp` (3rem) | Main action buttons (Pill) |
| `radius_lg` | `32dp` (2rem) | Bottom sheet top corners |
| `radius_md` | `16dp` (1rem) | Info cards |
| `touch_target_min` | `56dp` | Minimum dimension for any interactive area |
| `ambient_shadow`| `Y: 8, Blur: 24` | @ 6% Alpha | Standard shadow for map elements |

## Component States

- **Disabled**: `primary_fixed_dim` (maintains brand soul).
- **Focused Input**: `surface_container_high` with 2px `primary` Ghost Border (20% opacity).
- **Glass Effect**: 85% opacity + 20px blur.
