# VocabMania — AI Workflow Playbook

> How we build with AI. Start at **B (playbook)**; promote repeated patterns to **C (Cursor skills)**.

Read `docs/VISION.md` before every session.

---

## Planning pipeline (team-simulation)

Use this sequence when starting a **new phase** (office-prep workflow):

```
1. grill-me          → lock decisions (one question at a time)
2. to-prd-vocabmania → docs/prds/phase-N-<slug>.md
3. to-vertical-slices → docs/prds/phase-N-<slug>-tickets.md + update week table below
4. Implement         → one ticket per week, one chat per ticket
```

Skills live in `.cursor/skills/`. Phase 0–1 shipped without formal PRDs; use the pipeline from **Phase 2** onward.

---

| Block | Time | Activity |
|-------|------|----------|
| **Plan** | ~1 hr | Read VISION.md → spec this week's phase + week → grill-me if unclear |
| **Build** | ~6 hr | `:shared` (SQLDelight/domain) → Compose UI → wire ViewModel |
| **Review** | ~1 hr | Run on device → fix → commit with clear message |
| **Reflect** | ~1 hr | Update VISION.md if decisions changed → note skill promotion opportunities |

---

## Chat Hygiene

- **One chat per week of work:** `phase-0-week-1`, `phase-1-week-2-onboarding`, `phase-1-week-3-home`, etc.
- **Start each chat with:** "Read `docs/VISION.md` and `docs/AI_WORKFLOW.md`. We're working on Phase [N], Week [N] — [focus]."
- **Don't** continue stale 3-month threads — context rots; docs are the source of truth.

---

## Phase / Week Template

Copy this into a chat or `docs/phases/phase-N-week-M.md` when starting work.

```markdown
## Phase [N] · Week [M] — [focus]
**Est. hours:** [8-10]

### Goal
One sentence. What can the user do when this week ships?

### :shared changes
- [ ] SQLDelight tables / queries
- [ ] Domain models
- [ ] Use case(s)
- [ ] Unit tests (commonTest)

### Android / Compose changes
- [ ] Screen(s)
- [ ] ViewModel(s)
- [ ] Navigation wiring

### Out of scope
What we're explicitly NOT doing this week.

### Done when
- [ ] Runs on device
- [ ] No regressions in new flow
- [ ] Committed
```

### Build Order (Every Week)

```
1. SQLDelight schema/query (if needed)
2. Repository + use case in commonMain
3. commonTest for domain logic
4. ViewModel (Android)
5. Compose screen(s)
6. Navigation
7. Device test
```

---

## AI Session Prompts

### Start of session

```
Read docs/VISION.md and docs/AI_WORKFLOW.md.
Current work: Phase [N], Week [M] — [focus].
Implement [specific task]. Follow architecture rules in .cursor/rules/vocabmania.mdc.
Do not modify legacy Java/XML unless explicitly asked.
```

### Stuck / scope creep

```
Grill me on [decision]. One question at a time with your recommendation.
Check docs/VISION.md — does this fit the current phase scope?
```

### End of session

```
Summarize what shipped, what's left, and any VISION.md updates needed.
Flag any skill promotion opportunities (patterns we repeated).
```

---

## B → C Promotion Triggers

Promote to a Cursor skill when the signal appears **twice**. Flag in chat: **"Skill promotion opportunity."**

| Signal | Playbook (B) | Promote to skill (C) |
|--------|--------------|----------------------|
| Same phase/week scaffold steps 2× | Follow phase/week template manually | `/scaffold-feature` — SQLDelight + repo + ViewModel + Compose boilerplate |
| SRS / scheduler work | Implement + test ad hoc | `/srs-scheduler` — algorithm spec + commonTest template |
| Review checklist 3× | Manual review list | `/review-week` — checklist + Bugbot prompt |
| Planning >1 hr repeatedly | Grill-me ad hoc | `/plan-feature` — decision tree as skill input |
| Re-explaining stack each chat | Point to VISION.md | Enrich `.cursor/rules/vocabmania.mdc` |

### Planned skill promotions (roadmap)

| After | Skill to create |
|-------|-----------------|
| Phase 1 ships (Week 4) | `scaffold-feature` |
| SM-2 upgrade (Week 5) | `srs-scheduler` |
| 3 week reviews | `review-week` |
| Phase 3 (Week 12) | `plan-feature` |

Skills live in `.cursor/skills/` (project) or personal skills folder.

---

## Code Review Checklist (Before Commit)

- [ ] Business logic in `:shared/commonMain`, not in Composables or legacy Java
- [ ] No polish / refactors on legacy Activities
- [ ] SQLDelight queries tested or manually verified
- [ ] New screens follow new product concept (not 1:1 ports)
- [ ] Runs on device/emulator
- [ ] Commit message states phase/week + why

---

## Phase Backlog

| Phase | Weeks (absolute) | Focus | Status |
|-------|------------------|-------|--------|
| **0 — Foundation** | 1 | SQLDelight, migration, simple SRS, docs + rules | **done** |
| **1 — Core flow** | 2–4 | Onboarding, Home, Review session → shippable debug build | **done** |
| **2 — Deepen** | 5–11 | Firebase catalog, SM-2 lite, progress, favorites, WOTD — see tickets | pending |
| **3 — Cohesive app** | 12+ | Delete legacy UI, notifications, release prep, AI playbook | pending |

**Phase 2 tickets:** `docs/prds/phase-2-deepen-tickets.md` (6 vertical slices, forward weeks 1–7 within Phase 2).

### Week-level detail

| Week | Phase | Focus | Ticket | Status |
|------|-------|-------|--------|--------|
| 1 | 0 | Foundation — SQLDelight, migration, simple SRS | — | done |
| 2 | 1 | Onboarding + navigation shell | — | done |
| 3 | 1 | Home + due queue | — | done |
| 4 | 1 | Review session + session complete | — | done |
| 5 | 2·W1 | Real vocabulary library (Firebase import) | 1 | pending |
| 6 | 2·W2 | Reviews adapt to ratings (SM-2 lite) | 2 | pending |
| 7 | 2·W3 | See my progress — activity + mastery | 3 | pending |
| 8 | 2·W4 | See my progress (continued) | 3 | pending |
| 9 | 2·W5 | Save and browse favorites | 4 | pending |
| 10 | 2·W6 | Word of the day on Home | 5 | pending |
| 11 | 2·W7 | Review favorites only + polish | 6 | pending |
| 12 | 3·W1 | Phase 3 — TBD (separate PRD) | — | pending |

Update **Status** as work completes: `pending` → `in progress` → `done`.

Full phase goals, deliverables, and done-when checklists live in `docs/VISION.md`.

---

## Case Study Notes (Phase 3)

Capture as you go — fodder for "AI-Mobile-Developer" portfolio piece.

- What prompt patterns worked?
- Where did AI waste time?
- What had to be in VISION.md vs chat context?
- Which skills saved the most time?

Dump raw notes in `docs/case-study-notes.md` (create when ready).
