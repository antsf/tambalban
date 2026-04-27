# Specification Clarification: Internal Feature Structure

**Feature ID**: 014-modular-structure (Refinement)
**Current Status**: Modularized by Feature Package

## Clarification Questions

1. **Sub-folder Naming**: Untuk masing-masing fitur (auth, workshop, map), folder apa saja yang ingin dibuat? Apakah standar `ui`, `viewmodel`, dan `data`?
2. **Data Layer Placement**: Apakah model dan repository juga masuk ke folder `data` di dalam fitur tersebut?
3. **Core Reorganization**: Apakah paket `core/` juga perlu dikelompokkan lagi, atau struktur saat ini (`network`, `ui`, `utils`, dll) sudah cukup?
4. **Locality Rule**: Apakah tetap ingin Activity dan ViewModel berada di folder yang sama (untuk *locality*), atau dipisah ke folder `ui` dan `viewmodel` yang berbeda?

## Proposed Internal Structure Example

```text
com.tambal_ban.auth/
├── ui/              (LoginActivity, RegisterActivity, ProfileActivity)
├── viewmodel/       (LoginViewModel, RegisterViewModel, ProfileViewModel)
└── data/            (AuthRepository, AuthModels, Profile)
```

Mohon konfirmasi struktur yang diinginkan.
