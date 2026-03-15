# Quickstart: Supabase Integration

This guide explains how to initialize the Supabase connection in the Android app.

## 1. Configuration Constants
Ensure `com.tambal_ban.utils.SupabaseConfig.kt` has your Supabase credentials:

```kotlin
object SupabaseConfig {
    const val URL = "https://xwqckmkjciptlbopmxjl.supabase.co/"
    const val ANON_KEY = "sb_publishable_VDZL8LuKtE1kv5r0YFzypQ_sMz77ZWI"
}
```

## 2. Networking Setup
Initialize the `OkHttpClient` with the `AuthInterceptor`:

```kotlin
// In AuthInterceptor.kt
val token = authPrefs.getAccessToken()
val isAuthRequest = originalRequest.url.toString().contains("/auth/v1/")

if (!token.isNullOrEmpty()) {
    requestBuilder.addHeader("Authorization", "Bearer $token")
} else if (!isAuthRequest) {
    // Only add anon token to data requests, NOT auth/login requests
    // to avoid "invalid_credentials" errors during login
    requestBuilder.addHeader("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
}
```

## 3. Repository Injection
Initialize the `SupabaseService` via Retrofit:

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl(SupabaseConfig.URL)
    .client(client)
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .build()

val service = retrofit.create(SupabaseService::class.java)
```

## 4. Key Workflows

### Loading Map Markers
1. Get visible bounding box from `osmdroid`.
2. Call `service.getWorkshops(minLat, maxLat, minLng, maxLng)`.
3. Map result to `Workshop` models.
4. Update `MapViewModel` LiveData.

### Authenticating User
1. Enter credentials in `LoginActivity`.
2. Call `service.login(LoginRequest(email, password))`.
3. On success, save `access_token` and `user_id` to `AuthPrefs`.
   - `user_id` is critical for RLS (Row Level Security) policies.
4. Navigate to Home screen.
