# Tasks: TambalBan Finder (V2 Upgrade)

## Phase 9: UI Overhaul & Theme Update
- [ ] T034 [P] Update colors.xml and themes.xml with `#DA70D6` primary color and Material 3 styles
- [ ] T035 [P] Implement rounded input field styles in res/drawable/ and res/values/styles.xml
- [ ] T036 Refactor MainActivity layout to a simplified AppBar + Search + Map structure

## Phase 10: Navigation & Drawer
- [ ] T037 Create drawer_menu.xml with items: Map, Semua Tambal Ban, Profile, Login/Register
- [ ] T038 Implement DrawerLayout and NavigationView in activity_main.xml
- [ ] T039 Wire drawer fragment/activity navigation in MainActivity.kt

## Phase 11: Workshop List & Pagination (Semua Tambal Ban)
- [ ] T040 Create activity_all_workshops.xml with RecyclerView and FAB for Add Workshop
- [ ] T041 Implement AllWorkshopsViewModel with paginated fetch from WorkshopRepository
- [ ] T042 Implement workshop_item.xml layout and AllWorkshopsAdapter
- [ ] T043 Add distance sorting logic in WorkshopRepository.kt if location is available

## Phase 12: Photo System & Glide
- [ ] T044 Integrate Glide dependency in app/build.gradle
- [ ] T045 Create ImageCompressionUtils.kt (1280px, JPEG 70%)
- [ ] T046 Implement Supabase Storage upload/download logic in NetworkClient.kt
- [ ] T047 Implement photo gallery/carousel in activity_workshop_detail.xml

## Phase 13: Auth & Persistence
- [ ] T048 Implement Login and Register layouts with modern card design
- [ ] T049 Implement AuthViewModel and use SharedPreferences for session persistence
- [ ] T050 Fix login redirect logic to return to intended destination (Add Workshop)

## Phase 14: Workshop Detail & Reviews
- [ ] T051 Redesign activity_workshop_detail.xml with carousel and Reviews section
- [ ] T052 Implement review submission and retrieval in WorkshopRepository.kt
- [ ] T053 Update Workshop entity to include rating_avg and rating_count

## Phase 15: Submission & Edit Logic (Admin Verified)
- [ ] T054 Update Workshop submission logic: set verified = false, status = pending
- [ ] T055 Update Map query to only fetch verified = true workshops
- [ ] T056 Implement "My Submissions" list in UserProfileActivity

## Phase 16: AdMob Integration (List & Detail)
- [ ] T057 Add Banner Ad containers to activity_all_workshops.xml and activity_workshop_detail.xml
- [ ] T058 Implement AdMob loading in AllWorkshopsActivity and WorkshopDetailActivity
- [ ] T059 Remove AdMob from Map screen if still present
