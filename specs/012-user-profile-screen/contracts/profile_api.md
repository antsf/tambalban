# API Contracts: Profile & Storage

## Profile REST API

### GET /rest/v1/profiles
Fetch the current user's profile.

**Headers**:
- `Authorization: Bearer {{token}}`
- `apikey: {{apikey}}`

**Query**:
- `id=eq.{{user_id}}`
- `select=*`

**Response (200 OK)**:
```json
[
  {
    "id": "uuid",
    "full_name": "Marcus Thorne",
    "email": "marcus.thorne@design.com",
    "phone": "+1 (555) 012-3456",
    "avatar_url": "https://.../avatars/user_id/avatar.png",
    "updated_at": "2026-04-20T00:00:00Z"
  }
]
```

### PATCH /rest/v1/profiles
Update profile details.

**Query**:
- `id=eq.{{user_id}}`

**Body**:
```json
{
  "full_name": "New Name",
  "phone": "New Phone",
  "avatar_url": "New URL"
}
```

---

## Storage API

### POST /storage/v1/object/avatars/{{user_id}}/avatar.png
Upload avatar image.

**Headers**:
- `Authorization: Bearer {{token}}`
- `Content-Type: image/png` (or appropriate)

**Body**:
Binary image data.

**Response (200 OK)**:
```json
{
  "Key": "avatars/user_id/avatar.png"
}
```
