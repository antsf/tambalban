# Data Models: Supabase Integration

## Workshop Entity
Represents a tire repair workshop verified for display on the map.

| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Unique identifier |
| name | String | Workshop name |
| address | String? | Physical address |
| phone | String? | Contact number |
| latitude | Double | Geographic latitude |
| longitude | Double | Geographic longitude |
| open_time | String? | Opening time (e.g., "08:00") |
| close_time | String? | Closing time (e.g., "17:00") |
| is_24h | Boolean | True if operating 24 hours |
| rating_avg | Float | Aggregated rating score |
| rating_count | Int | Total number of reviews |
| verified | Boolean | True if approved by admin |

## Review Entity
Represents a user-submitted experience.

| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Unique identifier |
| workshop_id | String (UUID) | ID of the target workshop |
| user_id | String (UUID) | Submitter ID (Supabase Auth) |
| rating | Int | Score (1-5) |
| comment | String | User feedback text |
| created_at | String | ISO Timestamp |

## WorkshopSubmission Entity
Used for proposing new workshop locations.

| Field | Type | Description |
|-------|------|-------------|
| id | String (UUID) | Unique identifier |
| name | String | Proposed name |
| address | String | Proposed address |
| latitude | Double | Proposed latitude |
| longitude | Double | Proposed longitude |
| phone | String | Proposed phone |
| user_id | String (UUID) | Submitter ID |
| status | String | "pending", "approved", or "rejected" |

## Authentication Entities

### LoginRequest
```kotlin
data class LoginRequest(
    val email: String,
    val password: String
)
```

### AuthResponse
Contains the JWT session info.
```kotlin
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val user: User
)

data class User(
    val id: String,
    val email: String
)
```
