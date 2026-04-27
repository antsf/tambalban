# Research: User Registration with Supabase

**Feature**: User Registration (`013-auth-register`)

## Decision 1: Registration API Selection
- **Decision**: Use `supabase.auth.signUp(email, password, data)` with additional user metadata (Full Name).
- **Rationale**: Direct integration with Supabase Auth ensures security and automatic session management.
- **Alternatives considered**: Custom backend registration (Rejected: against Constitution Principle III).

## Decision 2: Error Handling and Mapping
- **Decision**: Map Supabase Auth exceptions to user-friendly strings via a `AuthErrorMapper` utility.
- **Rationale**: Supabase returns specific error codes (e.g., `422` for duplicate email) that need to be translated for the UI.
- **Alternatives considered**: Raw error messages (Rejected: poor UX).

## Decision 3: Post-Registration Flow
- **Decision**: Automatic login and redirection to the Map screen.
- **Rationale**: Standard industry pattern for onboarding.
- **Alternatives considered**: Required email verification (Deferred: MVP focus).

## Decision 4: Icon Standardization
- **Decision**: Use `ic_visible` and `ic_invisible` at exactly 20dp.
- **Rationale**: Complies with Constitution Principle VII.
- **Alternatives considered**: Android default icons (Rejected: inconsistent sizing).
