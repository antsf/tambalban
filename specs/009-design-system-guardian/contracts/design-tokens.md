# Design System Contracts: The Responsive Guardian

## Theme Contract (`Theme.TambalBanFinder`)

All layouts MUST use children of the main feature theme to ensure consistency.

```xml
<!-- Mandatory attributes to be overridden in themes.xml -->
<item name="colorPrimary">#973497</item>
<item name="colorPrimaryContainer">#DA70D6</item>
<item name="colorSurface">#f8f9fa</item>
<item name="colorOnSurface">#191c1d</item>

<!-- Structural contract (The No-Line Rule) -->
<!-- Boundaries MUST be defined by background shifts, NOT dividers -->
<item name="colorSurfaceContainerLow">#f3f4f5</item> 
<item name="colorSurfaceContainerLowest">#ffffff</item>
```

## Component Interoperability

### 1. Primary Action Button
- **Identifier**: `@style/Guardian.Button.Primary`
- **Contract**:
    - Corner Radius: 48dp (Fixed)
    - Min Height: 56dp (Enforced)
    - Background: `primary_container`
    - Elevation: `ambient_shadow`

### 2. Live-Status Drawer (Bottom Sheet)
- **Identifier**: `@style/Guardian.BottomSheet`
- **Contract**:
    - Top Radius: 32dp
    - Background: `surface_lowest` @ 85% opacity
    - Blur: 20px (via BlurView)

### 3. List Item Separation
- **Rule**: Vertical margin `1.5rem` (24dp) between items.
- **Contract**: `divider` visibility MUST be `gone` in all lists.

## Accessibility Requirements
- All primary beacons (Orchid) must maintain a contrast ratio of at least 4.5:1 against the background.
- "Emergency Chips" must use green tones (`tertiary_container`) to signal safety.
