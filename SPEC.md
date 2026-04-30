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
   - Review/Approval workflow via `workshop_submissions` table

6. **Offline Support**
   - Cache workshop data in local SQLite database
   - Spatial queries on local data when network is unavailable
   - Seamless transition between Remote and Local data sources

---

## 4. Data Tables (Supabase)

### 1. workshops
- id (UUID, PK)
- name (text)
- latitude (double)
- longitude (double)
- phone (text, nullable)
- address (text, nullable)
- open_time (text, nullable)
- close_time (text, nullable)
- is_24h (boolean)
- rating_avg (double)
- rating_count (int)
- image_url (text, nullable)
- verified (boolean, default false)
- source (text)
- created_at (timestamp)

### 2. users_profile
- id (UUID, PK, References Auth.Users)
- full_name (text, nullable)
- email (text, nullable)
- phone (text, nullable)
- avatar_url (text, nullable)
- updated_at (timestamp)

### 3. reviews
- id (UUID, PK)
- workshop_id (UUID, FK)
- user_id (UUID, FK)
- rating (int)
- comment (text)
- created_at (timestamp)

### 4. workshop_submissions
- id (UUID, PK)
- name (text)
- phone (text)
- address (text)
- latitude (double)
- longitude (double)
- user_id (UUID, FK)
- status (text: pending/approved/rejected)
- created_at (timestamp)

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
