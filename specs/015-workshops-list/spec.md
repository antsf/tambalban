# Feature Specification: Workshops List Screen

> **SUPERSEDED SCHEMA:** this spec references the retired `workshops` table (`latitude` /
> `longitude`). The live shared table is `tambal_ban` (`lat` / `lon`) — see
> [`017-workshop-schema-update`](../017-workshop-schema-update/spec.md).

**Feature ID**: 015-workshops-list
**Status**: [DONE] (Retrospective)
**Priority**: P1
**Owner**: Antigravity

## Context & Problem Statement
Users need a way to view all nearby workshops in a clean, scrollable list format rather than just as pins on a map. This improves discoverability and allows users to compare distances, ratings, and status at a glance.

## User Stories
- **As a User**, I want to see a list of workshops near my current location.
- **As a User**, I want to see the distance to each workshop.
- **As a User**, I want to see which workshops are verified and their current rating.
- **As a User**, I want to see a shimmer loading effect while data is being fetched.

## Functional Requirements
- [x] **FR1: Nearby Fetching**: Fetch workshops within a 5km radius of the user's location.
- [x] **FR2: List Presentation**: Display workshops in a MaterialCardView list.
- [x] **FR3: Shimmer Effect**: Show a skeleton loading state for at least 5 items while fetching.
- [x] **FR4: Empty State**: Display a clear "No workshops found" message if the radius returns zero results.
- [x] **FR5: Navigation**: Tapping a list item must navigate to the `WorkshopDetailActivity`.
- [ ] **FR6: Entry Point**: "Lihat Semua" button on `MainActivity` bottom sheet must navigate to `WorkshopListActivity`.

## API Contracts
- **Endpoint**: `GET /rest/v1/workshops`
- **Parameters**: `verified=eq.true`, `latitude=gte.{minLat}&lte.{maxLat}`, `longitude=gte.{minLng}&lte.{maxLng}`
- **Repository**: `WorkshopRepository.getNearbyWorkshops()`

## Design Alignment
- **Theme**: Orchid Palette (#D672E1)
- **Typography**: Plus Jakarta Sans for titles, Inter for body.
- **Icons**: 20dp standard icons for back navigation and status.

## Success Criteria
- [x] Average loading time under 2 seconds.
- [x] Shimmer effect remains smooth (60fps).
- [x] Zero crashes during network transitions (Offline Safety).
