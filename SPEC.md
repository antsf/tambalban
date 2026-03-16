# TambalBan Finder - Specification Document

## 1. Project Overview

### Project Name
TambalBan Finder

### Project Type
Android Mobile Application

### Core Functionality
A mobile app that helps drivers quickly find the nearest tire repair shop (tambal ban) in Indonesia. Optimized for emergency roadside situations with a clean interface and light footprint.

---

## 2. Technology Stack & Choices

### Framework & Language
- **Language**: Kotlin 1.9.x
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35
- **Architecture**: MVVM (Model-View-ViewModel)

### Key Libraries/Dependencies
- **Maps**: osmdroid (OpenStreetMap)
- **Image Loading**: Glide
- **Backend**: Supabase (Database, Auth, Storage)
- **Ads**: Google AdMob
- **Networking**: HttpURLConnection (Core data) & Supabase SDK
- **JSON**: org.json

---

## 3. Feature List

- **Home Map**: Simplified view with AppBar, search and map only.
- **Workshop List**: Paginated list of all workshops sorted by distance.
- **Workshop Detail**: Redesigned page with photo carousel (max 3), reviews, and call/navigate buttons.
- **Photo System**: Compressed image uploads to Supabase Storage.
- **User Profile**: Track submissions and statuses (Pending/Approved/Rejected).
- **Navigation Drawer**: Central navigation menu using Bahasa Indonesia.
- **Rating System**: 1-5 star reviews by authenticated users.

---

## 4. UI/UX Design Direction

### Visual Style
- **Primary Color**: `#DA70D6` (Orchid)
- **Design System**: Material Design 3
- **Language**: Bahasa Indonesia

---

## 5. Project Structure

```
app/
├── data/
│   ├── api/          # Networking & Supabase
│   ├── database/     # SQLite local cache
│   └── repository/   # Data sync & logic
├── ui/
│   ├── main/         # Home screen
│   ├── list/         # All workshops
│   ├── detail/       # workshop detail
│   ├── profile/      # User profile
│   └── auth/         # Login & Register
└── utils/            # Geo, Image, Compression utils
```
