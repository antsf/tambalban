# Quickstart: Implementing The Responsive Guardian

## 1. Setup Theme
Ensure `res/values/themes.xml` inherits from the Guardian base styles and excludes all 1px borders.

```xml
<style name="Theme.TambalBanFinder" parent="Theme.Material3.Light.NoActionBar">
    <!-- Apply color mapping from data-model.md -->
</style>
```

## 2. Using Critical Actions (SOS)
Always use the `Guardian.Button.Primary` style for SOS or "Request Help" buttons. These include the 45-degree Orchid gradient.

```xml
<com.google.android.material.button.MaterialButton
    style="@style/Guardian.Button.Primary"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:text="REQUEST MOBILE REPAIR" />
```

## 3. Creating Sectioned Layouts (No-Line)
Instead of `<View android:height="1dp" android:background="@color/divider"/>`, use background tints:

```xml
<!-- Background surface -->
<LinearLayout
    android:background="?attr/colorSurface" ...>

    <!-- Content Block -->
    <CardView
        app:cardBackgroundColor="?attr/colorSurfaceContainerLow"
        app:cardElevation="0dp" ...>
    </CardView>
</LinearLayout>
```

## 4. Typography Hierarchy
Use the semantic text styles for all status updates.

- Status Update: `?attr/textAppearanceHeadlineLarge` (Plus Jakarta Sans)
- Distance/Metric: `?attr/textAppearanceDisplaySmall` (Plus Jakarta Sans)
- Body: `?attr/textAppearanceBodyLarge` (Inter)

## 5. Live Drawer (Glass Appearance)
When implementing the bottom sheet, wrap your content in a `BlurView`.

```xml
<eightbitlab.com.blurview.BlurView
    android:id="@+id/blurView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:blurOverlayColor="#D9FFFFFF"> <!-- 85% opacity White -->
    <!-- Content here -->
</eightbitlab.com.blurview.BlurView>
```
