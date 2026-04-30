# TambalBan Agent System — How To Use

> **System**: Multi-Agent Coordinator | Constitution v1.2.0
> **Last Updated**: 2026-04-30

---

## Quick Start

Paste this at the start of any new session to restore context instantly:

```
Load TambalBan agent system. Constitution v1.2.0.
Read `current_state.md` and `.specify/MASTER_CONTEXT.md`. Resume from last known state.
```

---

## How The System Works

The system has **3 agents** that work in a pipeline:

```
You → BRIEF (plans) → GO → BUILD (codes) → TEST (validates) → NEXT
```

| Agent | Role | Output |
|-------|------|--------|
| **BRIEF** | Architect — plans only, never codes | YAML plan (≤15 lines) |
| **BUILD** | Coder — generates Kotlin from BRIEF YAML | Kotlin code (1 package) |
| **TEST** | QA — validates against Constitution v1.2.0 | 5-bullet PASS/FAIL list |

---

## Command Reference

| Command | What Happens |
|---------|-------------|
| `BRIEF: [feature description]` | BRIEF outputs a YAML plan for the feature |
| `GO` | BUILD generates code for ONE package |
| `NEXT` | Proceed to the next package after TEST passes |
| `SKIP TEST` | Skip QA and go straight to NEXT (not recommended) |
| `FIX [describe issue]` | BUILD patches only the flagged issue |
| `STATUS` | Summarize completed/pending packages and update `current_state.md` |
| `EXPLAIN` | BUILD adds inline comments to the last code output |

---

## Standard Workflow (Step by Step)

### Step 1 — Describe a Feature
```
BRIEF: implement debounced search pipeline in map/ui package
```

### Step 2 — BRIEF Outputs a YAML Plan
```yaml
task: search_pipeline
package: map
constitution_ref: II, III
steps:
  - file: MainViewModel.kt
    layer: viewmodel
    depends_on: [WorkshopRepository]
  - file: SearchSuggestionAdapter.kt
    layer: ui
    depends_on: [MainViewModel]
```

### Step 3 — Approve and Execute
```
GO
```
→ BUILD generates Kotlin code for the package defined in the YAML.

### Step 4 — TEST Runs Automatically
```
- ✅ MVVM boundary — No API calls in Activity
- ✅ Repository pattern — Search routed through WorkshopRepository
- ✅ Null safety — No !! found
- ✅ Security — BuildConfig keys only
- ✅ Offline safety — Loading/Empty/Error states handled
✅ Compliant. Type NEXT to continue.
```

### Step 5 — Proceed
```
NEXT
```
→ Move to the next package in the plan.

---

## Key Files

| File | Purpose |
|------|---------|
| `current_state.md` | Live status of all packages + open issues |
| `.specify/MASTER_CONTEXT.md` | Token-optimized context: constitution + features + pending tasks |
| `.specify/memory/constitution.md` | Full Constitution v1.2.0 (source of truth) |
| `.agent/workflows/brief.md` | BRIEF agent rules and output template |
| `.agent/workflows/build.md` | BUILD agent rules and forbidden patterns |
| `.agent/workflows/test.md` | TEST agent checklist and output format |

---

## Patching Issues

If TEST reports a FAIL:
```
FIX WorkshopRepository.kt — getReviews method missing @GET annotation
```
BUILD will patch **only** the flagged file without regenerating the whole package.

---

## Constitution Guardrails (Always Active)

These rules are enforced silently on every output:

- ❌ No Firebase imports
- ❌ No Google Maps SDK
- ❌ No hardcoded API keys (use `BuildConfig`)
- ❌ No `!!` operator (use `?.` or `?:`)
- ❌ No Retrofit/API calls in Activity or Fragment
- ✅ All data flows through Repository only
- ✅ EncryptedSharedPreferences for auth tokens
- ✅ Every UI screen must handle Loading / Empty / Error states

---

## Package Status Legend

| Symbol | Meaning |
|--------|---------|
| ⬜ | Not Started |
| 🔄 | In Progress |
| ✅ | Done |
| ❌ | Blocked |

---

## Tips

- Always run `BRIEF` before `GO` — never skip planning.
- One package per `GO` session keeps code reviewable and TEST meaningful.
- After a major session, type `STATUS` to sync `current_state.md`.
- Use `EXPLAIN` when you need to understand generated code before merging.
