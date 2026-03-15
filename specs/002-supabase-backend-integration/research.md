<!--
Sync Impact Report:
- Version change: N/A → 1.0.0
- Templates requiring updates: N/A
- Decision Log:
  - Auth: POST /auth/v1/token?grant_type=password
  - Database: PostgREST range filters for bounding box
  - Storage: EncryptedSharedPreferences for tokens
-->

# Research: Supabase Integration for Tambal Ban Finder

## Decision: Supabase Auth Implementation
**Chosen Approach**: Utilize the Supabase Auth REST API via Retrofit.
**Rationale**: Avoids the overhead of a heavy SDK while maintaining full control over the authentication flow and token storage.
**Details**:
- **Endpoint**: `POST /auth/v1/token?grant_type=password`
- **Body**: `{"email": "user@example.com", "password": "password"}`
- **Response**: Returns a JSON object containing `access_token`, `refresh_token`, and user details.
- **Headers**: Requires `apikey` header for all requests.

## Decision: Data Fetching and Bounding Box Queries
**Chosen Approach**: Standard PostgREST range filters.
**Rationale**: Simple to implement without requiring complex spatial extensions on the initial schema, while still being highly performant for 10k markers.
**Details**:
- **Query**: `GET /workshops?latitude=gte.{minLat}&latitude=lte.{maxLat}&longitude=gte.{minLng}&longitude=lte.{maxLng}&verified=eq.true`
- **Pagination**: Use `range` header for offset-based pagination if required by the UI for list views.

## Decision: Secure Token Storage
**Chosen Approach**: AndroidX `EncryptedSharedPreferences`.
**Rationale**: Industry standard for encrypting sensitive data at rest on Android.
**Details**:
- **Library**: `androidx.security:security-crypto:1.1.0-alpha06`
- **MasterKey**: Generated using `MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()`.
- **Initialization**: `EncryptedSharedPreferences.create(context, "auth_prefs", masterKey, prefKeyEncryptionScheme, prefValueEncryptionScheme)`.

## Decision: Authentication Interceptor
**Chosen Approach**: Custom OkHttp Interceptor.
**Rationale**: Centralizes the logic for adding `apikey` and `Authorization: Bearer <token>` headers to all outgoing requests.
**Details**:
- **Injection**: Injected into the Retrofit `OkHttpClient`.
- **Logic**: Reads the token from `EncryptedSharedPreferences`. If no token exists (public mode), only the `apikey` is sent.

## Alternatives Considered
- **GoTrue SDK**: Rejected because the project already uses Retrofit for networking; adding another networking library for just Auth is redundant.
- **Plain SharedPreferences**: Rejected due to security requirements in the project constitution.
- **PostGIS Spatial Queries**: Considered but deferred until a dedicated location column is implemented in the Supabase schema. Flat latitude/longitude filters are sufficient for current needs.
