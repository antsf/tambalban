# Quickstart: TambalBan Finder (Minimal Footprint)

## Environment Requirements
- Android Studio Iguana+
- JDK 17
- Target Device: 2GB RAM+ (Optimized for low-spec)

## Project Setup
1. Clone the repository.
2. The project is designed with **minimal dependencies**. No third-party networking or database libraries are used.
3. Update `local.properties`:
   ```properties
   SUPABASE_URL=https://{project_ref}.supabase.co
   SUPABASE_ANON_KEY={your_anon_key}
   ADMOB_APP_ID={your_admob_app_id}
   ```

## Development Patterns
- **API Calls**: Check `com.tambal_ban.data.api.NetworkClient`. We use native `HttpURLConnection`.
- **Database**: Check `com.tambal_ban.data.database.DbHelper`. We use native `SQLiteOpenHelper`.
- **Parsing**: Logic is handled via `org.json` objects.

## Performance Verification
- **Binary Size**: Use "Build > Analyze APK" to ensure the total size remains under the target threshold.
- **Memory Profiler**: Use the Android Studio Profiler to monitor memory usage on 2GB RAM device emulators.
