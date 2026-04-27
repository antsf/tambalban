# Contract: Registration API

**Interface**: `AuthApi` / `AuthRepository`

## Request
`register(name, email, password)`

## Response (Success)
`AuthResult.Success(UserSession)`

## Response (Failure)
`AuthResult.Error(code, message)`

### Error Codes
| Code | Description |
|------|-------------|
| 422 | Email already exists |
| 400 | Invalid password strength |
| 503 | Service unavailable |
