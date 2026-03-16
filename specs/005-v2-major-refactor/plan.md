# Implementation Plan: Tambal Ban Finder (V2 Upgrade)

**Branch**: `005-v2-major-refactor` | **Date**: 2026-03-16 | **Spec**: [spec.md](spec.md)

## Summary

This plan outlines the major upgrade to Tambal Ban Finder V2, focusing on a modern UI overhaul, enhanced map behavior, a robust photo system using Supabase Storage, and improved user authentication/profiles. We will maintain the lightweight core while integrating Glide for premium image handling.

## Technical Context

**Language/Version**: Kotlin 1.9+, Android SDK 35+
**Primary Dependencies**:
- **Maps**: osmdroid 6.x
- **Image Loading**: Glide (Requested for smooth scrolling/caching)
- **Monetization**: Google AdMob
- **Backend**: Supabase (Database, Auth, Storage)
- **UI Components**: Material Design 3, ViewBinding

**Built-in/Lightweight Components**:
- **Networking**: `HttpURLConnection` (Core data) & Supabase SDK (Auth/Storage)
- **Database**: `SQLiteOpenHelper` (Local caching)
- **JSON Parsing**: `org.json`
- **Session**: `SharedPreferences`

## Project Structure

```text
app/src/main/java/com/tambal_ban/
├── data/
│   ├── api/          # Networking & Supabase
│   ├── database/     # SQLite local cache
│   ├── model/        # Workshop, Review, User, WorkshopPhoto
│   └── repository/   # Data sync & logic
├── ui/
│   ├── main/         # Home screen
│   ├── list/         # All workshops
│   ├── detail/       # Workshop detail
│   ├── profile/      # User profile
│   └── auth/         # Login & Register
└── utils/            # Geo, Image, Compression utils
```
