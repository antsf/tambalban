# Tasks: Add Tambal Ban from Profile

## Phase 1: UI

- [ ] T001 [P2] Create AddWorkshopDialogFragment.kt — BottomSheetDialogFragment with form fields (name, address, city, phone, province, opening_hours, photo) (auth/ui/AddWorkshopDialogFragment.kt)

- [ ] T002 [P2] Create fragment_add_workshop_dialog.xml — form layout with TextInputLayouts for name/address/city/phone/province/opening_hours, image picker button, submit/cancel buttons (app/src/main/res/layout/fragment_add_workshop_dialog.xml)

## Phase 2: ViewModel + Logic

- [ ] T003 [P2] Update ProfileViewModel.kt — add `submitWorkshop()` method that calls WorkshopRepository.addWorkshop(); track loading/result states (auth/viewmodel/ProfileViewModel.kt)

- [ ] T004 [P2] Update ProfileActivity.kt — add "Add Tambal Ban" button; launch AddWorkshopDialogFragment on click (auth/ui/ProfileActivity.kt)

## Phase 3: Integration

- [ ] T005 [P2] Update activity_profile.xml — add button (auth/ui/ProfileActivity.kt)

- [ ] T006 [P2] Test: submit workshop from profile, verify appears in map after admin verify

## Phase 4: Verify

- [ ] T007 [P1] Build verification: `./gradlew compileDebugKotlin`
