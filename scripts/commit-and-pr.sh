#!/bin/bash
set -euo pipefail

BRANCH=$(git branch --show-current)
if [ "$BRANCH" = "dev" ] || [ "$BRANCH" = "main" ] || [ "$BRANCH" = "master" ]; then
  echo "Error: On $BRANCH branch. Switch to feature branch first."
  exit 1
fi

MSG="$*"
if [ -z "$MSG" ]; then
  echo "Usage: ./scripts/commit-and-pr.sh <commit message>"
  exit 1
fi

echo "=== git status ==="
git status

echo ""
echo "=== git add . ==="
git add .

echo ""
echo "=== git commit ==="
git commit -m "$MSG"

echo ""
echo "=== git push ==="
git push -u origin "$BRANCH" 2>&1 || git push 2>&1

echo ""
echo "=== gh pr create → dev ==="
gh pr create \
  --base dev \
  --head "$BRANCH" \
  --title "$MSG" \
  --body "PR from \`$BRANCH\` → \`dev\`" \
  --fill

echo ""
echo "Done."
