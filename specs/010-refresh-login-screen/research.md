# Research: Refresh Login Screen Design

This document details the UI approach and component selection for the modern login screen redesign, updated after clarification.

## UI Decisions

### 1. Layout & Styling
- **Root Container**: `ConstraintLayout`. This is necessary for the precisely overlapping branding logo and centered card layout shown in the image.
- **Background**: `bg_login_gradient.xml` will be a `layer-list` containing a solid color and a subtle top-to-bottom linear gradient.
- **Typography**: Per clarification, we will use the **Android System Default** (Roboto/Inter-like variable font) to minimize asset bloat and follow the "Simplicity First" principle.

### 2. Form Components
- **Input Fields**: `com.google.android.material.textfield.TextInputLayout` using the `outlineBox` style, but with custom `app:boxCornerRadius` values of `28dp` to achieve the pill/rounded look.
- **Floating Labels**: We will use the standard M3 behavior where the hint floats on focus, ensuring accessibility without adding separate Label views above the fields (unless layout constraints require more space).
- **Error Feedback**: Per clarification, all validation errors will be displayed as **inline error messages** using the standard `TextInputLayout.setError()` functionality.

### 3. Component Color Mapping
- **Primary Color**: #D672E1 (Used for the branding icon background and the Login button).
- **Surface Color**: #FFFFFF (Main login card).
- **Input Background**: Light gray / #F5F5F5 (Matched from image).

## Scope Bounding

- **Social Login**: Google and Apple icons and text are removed. No logic or icons related to OAuth2 will be implemented.
- **Footer Links**: Terms of Service, Privacy Policy, and Help Center links are temporarily removed. The footer area will only contain the copyright and "Powered by" text.

## Dependencies
- No new dependencies are required. All features will be implemented using standard Material 3 components and native Kotlin code.
