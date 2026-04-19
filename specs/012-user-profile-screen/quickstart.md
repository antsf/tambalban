# Quickstart: Profile Feature

## Prerequisites
- Supabase project configured with `profiles` table.
- Supabase Storage bucket `avatars` created and public access enabled (or appropriate RLS).

## How to Test

### 1. Navigation
1. Launch the app.
2. Ensure you are logged in.
3. Tap the profile avatar icon in the search bar.
4. Verify you are on the **Profile Screen**.

### 2. Viewing Data
- Verify name, email, and phone match your account details.
- Verify the avatar loads correctly.

### 3. Editing
1. Tap "Edit Profile".
2. Change your name or phone number.
3. Tap "Save".
4. Verify navigation back to the Profile screen and data update.

### 4. Avatar Change
1. In the Edit Profile screen, tap the edit badge on the avatar.
2. Select a photo from the gallery.
3. Tap "Save".
4. Verify the new avatar is uploaded and displayed.

### 5. Authentication Gate
1. Log out.
2. Restart the app.
3. Tap the profile avatar icon.
4. Verify you are redirected to the **Login Screen**.
