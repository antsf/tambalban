# Tasks: Add Workshop from Edit Profile

**Branch**: `018-add-workshop` | **Input**: `specs/018-add-workshop/spec.md`

## Phase 1: Activity + ViewModel Setup

- [ ] T001 [P] Create `app/src/main/java/com/tambal_ban/auth/ui/AddWorkshopActivity.kt` extending BaseActivity with ViewBinding (ActivityAddWorkshopBinding), inflate activity_add_workshop.xml, setup toolbar
- [ ] T002 Create `app/src/main/java/com/tambal_ban/auth/viewmodel/AddWorkshopFormState.kt` data class with form fields (name, address, city, phone, province, openingHours, lat, lon, selectedImageUri, isLoadingLocation, locationError)
- [ ] T003 [P] Update `app/src/main/java/com/tambal_ban/auth/viewmodel/ProfileViewModel.kt`: add submitWorkshop() method, fetchCurrentLocation(), updateFormField(); track form state + submission result via LiveData

## Phase 2: Form Wiring + Location

- [ ] T004 [P] Wire Activity form fields to observe ViewModel formState LiveData; on text change call updateFormField()
- [ ] T005 [P] Implement form field validation in ViewModel: required checks, phone format, lat/lon range; expose errors via LiveData
- [ ] T006 Wire "Current Location" button click: call viewModel.fetchCurrentLocation(), show loading state, update lat/lon fields on success, show toast on error
- [ ] T007 Implement ViewModel.fetchCurrentLocation(): call LocationService.getLastKnownLocation(), fallback to Jakarta default on error

## Phase 3: Image + Submit

- [ ] T008 [P] Register ActivityResultLauncher for GetContent (photo picker); store Uri in ViewModel, show preview in ivPhotoPreview
- [ ] T009 Wire submit button: validate form, call viewModel.submitWorkshop(formState), disable button during loading
- [ ] T010 Implement ViewModel.submitWorkshop(): create WorkshopSubmission, call WorkshopRepository.addWorkshop(), update submissionResult LiveData

## Phase 4: Success Flow + Integration

- [ ] T011 Wire success callback: observe submissionResult, show Snackbar "Terkirim, sedang ditinjau admin", finish() after 2s
- [ ] T012 Wire error handling: observe failure, show Toast, keep form visible for retry
- [ ] T013 [P] Update `app/src/main/java/com/tambal_ban/auth/ui/EditProfileActivity.kt`: add "Tambah Tambal Ban" button/menu, launch Intent to AddWorkshopActivity
- [ ] T014 [P] Update `app/src/main/res/layout/activity_edit_profile.xml`: add button/menu item if needed

## Phase 5: Verify

- [ ] T015 Run `./gradlew compileDebugKotlin`; verify zero errors
- [ ] T016 Run `./gradlew assembleDebug`; verify APK builds
- [ ] T017 Update `CHANGELOG.md`: document "Add Workshop from Edit Profile" feature
- [ ] T018 Manual E2E test: tap menu → form opens → fill fields → submit → snackbar → back to Edit Profile

**MVP Scope**: All tasks (Phases 1-5)  
**Time**: 3-4 hours (single dev)
