# Research: User Profile Screen Implementation

## Decision: Supabase Profiles & Storage Integration

### Profiles Table
- **Decision**: Use a `profiles` table in the `public` schema with `id` as a foreign key to `auth.users.id`.
- **Rationale**: Separates public-facing user data (name, avatar) from private auth data. Allows easier querying via REST API.
- **Attributes**: `id` (UUID), `full_name` (Text), `email` (Text), `phone` (Text), `avatar_url` (Text), `updated_at` (Timestamp).

### Avatar Storage
- **Decision**: Store profile pictures in a Supabase Storage bucket named `avatars`.
- **Rationale**: Integrated with Supabase Auth/REST. Handles large binary files efficiently.
- **Mechanism**:
    - Upload using `POST /storage/v1/object/avatars/{user_id}/{filename}`.
    - Requires `Authorization: Bearer {token}` and `Content-Type: image/*`.
    - Retrieve via public URL if bucket is public, or signed URL if private.

### Image Selection (Android)
- **Decision**: Use the modern Android **Photo Picker** (`PickVisualMedia`).
- **Rationale**: No special permissions required for basic image selection on Android 13+. Backward compatible via Google Play Services.
- **Implementation**: `registerForActivityResult(ActivityResultContracts.PickVisualMedia())`.

## Decision: Navigation & Authentication

### Entry Point
- **Decision**: Implement a `setOnClickListener` on `ivUserAvatar` in `MainActivity` (handling `view_search_overlay`).
- **Rationale**: Direct and accessible as requested.

### Auth Redirect
- **Decision**: Use a `SessionManager` or check `SupabaseAuth` state in `ProfileViewModel` or `ProfileActivity`.
- **Rationale**: Centralized auth state management ensures consistency.

## Alternatives Considered

### Firebase Storage
- **Rejected**: Constitution forbids Firebase.
### Local Database (Room) Caching
- **Rejected**: Simplicity First principle. Use network-first with graceful failure for now. Caching can be added if offline requirements expand.
