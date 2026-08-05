# TambalBan - Specification Document

## 1. Project Overview

### Project Name
TambalBan

### Project Type
Android Mobile Application

### Core Functionality
A mobile app that helps drivers quickly find the nearest tire repair shop (tambal ban) during emergency situations such as flat tires. The app uses OpenStreetMap with osmdroid library to display workshop locations, supports offline caching, and includes an emergency mode for quick access to the closest workshop.

---

## 2. Technology Stack & Choices

### Framework & Language
- **Language**: Kotlin 1.9.x
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **Architecture Components**: ViewModel, LiveData, ViewBinding
- **Database**: Native SQLite (`SQLiteOpenHelper`) with Repository pattern
- **Dependency Injection**: Manual (Repository pattern)

### Key Libraries/Dependencies

| Category | Library | Version |
|----------|---------|---------|
| Networking | Retrofit | 2.9.0 |
| Networking | OkHttp / Logging Interceptor | 4.12.0 |
| JSON Serialization | kotlinx-serialization | 1.6.2 |
| Maps | osmdroid | 6.1.18 |
| Maps Extras | osmbonuspack | 6.9.0 |
| Location | Google Play Services Location | 21.1.0 |
| Ads | Google Mobile Ads SDK | 23.0.0 |
| Image Loading | Coil | 2.6.0 |
| Coroutines | Kotlin Coroutines | 1.7.3 |
| Security | AndroidX Security (EncryptedSharedPreferences) | 1.1.0 |
| UI Effects | Shimmer | 0.5.0 |

### State Management
- LiveData for reactive UI updates
- ViewModel for UI state management
- Repository pattern for data access and synchronization

### Backend
- **Platform**: Supabase (PostgreSQL)
- **Authentication**: Supabase Auth (Email/Password, Anonymous)
- **Storage**: Supabase Storage for workshop and profile images
- **API Style**: REST via Retrofit (Supabase PostgREST API)

---

## 3. Feature List

### Core Features

1. **Map Screen (The Guardian View)**
   - Display map using osmdroid library
   - Show user location with GPS tracking
   - Load workshop markers from backend API (bounding box queries)
   - Support marker clustering for density management
   - Dynamic search radius (1km, 3km, 5km)

2. **Nearby Search & Hybrid Discovery**
   - Auto-detect GPS location
   - Find nearest workshops using SQL-level bounding box and Haversine formula
   - Unified search results with skeleton loading (Shimmer effect)

3. **Workshop Detail (Bottom Sheet)**
   - Display workshop name, address, phone, distance, rating
   - Workshop images loaded via Coil
   - Verified status indicator
   - Primary Actions: Call (Dialer Intent), Navigate (Map Intent)

4. **User Authentication & Profile**
   - User Registration and Login
   - Profile management (Full Name, Phone, Avatar)
   - Secure token storage using EncryptedSharedPreferences

5. **Add Workshop & Submissions**
   - Form to submit new workshop data
   - Location picker for precise lat/lng
   - Submissions insert directly into `tambal_ban` with `source = 'user'` and `verified = false`; admin publishes by flipping `verified = true` in Supabase

6. **Offline Support**
   - Cache workshop data in local SQLite database
   - Spatial queries on local data when network is unavailable
   - Seamless transition between Remote and Local data sources

---

## 4. Data Tables (Supabase)

> Reference schema: `supabase_schema.sql` in this repo. The single shared table is
> `tambal_ban` (see `specs/017-workshop-schema-update/` — the `workshops` /
> `workshop_submissions` tables are retired).

### 1. tambal_ban  (the ONE workshop table, shared with the web app)
- id (UUID, PK)
- name (text)
- lat (double)
- lon (double)
- address (text, nullable)
- city (text, nullable)
- district (text, nullable)
- province (text, nullable)
- phone (text, nullable)
- whatsapp (text, nullable)
- website (text, nullable)
- instagram (text, nullable)
- opening_hours (text, nullable)
- rating (double, default 0.0) — read-only, sourced from scraper data
- total_reviews (int, default 0)
- image_url (text, nullable)
- source (text: 'osm' | 'user')
- verified (boolean, default false) — map queries filter `verified = eq.true`
- verified_at (timestamptz, nullable)
- user_id (UUID, FK → auth.users) — stamped by trigger on insert
- osm_id (bigint, nullable) + osm_tags (jsonb, nullable) — OSM provenance
- service flags (boolean ×8): motorcycle_tyres, car_tyres, truck_tyres,
  tubeless_repair, vulcanizer, balancing, spooring, roadside_service
- created_at (timestamptz)
- updated_at (timestamptz)

### 2. users_profile
- id (UUID, PK, References Auth.Users)
- username (text, nullable)
- full_name (text, nullable)
- email (text, nullable)
- phone (text, nullable)
- avatar_url (text, nullable)
- updated_at (timestamptz)
- created_at (timestamptz)

### 3. reviews
- id (UUID, PK)
- workshop_id (UUID, FK → tambal_ban.id)
- user_id (UUID, FK → auth.users)
- rating (int, 1..5)
- comment (text)
- created_at (timestamptz)

---

## 5. UI/UX Design Direction

### Visual Style: "The Responsive Guardian"
- **Aesthetic**: Soft-Editorial Minimalism
- **Concept**: High-trust, calm interface with premium tactile feel
- **Glassmorphism**: Subtle overlays for bottom sheets and floating controls

### Color Scheme (Orchid Palette)
- **Primary**: #D672E1 (Emergency Orchid)
- **Primary Container**: #DA70D6
- **Surface**: #F8F9FA (Tonal Layering)
- **On Surface**: #191C1D (High legibility grey)
- **Secondary**: #DA70D6 (Action orchid)
- **Tertiary Container**: #D1E8D1 (Safety Green)

### Typography
- **Headings**: Plus Jakarta Sans (Bold/Semi-bold)
- **Body & Titles**: Inter (Regular/Medium)
- **Accessibility**: High contrast and 56dp minimum touch targets

### Key UI Components
1. **Shimmer Loading**: 5-item list skeletons for search results
2. **Bottom Sheet**: Overlaid search and detail view with rounded corners
3. **Emergency FAB**: Prominent orchid beacon for immediate assistance

---

## 6. Project Structure

```
app/src/main/java/com/tambal_ban/
├── auth/
│   ├── data/ (Profile, Auth Models)
│   ├── ui/ (Login, Register Activities)
│   └── viewmodel/
├── workshop/
│   ├── data/
│   │   ├── database/ (WorkshopDbHelper, Mappers)
│   │   └── Workshop.kt
│   ├── ui/ (MapFragment, DetailSheet)
│   └── viewmodel/
├── core/
│   ├── network/ (SupabaseService, ApiClient)
│   ├── di/ (Manual Injection)
│   └── utils/ (GeoUtils, Constants)
└── MainActivity.kt
```

---

## 7. Performance & Security

- **Spatial Optimization**: Indexing on latitude/longitude in SQLite
- **Network Efficiency**: Bounding box queries and OkHttp caching
- **Security**: Supabase Anon Key and EncryptedSharedPreferences
- **UI Performance**: Marker clustering and lazy image loading via Coil
