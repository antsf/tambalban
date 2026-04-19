# Data Model: User Profile

## Entities

### Profile
Represents the public-facing user information.

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Primary key, matches `auth.users.id` |
| full_name | String | User's full name |
| email | String | User's email (synced from auth or editable) |
| phone | String | User's phone number |
| avatar_url | String? | Public URL to the avatar image in Supabase Storage |
| updated_at | ISO8601 | Last update timestamp |

## Validation Rules
- `full_name`: MUST NOT be empty.
- `email`: MUST follow valid email regex.
- `phone`: MUST be numeric if provided.

## State Transitions
- **View**: Initial state when opening the profile.
- **Editing**: Triggered by "Edit Profile" or avatar edit icon.
- **Saving**: Network request to Supabase `PATCH /profiles`.
- **Error**: Validation failure or network failure.
