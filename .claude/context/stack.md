# TambalBan — Agent Context: Stack

Single source of truth for all agents. Read this at the start of every session.

---

## App

**Name**: TambalBan
**Package**: `com.tambal_ban`
**Purpose**: Find nearby tire repair shops (tambal ban) in Indonesia. Map-first UX.
**Entry point**: `TambalBanApp` (Application, service locator) → `MainActivity` (LAUNCHER)

---

## Tech Stack

| Concern | Solution |
|---|---|
| Language | Kotlin 1.9.22 |
| Min SDK | 24 (Android 7.0) |
| UI | XML layouts + ViewBinding |
| Architecture | MVVM + Repository |
| Networking | Retrofit 2.9 + OkHttp 4.12 |
| Serialization | kotlinx-serialization 1.6.2 |
| Backend | Supabase (PostgreSQL REST + Auth) |
| Maps | osmdroid 6.1.18 |
| Local DB | SQLiteOpenHelper (WorkshopDbHelper) |
| Auth storage | EncryptedSharedPreferences (AuthPrefs) |
| Images | Coil 2.6 |
| DI | Manual — TambalBanApp holds singletons |
| Async | Coroutines 1.7.3 |

---

## Architecture Chain

```
Activity / Adapter
      ↕ (observe LiveData, call ViewModel methods)
ViewModel  [MutableLiveData private, LiveData public, viewModelScope.launch]
      ↕ (suspend fun calls)
Repository  [runCatching/try-catch, no Context, returns domain model or null]
      ↕ (suspend fun, Response<T>)
SupabaseService  [Retrofit interface]
      ↕ HTTP
Supabase REST API  [base: SupabaseConfig.URL]
```

**Rule**: never skip a layer. No Retrofit calls in Activity or ViewModel.

---

## Networking

**Retrofit singleton path**: `core/network/ApiClient.kt`
```kotlin
ApiClient.getService(authPrefs): SupabaseService
```

**Auth mechanism** (`core/network/AuthInterceptor.kt`):
- Always adds: `apikey: {ANON_KEY}`
- Logged in: `Authorization: Bearer {user_jwt}`
- Not logged in: `Authorization: Bearer {anon_key}`
- On 401: clears tokens via `authPrefs.clear()`

**Network setup** (`core/network/NetworkModule.kt`):
- JSON config: `ignoreUnknownKeys=true`, `coerceInputValues=true`, `isLenient=true`
- Base URL: `SupabaseConfig.URL` (`https://xwqckmkjciptlbopmxjl.supabase.co/`)

**Supabase query pattern**:
```kotlin
@GET("rest/v1/workshops")
suspend fun getWorkshops(
    @Query("id") id: String = "eq.{value}",
    @Header("Prefer") prefer: String = "return=representation"
): Response<List<Workshop>>
```

---

## Auth Storage

**Class**: `core/utils/AuthPrefs.kt`
**Storage**: EncryptedSharedPreferences (AES256_GCM)
**Keys**: `access_token`, `refresh_token`, `user_id`, `user_email`

```kotlin
authPrefs.isLoggedIn()       // true if access_token exists
authPrefs.getAccessToken()   // String? JWT
authPrefs.getUserId()        // String? UUID
authPrefs.clear()            // logout
```

---

## Key File Locations

| Type | Path pattern |
|---|---|
| API service | `core/network/SupabaseService.kt` |
| Network client | `core/network/ApiClient.kt` |
| Auth interceptor | `core/network/AuthInterceptor.kt` |
| Repositories | `{feature}/data/{Name}Repository.kt` |
| ViewModels | `{feature}/viewmodel/{Name}ViewModel.kt` |
| Activities | `{feature}/ui/{Name}Activity.kt` |
| App singleton | `TambalBanApp.kt` |
| Local DB | `workshop/data/database/WorkshopDbHelper.kt` |

---

## UI Conventions

- All Activities extend `BaseActivity` (edge-to-edge, `applySafeArea()`)
- ViewBinding: `binding = Activity{Name}Binding.inflate(layoutInflater)`
- Theme: `Theme.TambalBan` (Material3 Light, no action bar)
- Styles: `Guardian.*` prefix
- Icon size standard: 20dp

**Custom components** (always use these instead of raw widgets):

| Component | File | Use for |
|---|---|---|
| `TambalButton` | `core/ui/TambalButton.kt` | All buttons |
| `TambalTextField` | `core/ui/TambalTextField.kt` | All input fields |
| `AvatarView` | `core/ui/AvatarView.kt` | Profile images |
| `LiveStatusDrawer` | `core/ui/LiveStatusDrawer.kt` | Workshop status bottom sheet |

---

## Constitution Rules (Summary)

1. **Package-by-Feature**: `workshop/` must not import `auth/` and vice versa. Cross-feature via `Intent.setClassName()`.
2. **XML-First**: No Compose. ViewBinding only.
3. **No premature abstractions**: No Hilt, no Room, no multi-module, no use-cases.
4. **MVVM-Chain**: Never skip layers.
5. **Build passes**: `./gradlew assembleDebug` must pass before PR.
6. **No `!!`**: Use `?.` or `?:`.
7. **CHANGELOG.md**: Update before task complete.

Full constitution: `.specify/memory/constitution.md`
