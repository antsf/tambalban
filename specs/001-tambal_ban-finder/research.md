# Research: Lightweight Android Implementation

## Decision: Networking with `HttpURLConnection`
- **Choice**: Native `java.net.HttpURLConnection`.
- **Rationale**: Eliminates ~2MB of binary size from OkHttp/Retrofit. While more verbose, it is sufficient for the limited API scope of this app.
- **Pattern**:
  - Use `ThreadPoolExecutor` (built-in) for background threads.
  - Implement a `NetworkRequest` helper to handle headers (Supabase API Key) and response reading.

## Decision: Storage with `SQLiteOpenHelper`
- **Choice**: Native `android.database.sqlite.SQLiteOpenHelper`.
- **Rationale**: Room is an abstraction over SQLite that requires additional dependencies and annotation processing. Manual SQL is faster to compile and keeps the APK small.
- **Pattern**:
  - `DbHelper` class for table creation and upgrades.
  - `ContentValues` for insertion/updates.
  - Manual mapping of `Cursor` objects to Kotlin Data Classes.

## Decision: JSON Parsing with `org.json`
- **Choice**: Native `org.json` package.
- **Rationale**: Built into the Android SDK. Avoids reflection overhead and library bloat from Gson or Moshi.
- **Pattern**:
  - `JSONObject` and `JSONArray` usage within the API layer to map network responses to data objects.

## Decision: Map Memory Management
- **Choice**: osmdroid with restrictive tile cache.
- **Rationale**: Low-spec devices (2GB RAM) are prone to OOM when many bitmaps are loaded.
- **Optimization**:
  - `Configuration.getInstance().setCacheLowSize(50 * 1024 * 1024)` (50MB).
  - Explicitly clear overlays when switching contexts.

## Decision: Minimal Assets
- **Choice**: Vector Drawables for all markers and icons.
- **Rationale**: SVGs are much smaller than multiple PNG densities.
