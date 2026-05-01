# Research: Workshop Detail Visual Components

## Decision: Status Badge Implementation
- **Choice**: Overlay `TextView` on the bottom-left of the header image.
- **Rationale**: Cleanest look for "Editorial Minimalism".
- **Styles**: Use `bg_status_open.xml` for background.

## Decision: Icon Set
- **Address**: `ic_marker_pin` (20dp)
- **Phone**: `ic_phone` (20dp)
- **Business Hours**: `ic_schedule` (to be created or use standard Material icon)
- **Star**: `ic_star` (16dp)

## Decision: Layout Structure
- **Root**: `CoordinatorLayout` to allow for collapsing header effects if needed later.
- **Content**: `NestedScrollView` containing a `ConstraintLayout` for the info sections.
