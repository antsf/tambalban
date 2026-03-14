# TambalBan Finder - Specification Document

## 1. Project Overview

### Project Name
TambalBan Finder

### Project Type
Android Mobile Application

### Core Functionality
A mobile app that helps drivers quickly find the nearest tire repair shop (tambal ban) during emergency situations such as flat tires. The app uses OpenStreetMap with osmdroid library to display workshop locations, supports offline caching, and includes an emergency mode for quick access to the closest workshop.

---

## 2. Technology Stack & Choices

### Framework & Language
- **Language**: Kotlin 1.9.x
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **Architecture Components**: ViewModel, LiveData, Room
- **Dependency Injection**: Manual (Repository pattern)

### Key Libraries/Dependencies

| Category | Library | Version |
|----------|---------|---------|
| Networking | Retrofit | 2.9.0 |
| Networking | OkHttp | 4.12.0 |
| Database | Room | 2.6.1 |
| Maps | osmdroid | 6.1.18 |
| Maps Clustering | osmdroid-mapsforge | (via osmdroid) |
| Location | Google Play Services Location | 21.1.0 |
| Ads | Google Mobile Ads SDK | 23.0.0 |
| Image Loading | Coil | 2.5.0 |
| Coroutines | Kotlin Coroutines | 1.7.3 |
| JSON | Gson | 2.10.1 |
| ViewModel | AndroidX Lifecycle | 2.7.0 |

### State Management
- LiveData for reactive UI updates
- ViewModel for UI state management
- Repository pattern for data access

### Backend
- **Platform**: Supabase (PostgreSQL)
- **Authentication**: Anonymous (no auth required for basic usage)
- **API Style**: REST via Supabase REST API

---

## 3. Feature List

### Core Features

1. **Map Screen**
   - Display map using osmdroid library
   - Show user location with GPS
   - Load workshop markers from backend API
   - Support 10,000+ markers with clustering
   - Load only markers within map viewport (bounding box queries)
   - Cluster markers when zoomed out

2. **Nearby Search**
   - Auto-detect GPS location
   - Find nearest workshops using Haversine formula
   - Filter by radius: 1 km, 3 km, 5 km

3. **Workshop Detail Screen**
   - Display workshop name, address, phone, distance, rating
   - Call workshop button (intent to dialer)
   - Navigate button (intent to Google Maps/Waze)

4. **Add Workshop**
   - Form to submit new workshop data
   - Fields: name, phone, address, location (lat/lng)
   - Store submissions to backend

5. **Offline Support**
   - Cache workshop data in Room database
   - Load cached workshops when network unavailable
   - Sync when connection restored

6. **Emergency Mode**
   - Floating emergency button on map
   - When pressed: show closest workshop within 3km
   - Quick action buttons (Call, Navigate)

7. **Monetization**
   - Banner ads at bottom of screens
   - Native ads in workshop list
   - No ads during emergency actions (call/navigate)

### Data Tables (Supabase)

1. **workshops**
   - id (UUID, PK)
   - name (text)
   - latitude (double)
   - longitude (double)
   - phone (text)
   - address (text)
   - open_time (text, nullable)
   - close_time (text, nullable)
   - is_24h (boolean)
   - rating_avg (double)
   - rating_count (int)
   - source (text)
   - created_at (timestamp)

2. **reviews**
   - id (UUID, PK)
   - workshop_id (UUID, FK)
   - user_id (UUID)
   - rating (int)
   - comment (text)
   - created_at (timestamp)

3. **users**
   - id (UUID, PK)
   - created_at (timestamp)

4. **workshop_submissions**
   - id (UUID, PK)
   - name (text)
   - phone (text)
   - address (text)
   - latitude (double)
   - longitude (double)
   - status (text: pending/approved/rejected)
   - created_at (timestamp)

5. **workshop_reports**
   - id (UUID, PK)
   - workshop_id (UUID, FK)
   - user_id (UUID)
   - reason (text)
   - created_at (timestamp)

---

## 4. UI/UX Design Direction

### Overall Visual Style
- Material Design 3
- Clean, minimalist interface focused on functionality
- High contrast for visibility in outdoor/emergency conditions
- Large touch targets for quick interaction

### Color Scheme
- **Primary**: #FF5722 (Deep Orange - emergency/tire repair association)
- **Primary Variant**: #E64A19
- **Secondary**: #2196F3 (Blue - trust/navigation)
- **Background**: #FFFFFF
- **Surface**: #F5F5F5
- **Error**: #B00020
- **On Primary**: #FFFFFF
- **On Secondary**: #FFFFFF

### Layout Approach
- Single-screen focused design with map as main interface
- Bottom sheet for workshop details
- Floating action buttons for emergency mode
- Bottom navigation for minimal menu (Map, Search, Add, Settings)

### Key UI Components

1. **Main Map Screen**
   - Full-screen map with osmdroid
   - Floating emergency button (FAB, red/orange)
   - Bottom sheet showing nearest workshop
   - Search radius filter chips (1km, 3km, 5km)
   - My Location button

2. **Workshop Detail Bottom Sheet**
   - Workshop name (large)
   - Address and distance
   - Rating stars + count
   - Call button (primary action)
   - Navigate button (secondary action)
   - Report button (tertiary)

3. **Add Workshop Screen**
   - Form with text fields
   - Location picker on map
   - Submit button

4. **Emergency Mode UI**
   - Large, prominent floating button
   - Quick view card showing closest workshop
   - One-tap call functionality
   - One-tap navigation functionality

### Ad Placement Strategy
- **Banner**: Bottom of screen (below map controls)
- **Native Ads**: In workshop list/recycler view
- **Disabled**: During active call navigation, emergency mode actions

---

## 5. Project Structure

```
app/
├── data/
│   ├── api/
│   │   ├── ApiClient.kt
│   │   ├── SupabaseApi.kt
│   │   └── ApiModels.kt
│   ├── model/
│   │   └── Workshop.kt
│   └── repository/
│       └── WorkshopRepository.kt
├── database/
│   ├── AppDatabase.kt
│   ├── dao/
│   │   └── WorkshopDao.kt
│   └── entity/
│       └── WorkshopEntity.kt
├── ui/
│   ├── main/
│   │   ├── MainActivity.kt
│   │   └── MainViewModel.kt
│   ├── map/
│   │   ├── MapFragment.kt
│   │   └── MapViewModel.kt
│   ├── detail/
│   │   ├── WorkshopDetailActivity.kt
│   │   └── WorkshopDetailViewModel.kt
│   └── add/
│       ├── AddWorkshopActivity.kt
│       └── AddWorkshopViewModel.kt
├── location/
│   └── LocationService.kt
├── ads/
│   └── AdMobManager.kt
├── utils/
│   ├── Constants.kt
│   ├── HaversineUtils.kt
│   └── IntentUtils.kt
└── res/
    ├── layout/
    ├── values/
    └── drawable/
```

---

## 6. API Endpoints (Supabase REST)

| Endpoint | Method | Description |
|----------|--------|-------------|
| /rest/v1/workshops | GET | Get all workshops (with bounding box filters) |
| /rest/v1/workshops | POST | Create new workshop |
| /rest/v1/workshops?id=eq:{id} | GET | Get workshop by ID |
| /rest/v1/workshop_submissions | GET | Get submissions |
| /rest/v1/workshop_submissions | POST | Submit new workshop |

---

## 7. Performance Requirements

- Support 10,000+ workshop markers
- Use bounding box queries to load only visible markers
- Implement marker clustering for zoomed-out views
- Cache data in Room for offline access
- Lazy loading for images and details
- Efficient map tile loading
