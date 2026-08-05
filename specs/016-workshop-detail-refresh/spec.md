# Feature Specification: Workshop Detail UI Refresh

> **SUPERSEDED SCHEMA:** this spec references the retired `workshops` table. The live
> shared table is `tambal_ban` — see [`017-workshop-schema-update`](../017-workshop-schema-update/spec.md).

**Feature Branch**: `016-workshop-detail-refresh`  
**Created**: 2026-05-01  
**Status**: Draft  
**Input**: User description: "Update tampilan WorkshopDetailActivity sesuai desain premium: header image dengan badge status, container rata, tombol Call & Navigate jadi floating button dengan label dan icon, list detail dengan label kategori (Alamat, Telepon, Jam Operasional), dan penyesuaian semua label ke Bahasa Indonesia."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visual Experience & Information Clarity (Priority: P1)

As a user looking for a tire repair shop, I want to see a premium and clear detail view of the workshop so that I can quickly identify its status, location, and how to contact them.

**Why this priority**: This is the core purpose of the detail screen. Visual clarity and immediate access to actions (Call/Navigate) are critical for users in emergency situations.

**Independent Test**: Can be tested by opening any workshop detail from the map/list and verifying that all visual elements (image, status badge, actions, and details) are displayed correctly and are fully localized.

**Acceptance Scenarios**:

1. **Given** I am on the Map or List, **When** I click on a workshop, **Then** I see the new detail screen with a large header image and a "BUKA SEKARANG" or "TUTUP" badge.
2. **Given** I am on the detail screen, **When** I look at the information list, **Then** I see localized labels like "ALAMAT LENGKAP", "NOMOR TELEPON", and "JAM OPERASIONAL" in Indonesian.

---

### User Story 2 - Immediate Action (Priority: P2)

As a user in need of assistance, I want to have prominent buttons to call or navigate to the workshop so that I can get help without searching for buttons.

**Why this priority**: High value for emergency use cases.

**Independent Test**: Can be tested by tapping the "Telepon" or "Navigasi" buttons and ensuring they trigger the respective system intents (Dialer or Maps).

**Acceptance Scenarios**:

1. **Given** I am on the detail screen, **When** I tap the "Telepon" button, **Then** the phone dialer opens with the workshop's number.
2. **Given** I am on the detail screen, **When** I tap the "Navigasi" button, **Then** the Google Maps (or default map app) opens with directions to the workshop.

### Edge Cases

- **Missing Image**: If the workshop doesn't have an `image_url`, the system should show a high-quality placeholder image that maintains the premium feel.
- **Missing Phone/Address**: The layout must handle null values gracefully without breaking the alignment of other items.
- **Long Names/Addresses**: Text should wrap correctly without overlapping icons or other labels.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Header MUST display a full-width image of the workshop.
- **FR-002**: Status badge MUST be overlaid on the image (e.g., "BUKA SEKARANG" in green or "TUTUP" in red).
- **FR-003**: Action area MUST contain two side-by-side buttons for "Telepon" and "Navigasi" with icons and labels.
- **FR-004**: Workshop information MUST be presented as a vertical list with the following localized sections:
    - **ALAMAT LENGKAP**
    - **NOMOR TELEPON**
    - **JAM OPERASIONAL**
- **FR-005**: Each section in the list MUST have a descriptive icon aligned to the left.
- **FR-006**: All UI text MUST be in Indonesian.

### Key Entities *(include if feature involves data)*

- **Workshop**: Existing entity containing name, address, phone, latitude, longitude, and open/close times.
- **Status**: Derived state (Open/Closed) based on current time and workshop's operational hours.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All static labels and hints on the screen are 100% in Indonesian.
- **SC-002**: Primary action buttons (Call/Navigate) meet the 56dp touch target requirement.
- **SC-003**: Header image loads within 2 seconds on a standard 4G connection.
- **SC-004**: UI layout remains consistent on screens from 5" to 7" (no overlapping or cut-off text).
