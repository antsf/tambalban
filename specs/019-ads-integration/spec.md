# AdMob Ads Integration

## Overview
Display AdMob banner ads on the two most-visited screens: MainActivity (map home) and WorkshopListActivity. AdMob infrastructure already exists (initialized, manager class, ad unit IDs) but no ads are rendered on any screen.

## User Stories
- [P1] As a user, I can see banner ads on the home screen without obstructing the map or workshop list
- [P1] As a user, I can see banner ads on the workshop list screen
- [P2] As a developer, ad lifecycle is properly handled (pause/resume/destroy)

## Functional Requirements
- FR-001: MainActivity shows a banner ad at the bottom of the screen below the bottom sheet
- FR-002: WorkshopListActivity shows a banner ad at the bottom of the screen below the RecyclerView
- FR-003: Banner ads respect loading/error states (hidden on load failure)
- FR-004: Ad lifecycle (pause in onPause, resume in onResume, destroy in onDestroy)

## Assumptions
- Ad unit IDs in Constants.kt are production-ready (already set)
- Banner ads only, no native/interstitial for this phase

## Out of Scope
- Native ads in workshop list items
- Interstitial ads between screens
- Ad revenue tracking or analytics
