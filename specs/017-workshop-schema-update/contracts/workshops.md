# Workshop API Contracts — tambal_ban table

Base URL: `https://xwqckmkjciptlbopmxjl.supabase.co/`

All requests via `AuthInterceptor` (adds `apikey` + `Authorization` headers automatically).

---

## GET rest/v1/tambal_ban — fetch workshops in bounding box

**Auth**: anonymous
**Query params**:
```
lat=gte.{minLat}&lat=lte.{maxLat}&lon=gte.{minLon}&lon=lte.{maxLon}&verified=eq.true
```

**Response 200**:
```json
[
  {
    "id": "uuid",
    "name": "text",
    "lat": -6.2,
    "lon": 106.8,
    "address": "text | null",
    "city": "text | null",
    "province": "text | null",
    "phone": "text | null",
    "opening_hours": "text | null",
    "rating": 4.2,
    "total_reviews": 12,
    "image_url": "https://... | null",
    "source": "osm | user",
    "verified": true,
    "created_at": "2026-05-03T00:00:00Z"
  }
]
```

**Response 4xx**: `{ "message": "...", "code": "..." }`

---

## GET rest/v1/tambal_ban — search by name or city

**Auth**: anonymous
**Query params**:
```
or=(name.ilike.*{query}*,city.ilike.*{query}*)&verified=eq.true
```

PostgREST OR syntax: single `or` param wraps multiple conditions.

**Response 200**: same array shape as above

---

## GET rest/v1/tambal_ban — get by id

**Auth**: anonymous
**Query params**:
```
id=eq.{uuid}
```

**Response 200**: array with 1 item

---

## POST rest/v1/tambal_ban — user add workshop

**Auth**: required (Bearer JWT)
**Header**: `Prefer: return=representation`

**Body**:
```json
{
  "name": "text",
  "address": "text",
  "city": "text",
  "province": "text | null",
  "lat": -6.2,
  "lon": 106.8,
  "phone": "text",
  "opening_hours": "text | null",
  "image_url": "text | null",
  "source": "user",
  "verified": false
}
```

**Response 201**: array with created object (not visible on map until `verified = true`)
**Response 401**: unauthenticated — redirect to LoginActivity
**Response 422**: missing required field

---

## PUT storage/v1/object/workshops/{userId}/{uuid}.jpg — upload workshop image

**Auth**: required (Bearer JWT)
**Header**: `Content-Type: image/jpeg`
**Body**: raw JPEG bytes

**Path**: `workshops` is the public bucket name; `{userId}/{uuid}.jpg` is the object path.

**Response 200**: `{ "Key": "workshops/{userId}/{uuid}.jpg" }`

**Public URL** (no auth required for display):
```
{SUPABASE_URL}/storage/v1/object/public/workshops/{userId}/{uuid}.jpg
```
Store this URL as `image_url` in the POST body to `rest/v1/tambal_ban`.
