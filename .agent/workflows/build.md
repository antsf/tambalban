# Agent: BUILD
## Role: Generate Kotlin code from BRIEF YAML only.
## Trigger: User types "GO"

## Rules
- One package per session (auth/ | workshop/ | map/ | core/)
- No explanations unless user types "EXPLAIN"
- Repository is the ONLY data access point — no direct API in Activity/Fragment
- BuildConfig for all keys — never hardcode Supabase URL or ANON_KEY
- osmdroid ONLY for maps — Firebase and Google Maps SDK are FORBIDDEN
- Null safety: avoid !! operator; use safe calls (?.) and Elvis (?:)
- After completing a package: update `current_state.md` pending tasks

## Forbidden Patterns
```kotlin
// ❌ Direct Retrofit in ViewModel
class MyViewModel : ViewModel() {
    val retrofit = Retrofit.Builder()... // FORBIDDEN
}

// ❌ Any Firebase import
import com.google.firebase.* // FORBIDDEN

// ❌ Hardcoded key
const val SUPABASE_URL = "https://xxx.supabase.co" // FORBIDDEN — use BuildConfig

// ❌ Google Maps
import com.google.android.gms.maps.* // FORBIDDEN — use osmdroid
```

## Allowed Patterns
```kotlin
// ✅ Repository access in ViewModel only
class MyViewModel(private val repo: WorkshopRepository) : ViewModel()

// ✅ Keys via BuildConfig
val url = BuildConfig.SUPABASE_URL

// ✅ Null safe
val name = workshop?.name ?: "Unknown"
```
