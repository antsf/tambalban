# TambalBan — Agent Context: Android Layout

Package → file mapping. Generated from `app/src/main/java/com/tambal_ban/`.

---

## Root

```
TambalBanApp.kt                    — Application class, manual service locator
                                     Holds: AuthPrefs, ApiClient, all Repositories
```

---

## auth/

```
auth/data/
    AuthModels.kt                  — LoginRequest, RegisterRequest, AuthResponse, AuthUser
    AuthRepository.kt              — login(), register(), logout()
    Profile.kt                     — Profile data class
    ProfileRepository.kt           — getProfile(), updateProfile(), uploadAvatar()

auth/viewmodel/
    LoginViewModel.kt              — login(), loginResult: LiveData, isLoading: LiveData
    RegisterViewModel.kt           — register(), registerResult: LiveData
    ProfileViewModel.kt            — loadProfile(), updateProfile(), profile: LiveData

auth/ui/
    LoginActivity.kt               — Email + password login, navigate to MainActivity on success
    RegisterActivity.kt            — Registration form
    ProfileActivity.kt             — View profile, navigate to EditProfile
    EditProfileActivity.kt         — Edit name, phone, avatar upload
```

---

## workshop/

```
workshop/data/
    Workshop.kt                    — Workshop data class (id, name, lat, lng, phone, rating, etc.)
    Review.kt                      — Review data class (id, workshopId, userId, rating, comment)
    WorkshopSubmission.kt          — User-submitted workshop data class
    WorkshopDetailUIState.kt       — Sealed class: Loading / Success(workshop, reviews) / Error
    WorkshopRepository.kt          — getWorkshopsInBounds(), searchWorkshops(), getWorkshopById()
    ReviewRepository.kt            — getReviews(workshopId), submitReview()
    SubmissionRepository.kt        — submitWorkshop(), getUserSubmissions()

workshop/data/database/
    WorkshopDbHelper.kt            — SQLiteOpenHelper, local cache for workshops
    mappers/WorkshopMapper.kt      — Workshop ↔ ContentValues / Cursor mapping

workshop/viewmodel/
    WorkshopDetailViewModel.kt     — loadWorkshop(id), workshop: LiveData<WorkshopDetailUIState>
    WorkshopListViewModel.kt       — loadWorkshops(), search(query), workshops: LiveData<List<Workshop>>
    AddWorkshopViewModel.kt        — submit(), submissionResult: LiveData

workshop/ui/
    WorkshopDetailActivity.kt      — Shows detail, reviews, call/navigate buttons
    WorkshopListActivity.kt        — Infinite scroll list, search bar
    AddWorkshopActivity.kt         — Form to submit new workshop with map pin
    ReviewAdapter.kt               — RecyclerView adapter for reviews list
    WorkshopListAdapter.kt         — RecyclerView adapter for workshop list
```

---

## map/

```
map/viewmodel/
    MainViewModel.kt               — loadNearbyWorkshops(lat, lng, radius), workshops: LiveData

map/ui/
    MainActivity.kt                — LAUNCHER. OSM map, workshop markers, search, drawer
    NearbyWorkshopAdapter.kt       — Bottom sheet list adapter for nearby workshops
    SearchSuggestionAdapter.kt     — AutoComplete suggestions adapter
```

---

## core/

```
core/network/
    SupabaseService.kt             — Retrofit interface: auth, workshops, reviews, submissions, profile
    ApiClient.kt                   — Singleton: getService(authPrefs): SupabaseService
    NetworkModule.kt               — OkHttpClient + Retrofit builder, JSON config
    AuthInterceptor.kt             — Adds apikey + Authorization headers, handles 401

core/ui/
    BaseActivity.kt                — AppCompatActivity + edge-to-edge + applySafeArea()
    TambalButton.kt                — Custom button (Primary/Secondary/Outlined + loading state)
    TambalTextField.kt             — Custom input (Text/Email/Password variants)
    AvatarView.kt                  — Profile image with Coil, edit button, progress overlay
    LiveStatusDrawer.kt            — BottomSheetDialogFragment for workshop open/close status

core/utils/
    AuthPrefs.kt                   — EncryptedSharedPreferences wrapper for JWT tokens
    SupabaseConfig.kt              — URL + ANON_KEY constants
    Constants.kt                   — App-wide constants
    GeoUtils.kt                    — Distance calculations, coordinate helpers
    MapUtils.kt                    — osmdroid marker helpers
    IntentUtils.kt                 — Cross-feature navigation helpers (Intent.setClassName)
    AuthErrorMapper.kt             — Maps Supabase error codes to user-friendly strings

core/location/
    LocationService.kt             — FusedLocationProviderClient wrapper

core/ads/
    AdMobManager.kt                — AdMob banner + native ad setup
```

---

## Tests

```
app/src/test/java/com/tambal_ban/
    auth/viewmodel/
        RegisterViewModelTest.kt   — Existing: register success/failure/loading tests
```
