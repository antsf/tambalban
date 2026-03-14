# Data Model: TambalBan Finder

## Core Entities

### Workshop
Represents a tire repair shop.
- `id`: UUID (Primary Key)
- `name`: String (Not Null)
- `latitude`: Double (Not Null, PostGIS geography point)
- `longitude`: Double (Not Null, PostGIS geography point)
- `phone`: String (Nullable)
- `address`: String (Not Null)
- `open_time`: Time (Nullable)
- `close_time`: Time (Nullable)
- `is_24h`: Boolean (Default: false)
- `rating_avg`: Float (Default: 0.0)
- `rating_count`: Integer (Default: 0)
- `source`: String (e.g., "manual", "google_maps_import")

### Review
User feedback for a workshop.
- `id`: UUID (Primary Key)
- `workshop_id`: UUID (Foreign Key -> Workshop.id)
- `user_id`: UUID (Foreign Key -> User.id)
- `rating`: Integer (1-5)
- `comment`: Text (Nullable)
- `created_at`: Timestamp (Current Time)

### User
Application user profile.
- `id`: UUID (Primary Key)
- `email`: String (Unique, Not Null)
- `name`: String (Not Null)
- `created_at`: Timestamp (Current Time)

### WorkshopSubmission
A user's contribution to add a new workshop.
- `id`: UUID (Primary Key)
- `name`: String (Not Null)
- `phone`: String (Nullable)
- `address`: String (Not Null)
- `latitude`: Double (Not Null)
- `longitude`: Double (Not Null)
- `user_id`: UUID (Foreign Key -> User.id)
- `status`: Enum ("pending", "approved", "rejected")

### WorkshopReport
User reporting incorrect data or errors for a workshop.
- `id`: UUID (Primary Key)
- `workshop_id`: UUID (Foreign Key -> Workshop.id)
- `reporter_id`: UUID (Foreign Key -> User.id)
- `reason`: String (Not Null)
- `status`: Enum ("open", "resolved", "dismissed")

## Validation Rules
1. Latitudes must be between -90 and 90.
2. Longitudes must be between -180 and 180.
3. Phone numbers should be validated against a regex pattern (depending on the region).
4. Ratings must be between 1 and 5.

## State Transitions (WorkshopSubmission)
- `pending` -> `approved` (added to Workshop table)
- `pending` -> `rejected` (submission discarded)
