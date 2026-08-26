# Tasks: AdMob Native Ad — Workshop List

## Phase 1: Layout
- [X] T001 [P1] Restyle ad_native.xml to match workshop card visual style — use
  MaterialCardView wrapper, app theme colors instead of hardcoded android:color/white and
  android:color/black, add "Iklan" label TextView with id @+id/ad_label
  (app/src/main/res/layout/ad_native.xml)
- [X] T002 [P1] Fix adContainer overlap in activity_workshop_list.xml — add
  android:id="@+id/adContainer" as a top constraint anchor for the RecyclerView
  (rvWorkshops layout_constraintBottom_toTopOf="@id/adContainer"), and add bottom padding
  to RecyclerView so content does not hide under the banner
  (app/src/main/res/layout/activity_workshop_list.xml)

## Phase 2: Adapter Refactor
- [X] T003 [P1] Refactor WorkshopListAdapter to support two view types (VIEW_TYPE_WORKSHOP = 0,
  VIEW_TYPE_NATIVE_AD = 1) — implement getItemViewType() returning ad type at positions
  that are multiples of 5 (e.g. position % 5 == 4), workshop type otherwise
  (app/src/main/java/com/tambal_ban/workshop/ui/WorkshopListAdapter.kt)
- [X] T004 [P1] Add NativeAdViewHolder inner class to WorkshopListAdapter — inflates
  ad_native.xml, holds a NativeAdView reference, and exposes a bind(NativeAd?) method
  that calls AdMobManager.populateNativeAdView() when ad is non-null or sets visibility
  GONE when null
  (app/src/main/java/com/tambal_ban/workshop/ui/WorkshopListAdapter.kt)
- [X] T005 [P1] Add a MutableList<NativeAd?> nativeAds field to WorkshopListAdapter
  initialized to an empty list — sized to match the number of ad slots in the current
  dataset; expose fun updateNativeAd(slotIndex: Int, ad: NativeAd?) to update a slot and
  call notifyItemChanged() on the corresponding adapter position
  (app/src/main/java/com/tambal_ban/workshop/ui/WorkshopListAdapter.kt)
- [X] T006 [P1] Update getItemCount() to return workshops.size + adSlotCount where
  adSlotCount = workshops.size / 5; update onBindViewHolder() to map adapter position to
  the correct workshop index (workshopIndex = position - position / 5) for WORKSHOP type
  and to slotIndex (position / 5 - 1) for NATIVE_AD type
  (app/src/main/java/com/tambal_ban/workshop/ui/WorkshopListAdapter.kt)
- [X] T007 [P1] Expose fun destroyNativeAds() on WorkshopListAdapter that iterates
  nativeAds and calls nativeAd.destroy() on each non-null entry and clears the list
  (app/src/main/java/com/tambal_ban/workshop/ui/WorkshopListAdapter.kt)

## Phase 3: Activity Wiring
- [X] T008 [P1] In WorkshopListActivity.setupObservers() workshop observer — after
  adapter.submitList(workshops), calculate adSlotCount = workshops.size / 5, then for
  each slot index call adMobManager.loadNativeAd { nativeAd -> adapter.updateNativeAd(slotIndex, nativeAd) }
  (app/src/main/java/com/tambal_ban/workshop/ui/WorkshopListActivity.kt)
- [X] T009 [P1] In WorkshopListActivity.onDestroy() call adapter.destroyNativeAds() before
  the existing adMobManager.destroyBannerAd() call
  (app/src/main/java/com/tambal_ban/workshop/ui/WorkshopListActivity.kt)

## Phase 4: Strings
- [X] T010 [P2] Add string resource ad_label = "Iklan" to strings.xml and reference it
  from ad_native.xml ad_label TextView (rather than hardcoding)
  (app/src/main/res/values/strings.xml)

## Phase 5: Polish
- [X] T011 [P2] Update CHANGELOG.md with entry for native ads feature
- [X] T012 [P1] Build verification: ./gradlew assembleDebug
