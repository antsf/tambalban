# Tasks: Workshop Schema Update

## Phase 0: Supabase Migration (run BEFORE app deploy)

- [x] T000 [P1] Run SQL migration in Supabase SQL Editor:
  ```sql
  ALTER TABLE tambal_ban ADD COLUMN IF NOT EXISTS verified boolean DEFAULT true;
  ALTER TABLE tambal_ban ADD COLUMN IF NOT EXISTS image_url text;
  UPDATE tambal_ban SET verified = true WHERE source IS DISTINCT FROM 'user';
  CREATE INDEX IF NOT EXISTS tambal_ban_verified_idx ON tambal_ban(verified);
  ```
  Existing scraper rows → `verified = true`. New user submissions insert with `verified = false`.

## Phase 1: Model

- [x] T001 [P1] Update Workshop.kt — rename coords lat/lon; keep source/rating/totalReviews/imageUrl; add city, province, openingHours, verified; DROP: osm_id, shop_type, brand, website, osm_url, openTime, closeTime, is24h, ratingAvg, ratingCount (workshop/data/Workshop.kt)
- [x] T002 [P1] Update WorkshopSubmission.kt — add city (required), province (optional), openingHours (optional), imageUrl (optional); rename lat/lon; hardcode source="user", verified=false (workshop/data/WorkshopSubmission.kt)

## Phase 2: Network

- [x] T003 [P1] Update SupabaseService.kt:
  - All workshop `@GET` paths: `rest/v1/workshops` → `rest/v1/tambal_ban`
  - All GET queries: add `@Query("verified") verified: String = "eq.true"`
  - Bounding box params: rename `latitude`/`longitude` → `lat`/`lon`
  - `searchWorkshops`: replace single `name` query with PostgREST OR filter `@Query("or") or: String` (format: `(name.ilike.*q*,city.ilike.*q*)`)
  - `submitWorkshop` `@POST`: `rest/v1/workshop_submissions` → `rest/v1/tambal_ban`
  - Add `@PUT("storage/v1/object/workshops/{path}") suspend fun uploadWorkshopImage(@Path("path", encoded=true) path: String, @Body body: RequestBody): Response<Unit>` — content-type `image/jpeg`
  (core/network/SupabaseService.kt)

## Phase 3: Repository + Local DB

- [x] T004 [P1] Update WorkshopDbHelper.kt — add columns: city, province, opening_hours, source, rating, total_reviews, verified, image_url; rename lat/lon; remove old columns; bump DB_VERSION; onUpgrade: DROP TABLE + CREATE (workshop/data/database/WorkshopDbHelper.kt)
- [x] T005 [P1] Update WorkshopMapper.kt — sync toContentValues() + fromCursor() with new column names including image_url (workshop/data/database/mappers/WorkshopMapper.kt)
- [x] T006 [P1] Update WorkshopRepository.kt:
  - Fix bounding box param names to lat/lon
  - `searchWorkshops`: pass OR string `(name.ilike.*$query*,city.ilike.*$query*)`
  - Replace `submitWorkshop()` → `addWorkshop(submission: WorkshopSubmission)` that:
    1. If `submission.imageUri != null`: call `uploadImage(uri)` → get public CDN URL → set `submission.imageUrl`
    2. POST to tambal_ban with imageUrl in body
  - `uploadImage(uri: Uri): String` — reads bytes from ContentResolver, PUTs to `storage/v1/object/workshops/{userId}/{uuid}.jpg`, returns public URL `${SupabaseConfig.STORAGE_URL}/object/public/workshops/{userId}/{uuid}.jpg`
  - Local DB search fallback: query NAME OR CITY columns
  (workshop/data/WorkshopRepository.kt)
- [x] T007 [P1] Delete SubmissionRepository.kt — no longer needed; add-workshop goes through WorkshopRepository.addWorkshop() (workshop/data/SubmissionRepository.kt)

## Phase 4: ViewModel

- [x] T008 [P1] Update AddWorkshopViewModel.kt — new signature: `addWorkshop(name, address, city, lat, lon, phone, province?, openingHours?, imageUri?)`; validate required fields before calling repo; call WorkshopRepository.addWorkshop() (workshop/viewmodel/AddWorkshopViewModel.kt)
- [x] T009 [P2] Verify WorkshopDetailViewModel.kt compiles with new Workshop fields (workshop/viewmodel/WorkshopDetailViewModel.kt)

## Phase 5: UI

- [x] T010 [P1] Update WorkshopDetailActivity.kt — replace open/close time TextViews with opening_hours; add city + province row; show rating + total_reviews; load imageUrl as hero image via Coil (placeholder if null) (workshop/ui/WorkshopDetailActivity.kt)
- [x] T011 [P1] Update activity_workshop_detail.xml — remove time fields; add city/province TextViews; add opening_hours TextView; add total_reviews label; add hero `ImageView` (id: `ivHero`, 200dp height, scaleType centerCrop) at top of layout (app/src/main/res/layout/activity_workshop_detail.xml)
- [x] T012 [P1] Update AddWorkshopActivity.kt:
  - Add TambalTextField for city (required), province (optional), opening_hours (optional)
  - Add image picker: `ActivityResultLauncher` for `PickVisualMedia` (photo only); show selected image preview; store URI in ViewModel
  - Validate: name + address + city + phone + lat/lon non-empty before enabling submit
  - On success: show Snackbar "Terkirim, sedang ditinjau admin" → finish()
  (workshop/ui/AddWorkshopActivity.kt)
- [x] T013 [P1] Update activity_add_workshop.xml — add city (required), province (optional), opening_hours (optional) TambalTextField views; add image picker button + preview ImageView (100dp, gone until image selected) (app/src/main/res/layout/activity_add_workshop.xml)
- [x] T014 [P2] Add string resources: `label_city`, `label_province`, `label_opening_hours`, `hint_opening_hours`, `msg_submission_pending` = "Terkirim, sedang ditinjau admin", `label_total_reviews`, `btn_add_photo` = "Tambah Foto", `label_photo_optional` = "Foto (opsional)" (app/src/main/res/values/strings.xml)
- [x] T018 [P2] Update WorkshopListAdapter.kt — load `workshop.imageUrl` as thumbnail in list item via Coil (placeholder if null); update item_workshop.xml to add thumbnail ImageView (48dp) if not present (workshop/ui/WorkshopListAdapter.kt + app/src/main/res/layout/item_workshop.xml)

## Phase 6: Polish + Verify

- [x] T015 [P2] Update TambalBanApp.kt — remove submissionRepository singleton; AddWorkshopViewModel now resolves addWorkshop via workshopRepository (TambalBanApp.kt)
- [x] T016 [P1] Update CHANGELOG.md under ## [Unreleased]
- [x] T017 [P1] Build verification: `./gradlew assembleDebug`
