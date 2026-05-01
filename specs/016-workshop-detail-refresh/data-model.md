# UI Data Model: Workshop Detail

This model defines the UI state used by `WorkshopDetailActivity`.

```kotlin
data class WorkshopDetailUIState(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val fullAddress: String,
    val phoneNumber: String,
    val businessHours: String,
    val ratingAvg: String,
    val reviewCountText: String,
    val statusText: String,     // e.g., "BUKA SEKARANG"
    val statusColorRes: Int,    // e.g., R.color.success
    val is24h: Boolean
)
```

## Relationships
- Maps directly from `Workshop` entity.
- Computed in `WorkshopDetailViewModel`.
