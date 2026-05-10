---
description: Analyze changes, write caveman commit, push + PR to dev
---

## /commit

1. Run `git status` + `git diff` to see all changes
2. Draft caveman commit:

   **Subject:** `<type>(<scope>): <imperative summary>` — ≤50 chars, no period
   - Types: `feat`, `fix`, `refactor`, `perf`, `docs`, `test`, `chore`, `build`, `ci`, `style`, `revert`
   - Imperative: "add" not "added", "fix" not "fixed"
   - No: "this commit does", "I", "we", "now", AI attribution, emoji

   **Body:** skip unless non-obvious *why*, breaking change, migration, linked issue
   - Wrap 72, bullets `-`, ref issues `Closes #42`, `Refs #17`

3. `git add .` → commit with message → push (set upstream if needed)
4. `gh pr create --base dev --head $(git branch --show-current) --title "<subject>" --body "<body>"`
5. Return PR URL
