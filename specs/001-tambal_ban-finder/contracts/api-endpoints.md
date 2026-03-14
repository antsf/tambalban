# API Contracts: TambalBan Finder

The Android application communicates with the Supabase backend via the PostgREST API.

## Base URL
`https://{project_ref}.supabase.co/rest/v1/`

## Headers
- `apikey`: `SUPABASE_ANON_KEY`
- `Authorization`: `Bearer {user_jwt_token}` (if authenticated)
- `Content-Type`: `application/json`

## Endpoints

### 1. Fetch Workshops in Viewport
- **Method**: `GET`
- **Path**: `/workshops`
- **Params**:
  - `select`: `*`
  - `latitude`: `gte.{south}&lte.{north}`
  - `longitude`: `gte.{west}&lte.{east}`
- **Response**: `200 OK` (JSON array of Workshops)

### 2. Fetch Workshop Details (by ID)
- **Method**: `GET`
- **Path**: `/workshops?id=eq.{workshop_id}`
- **Params**:
  - `select`: `*,reviews(*)`
- **Response**: `200 OK` (JSON object with Workshop and nested Reviews)

### 3. Submit New Workshop
- **Method**: `POST`
- **Path**: `/workshop_submissions`
- **Body**:
  ```json
  {
    "name": "string",
    "phone": "string",
    "address": "string",
    "latitude": "double",
    "longitude": "double",
    "user_id": "uuid"
  }
  ```
- **Response**: `201 Created`

### 4. Fetch Reviews for Workshop
- **Method**: `GET`
- **Path**: `/reviews?workshop_id=eq.{workshop_id}`
- **Params**:
  - `order`: `created_at.desc`
- **Response**: `200 OK` (JSON array of Reviews)

### 5. Report a Workshop
- **Method**: `POST`
- **Path**: `/workshop_reports`
- **Body**:
  ```json
  {
    "workshop_id": "uuid",
    "reason": "string",
    "reporter_id": "uuid"
  }
  ```
- **Response**: `201 Created`
