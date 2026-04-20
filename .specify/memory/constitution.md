<!--
Sync Impact Report:
- Version change: 1.0.0 → 1.1.0
- List of modified principles:
  - Design Consistency (Added)
- Added sections: N/A
- Removed sections: N/A
- Templates requiring updates (✅ updated): .specify/templates/plan-template.md, .specify/templates/spec-template.md, .specify/templates/tasks-template.md
-->

# Tambal Ban Finder Constitution

## Core Principles

### I. Simplicity First
The application must prioritize a simple and understandable architecture. Avoid unnecessary frameworks or complex abstractions. Follow YAGNI (You Ain't Gonna Need It) principles strictly: only implement functionality that is required for the current features. Rationale: Maintainability and speed of development are critical for this project.

### II. MVVM Architecture Enforcement
All Android code must strictly adhere to the Model-View-ViewModel (MVVM) architecture.
- **UI Layer**: Activities, Fragments, and XML layouts handle only UI presentation and user events.
- **Logic Layer**: ViewModels manage UI state and handle business logic.
- **Data Layer**: Repositories act as the single source of truth for data.
Network calls or direct database access must never be executed inside Activities or fragments.

### III. API-Driven Development
All application data must come from Supabase APIs. The mobile application must not contain hardcoded data for core features. All database interaction must be handled through Repositories, ensuring a clean separation between the network/database logic and the rest of the application.

### IV. Offline Safety
The application must fail gracefully when internet connectivity is unavailable. The UI must explicitly handle and display:
- **Empty states** when no data is returned.
- **Loading states** during network requests.
- **Error states** with user-friendly messages for failures.
The app must never crash due to network failures or unexpected API responses.

### V. Secure Authentication
Access control is enforced via Supabase Auth:
- **Authenticated Users**: Required for submitting workshops and writing reviews.
- **Anonymous Users**: May view the map, view workshop details, call workshops, and open navigation.
Auth tokens must be stored securely using Android's Keystore system or encrypted shared preferences.

### VI. Performance for Large Map Data
The map system must remain responsive even with 10,000+ workshop markers. Marker loading must be optimized by:
- Loading only essential marker data (ID, name, coordinate) initially.
- Using radius-based or viewport-based queries to limit data transfer.
- Implementing clustering or lazy loading for high-density areas.

### VII. Design Consistency (UI Standards)
All interactive icons, menu icons, and status indicators must adhere to a standard size of 20dp to ensure a uniform visual language across the application. Any exceptions must be documented and justified in the implementation plan.
Rationale: A consistent UI improves user recognition and maintains a premium, polished feel.

## Technology Constraints
The following technologies are mandatory. Any deviation requires a constitution amendment.

- **Mobile**: Kotlin 1.9+, Android SDK (Min SDK 24), XML layouts.
- **Architecture**: MVVM, Repository Pattern.
- **Networking**: Retrofit 2, OkHttp 4.
- **Backend/Auth**: Supabase (PostgreSQL, Supabase Auth).
- **Map System**: OpenStreetMap via osmdroid.
- **Forbidden**: Firebase (Core, Firestore, Auth), Google Maps SDK.

## Code Organization Rules
The project follows a strict directory structure to ensure separation of concerns:

- `data/`:
  - `api/`: Retrofit interfaces and network models.
  - `repository/`: Implementation of data repositories.
  - `model/`: Plain Kotlin objects for internal data representation.
- `ui/`:
  - `map/`: Map view, markers, and location logic.
  - `detail/`: Workshop details page.
  - `review/`: Review listing and submission.
  - `auth/`: Login, registration, and profile.
- `viewmodel/`: All ViewModel classes.
- `utils/`: Shared helper functions and constants.

**Rule**: No feature should mix UI logic with networking code. Repositories must be the single source of truth.

## Testing Policy
While 100% coverage is not mandatory, critical business logic must have automated tests:
- **Repository Logic**: Testing data mapping and error handling.
- **Submission Validation**: Ensuring workshop data entries are valid.
- **Authentication Logic**: Verifying login/logout flows and token handling.
- **Integration Tests**: Covering core API communication paths.

## Development Workflow
Every new feature or major modification must follow this sequential flow:
1. **Define API Contract**: Confirm Supabase table/API structure.
2. **Create Data Models**: Implement Kotlin models for the feature.
3. **Implement Repository**: Build the data fetching/storage logic.
4. **Implement ViewModel**: Connect repository data to UI state.
5. **Implement UI**: Build the XML layouts and Activity/Fragment logic.
Never start from the UI layer without the data layer being fully defined.

## Code Review Requirements
All generated and submitted code must respect:
- **MVVM Boundaries**: No business logic in UI, no UI references in ViewModels.
- **Repository Pattern**: No direct API calls outside repositories.
- **Null Safety**: Leverage Kotlin's type system to prevent NullPointerExceptions.
- **Kotlin Idiomatic Style**: Efficient use of Kotlin features (val/var, scope functions, extension functions).
- **Activity/Fragment Size**: Classes should focus on UI coordination; complex logic should be moved to ViewModels or use cases.

## Security Requirements
- **User Privacy**: Sensitive user data must never be logged or transmitted over unencrypted channels.
- **Credential Storage**: Auth tokens must be stored in secure storage.
- **API Security**: API keys must not be hardcoded in the primary repository. Use build-time configuration (BuildConfig).
- **Input Validation**: All user-submitted content must be validated client-side and server-side.

## Performance Requirements
- **Response Time**: UI must remain responsive during marker loading.
- **Data Usage**: Avoid fetching full workshop details when just displaying markers on a map.
- **Map Efficiency**: Support at least 10,000 workshop locations with minimal overhead.

## Governance
- **Precedence**: This constitution overrides all informal coding practices and patterns.
- **Compliance**: AI agents (Antigravity) and human developers must verify all design artifacts against these rules.
- **Amendments**: Major architectural changes or technology stack shifts require a formal update to this document.
- **Guidance**: The constitution serves as the primary guidance for long-term project maintenance.

**Version**: 1.1.0 | **Ratified**: 2026-03-15 | **Last Amended**: 2026-04-20
