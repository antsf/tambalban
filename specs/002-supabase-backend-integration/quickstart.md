# Quickstart: Supabase Integration

This guide explains how to initialize the Supabase connection in the Android app.

## 1. Configuration Constants
Create `com.tambalban.utils.SupabaseConfig.kt`:

```kotlin
object SupabaseConfig {
    const val URL = "https://xwqckmkjciptlbopmxjl.supabase.co/"
    const val ANON_KEY = "sb_publishable_VDZL8LuKtE1kv5r0YFzypQ_sMz77ZWI"
}
```

## 2. Networking Setup
Initialize the `OkHttpClient` with an interceptor:

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("apikey", SupabaseConfig.ANON_KEY)
            
        // Add Authorization header if token exists
        sharedPrefs.getToken()?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        
        chain.proceed(requestBuilder.build())
    }
    .build()
```

## 3. Repository Injection
Initialize the `SupabaseService` via Retrofit:

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl(SupabaseConfig.URL)
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create())
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
3. On success, save `access_token` to `EncryptedSharedPreferences`.
4. Navigate to Home screen.
