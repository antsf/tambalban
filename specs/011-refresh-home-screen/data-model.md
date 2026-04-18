# Data Model: Home Screen Refresh

**Feature**: `011-refresh-home-screen`

## Entities

### Workshop
*Represents a service location displayed on the map and in the bottom sheet.*

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Unique identifier |
| `name` | String | Name of the workshop (e.g. "Agus Tambal Ban") |
| `latitude` | Double | Map latitude |
| `longitude` | Double | Map longitude |
| `rating` | Float | Average user rating (0.0 to 5.0) |
| `status` | Enum | `OPEN`, `CLOSED` |
| `imageUrl` | String | URL to the workshop's cover image |
| `address` | String | Formatted address string |
| `distance` | Double | Calculated distance from user (transient/computed) |

---

### HomeState
*Represents the UI state of the Home Screen.*

| Field | Type | Description |
|-------|------|-------------|
| `workshops` | List<Workshop> | Current local workshops to display |
| `searchQuery` | String | Current active search text |
| `isLoading` | Boolean | Loading state for network requests |
| `selectedWorkshop` | Workshop? | Workshop currently focused on the map/sheet |
| `error` | String? | Error message for the bottom sheet |

---

## State Transitions

1. **Idle → Fetching**: Triggered by user moving the map or searching.
2. **Fetching → Success**: Workshops updated, loading icon hidden.
3. **Fetching → Failure**: Error state shown in bottom sheet.
4. **Workshop Selected**: Map pans to marker, bottom sheet scrolls to the specific card.
