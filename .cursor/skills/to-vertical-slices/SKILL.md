---
name: to-vertical-slices
description: Break a VocabMania PRD into vertically sliced, end-to-end demoable tickets (shared + UI + navigation). Use after to-prd-vocabmania, when the user says "to-vertical-slices", "break into tickets", or "create issues from PRD".
---

# To Vertical Slices (VocabMania)

Break a **repo-local PRD** (`docs/prds/phase-N-*.md`) into ordered, **tracer-bullet** tickets. Each ticket cuts through every layer (`:shared` → Compose → navigation → device verification) — not horizontal "database week" / "UI week".

Feeds the weekly execution model in `docs/AI_WORKFLOW.md` (one ticket ≈ one week at 8–10 hrs).

## Inputs

- **PRD path** (required): e.g. `docs/prds/phase-2-deepen.md`. If user invoked without a path, ask which PRD.
- Conversation context (optional)

## Process

### 1. Read the PRD

Read the PRD file completely. It is the spec. Don't add scope; don't redesign. If ambiguous, ask the user to clarify **before** drafting slices — reflect the PRD as-is.

Also read `docs/VISION.md` and `.cursor/rules/vocabmania.mdc`.

### 2. Check for existing tickets

Look for `docs/prds/phase-N-<slug>-tickets.md` or `docs/prds/phase-N/ticket-*.md`.

If tickets already exist, stop and ask: (a) abort, (b) add more, or (c) replace. Don't silently double up.

### 3. Explore the codebase

Understand current state: what's shipped (Phase 0–1), what the PRD touches. Look for **prefactor** opportunities — "make the change easy, then make the easy change." Prefactoring gets its own ticket(s) at the start if needed.

### 4. Draft vertical slices

<vertical-slice-rules>

- Each slice delivers a **narrow but complete** path: `:shared` (schema/domain/use case/tests) + Compose UI + navigation + device demo
- A completed slice is **demoable on its own** — pass the cold-handoff test (see below)
- Tickets are **flat** — no nested epics. Too big? Split into peer tickets, don't nest
- **Order** satisfies dependencies: schema before UI that reads it; scheduler before screen that uses new intervals
- One ticket ≈ **one week** (8–10 hrs), one agent session, one commit — realistic for a couple of screens + shared logic + tests
- Include **prefactor** ticket(s) first when the PRD requires structural cleanup

</vertical-slice-rules>

### Cold-handoff test

Each ticket must pass:

> Could a colleague implement this from the ticket body alone, without a 30-minute Slack call?

If no, add acceptance criteria, demo script, or `:shared` contract detail.

### 5. Quiz the user

Present a numbered list. For each ticket:

- **Title** — short, imperative
- **What it delivers** — one or two sentences, end-to-end behavior
- **Depends on** — earlier ticket # or "none"
- **Est. hours** — ~8–10

Ask:

- Granularity right? (too coarse / too fine)
- Order right?
- Merge, split, or drop any?

Iterate until approved.

### 6. Publish tickets

Write to **`docs/prds/phase-N-<slug>-tickets.md`** using the template below.

**Optionally** create GitHub issues (one per ticket) only if the user asks — use `gh issue create` with the ticket body. Default: **markdown only**.

### 7. Sync execution tracker

Update **`docs/AI_WORKFLOW.md`** week-level table: map each ticket to Phase + Week, status `pending`.

Update **`.cursor/rules/vocabmania.mdc`** Planning section: current Phase + Week from the first pending ticket.

## Ticket file template

```markdown
# Phase N Tickets: [slug]

**Parent PRD:** [docs/prds/phase-N-slug.md](link)
**Approved:** [date]

---

## Ticket 1: [Imperative title]

**Phase:** N · **Week:** X · **Est. hours:** 8–10
**Depends on:** none

### What to build

One to three short paragraphs. What the user can **do on device** when this ships. Frame around delivered behavior, not file lists.

### :shared (if applicable)

- Schema / queries
- Domain / use cases
- commonTest coverage

### :app (if applicable)

- Screen(s) / ViewModel(s)
- Navigation
- Theme / UX notes

### Acceptance criteria

- [ ] Concrete, checkable outcome 1
- [ ] Concrete, checkable outcome 2
- [ ] `commonTest` covers new domain behavior (if applicable)
- [ ] Runs on device/emulator per demo script
- [ ] No scope creep into Out of Scope (PRD)

### Demo script

1. Install / launch app
2. Steps to verify this ticket's behavior
3. Expected result

### Out of scope for this ticket

What this ticket explicitly does NOT include (defer to later tickets).

---

## Ticket 2: ...

(repeat per ticket)
```

## Hard rules (VocabMania)

- Every ticket includes **shared + UI** when the feature is user-visible — no "SQLDelight only" tickets unless pure prefactor with no UI dependency
- Legacy Java/XML: do not polish; back door stays unless PRD says delete
- Business logic stays in `:shared` use cases — tickets must not assign domain logic to Composables

## After publishing

Tell the user:

> Tickets written to `docs/prds/phase-N-<slug>-tickets.md`. `AI_WORKFLOW.md` updated.
>
> Start implementation with: "Read VISION.md, the PRD, and Ticket N. Implement end-to-end."

Remind: **one ticket per week**, reviewer hat before commit (match AC + demo script).

## Relationship to other skills

```
grill-me  →  decisions
to-prd-vocabmania  →  phase spec (this PRD)
to-vertical-slices  →  ordered tickets (this skill)
implement  →  build Ticket N in one session
```
