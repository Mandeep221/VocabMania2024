---
name: to-prd-vocabmania
description: Synthesize a grill-me session (or planning conversation) into a repo-local PRD for VocabMania. Use after grill-me, when starting a new phase, or when the user says "to-prd", "write PRD", or "create spec".
---

# To PRD (VocabMania)

Turn **decisions already made** (usually via `grill-me`) into a concrete PRD stored in the repo. This is the **spec** that `to-vertical-slices` breaks into end-to-end tickets.

**Do NOT interview the user** — synthesize from conversation + repo. If context is thin, tell the user to run `grill-me` first or talk through the phase; don't run on an empty plate.

## When to use

- After a `grill-me` session locks decisions for an upcoming **phase** (typically Phase 2+)
- User says: "to-prd", "write the PRD", "create spec for Phase N"
- **Not** for every week of work — phase-level scope only

## Inputs

- Conversation context (grill-me decisions, user notes)
- `docs/VISION.md` — north star, stack, architecture rules, phase roadmap
- `docs/AI_WORKFLOW.md` — workflow constraints
- `.cursor/rules/vocabmania.mdc` — hard rules

## Process

1. **Read** `docs/VISION.md` and the relevant phase section. Note what's already done vs pending.
2. **Explore the codebase** if you haven't: `:shared` (SQLDelight, use cases, SRS), `:app` (Compose screens, navigation), legacy Java back door.
3. **Sketch major modules** to build or modify. Prefer **deep modules** in `:shared` (simple interface, testable in `commonTest`). List which modules need tests.
4. **Confirm modules with user** (one message): "These are the modules I'd touch — match your expectations? Which need tests?" Wait for approval before writing the PRD.
5. **Write the PRD** to `docs/prds/phase-<N>-<slug>.md` using the template below.
6. **Update** `docs/VISION.md` phase section only if grill decisions changed scope (don't duplicate the full PRD into VISION).
7. **Output** the file path and tell the user: "Run `to-vertical-slices` on this PRD to create vertical tickets."

### Optional: GitHub issue

Only if the user explicitly asks — publish the PRD body via `gh issue create`. Default is **repo-local markdown only**.

## PRD template

The PRD is read by humans, `to-vertical-slices`, and future agent sessions. It must be a **spec**, not a sketch — concrete enough to implement without re-deriving grill decisions.

```markdown
# PRD: [Phase N — Title]

**Status:** draft | approved
**Grill session:** [date or chat reference]
**Phase:** N · Weeks X–Y
**Est. total hours:** ~[N] (at 8–10 hrs/week)

## Problem Statement

What user/product problem this phase solves. User perspective.

## Solution

What we're building. User perspective. Tie to north star (daily habit + SRS).

## Decisions from grill-me

Bullet list of locked decisions this PRD implements. Reference grill Q&A outcomes — don't reopen settled forks.

## User Stories

Numbered list. Format: As a \<actor\>, I want \<feature\>, so that \<benefit\>.

Cover all phase scope. Include edge cases (empty state, errors, legacy back door if relevant).

## Implementation Decisions

- Modules to build/modify (`:shared` domain, SQLDelight, use cases; `:app` Compose/ViewModels/navigation)
- Interfaces and contracts (repository methods, use case inputs/outputs, navigation routes)
- Schema changes (SQLDelight tables/columns)
- SRS / algorithm changes if any
- Firebase / platform-specific work and where it lives (`commonMain` vs `androidMain`)
- Architectural constraints from VISION.md

Do **not** include specific file paths unless encoding an unavoidable contract (e.g. route names). Prefer module-level language.

**Exception:** Prototype snippets that encode decisions (state machine, scheduler interface, schema) may be inlined — trim to decision-rich parts only.

## Testing Decisions

- What to test in `commonTest` (domain, SRS, repositories)
- What to verify on device (demo script)
- Prior art (existing tests to follow, e.g. `SimpleSrsSchedulerTest`)

## Out of Scope

Explicit exclusions for this phase. "We are not building X" — not vague deferrals.

## Risks & Open Questions

Known risks, deferred decisions, dependencies on legacy/Firebase.

## Further Notes

Anything else worth recording for implementers.
```

## Hard rules (VocabMania)

- Business logic in `:shared/commonMain` — never specified as Composable or legacy Java responsibility
- New Compose screens serve the **new SRS concept** — not 1:1 ports of legacy Activities
- Don't polish legacy Java/XML unless explicitly in scope as a blocker fix
- KMP path: SQLDelight in `commonMain`; expect/actual only when required

## After writing

Tell the user:

> PRD written to `docs/prds/phase-N-<slug>.md`. Review it, then run **`to-vertical-slices`** to break it into vertical, demoable tickets.

Do **not** start implementation in the same session unless the user asks.
