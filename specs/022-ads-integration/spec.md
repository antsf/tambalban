# AdMob Ads Integration — Native Ad in Workshop List

## Overview
Banner ads are already fully wired in MainActivity and WorkshopListActivity (implemented
in 019-ads-integration). This feature completes the ad integration plan by inserting
native ads inline in the WorkshopListActivity RecyclerView as a card every 5 workshop
items. It also fixes a visual bug where the existing banner ad container in
WorkshopListActivity overlaps the RecyclerView bottom because both are constrained to
the same parent edge.

## User Stories
- [P1] As a user, I see a native ad card every 5 items while scrolling the workshop list
  so that the app generates revenue without blocking core content
- [P1] As a user, the banner ad at the bottom of the workshop list does not clip the last
  item in the list
- [P2] As a user, native ad cards are visually distinct from workshop cards (labeled "Ad")
  so I am not misled
- [P3] As a developer, native ad objects are properly destroyed when the activity is
  destroyed to prevent memory leaks

## Functional Requirements
- FR-001: WorkshopListAdapter supports two view types: WORKSHOP (0) and NATIVE_AD (1)
- FR-002: A native ad card is inserted at every position that is a multiple of 5 (positions
  5, 10, 15, …) in the combined list; workshop items fill all other positions
- FR-003: AdMobManager.loadNativeAd() is called once per ad slot that becomes visible;
  individual NativeAd references are held by the adapter, not AdMobManager
- FR-004: AdMobManager.populateNativeAdView() populates each NativeAdView using ad_native.xml
- FR-005: RecyclerView bottom padding in activity_workshop_list.xml is increased so the
  last item is not obscured by the banner adContainer
- FR-006: All loaded NativeAd objects are destroyed in WorkshopListActivity.onDestroy()
- FR-007: Native ad card displays an "Iklan" label (strings.xml) so the ad is identifiable
- FR-008: If native ad fails to load, the ad slot is hidden (View.GONE) rather than
  showing a blank card

## Assumptions
- ⚠️ ASSUMPTION: One native ad load per visible slot is acceptable; no pre-fetching pool
  is required for this phase

## Out of Scope
- Native ads in MainActivity bottom sheet list (NearbyWorkshopAdapter)
- Interstitial or rewarded ads
- Ad frequency capping beyond the fixed every-5-items rule
- Ad revenue analytics or reporting
