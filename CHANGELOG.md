# Changelog

All notable changes to TambalBan documented here.
Format: [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

---

## [Unreleased]

### Added
- `CLAUDE.md`: Complete Android agent setup with tech stack, custom components, code style, and do-not rules
- `.specify/memory/constitution.md`: Rewritten v2.0.0 with package-by-feature rule, XML-first, MVVM chain, build verification
- `.claude/agents/brief.md`: Design agent — produces spec.md, contracts, tasks.md
- `.claude/agents/build.md`: Implementer agent — executes tasks.md phase by phase
- `.claude/agents/test.md`: QA agent — writes and runs JUnit4 + MockK tests
- `.claude/context/stack.md`: Single source of truth for agents (tech stack, architecture, key files)
- `.claude/context/android-layout.md`: Package → file mapping for all modules
- `CHANGELOG.md`: This file
- caveman: Claude Code plugin for token-efficient responses (~75% output token reduction)
- speckit: GitHub Spec-Kit v0.8.4 Claude integration (14 skills in `.claude/skills/`)

---

## [0.16.0] — 2026-04-xx

### Changed
- Workshop detail UI: premium design refresh, Indonesian localization

## [0.15.0] — 2026-04-xx

### Added
- Workshop list: infinite scroll, search, edge-to-edge support
