# VocabMania — Product Vision

> Living document. Update when grill-me decisions change. Read at the start of each session.

## North Star

**Ship an app people return to daily** — not a tech demo, not a full rewrite that never lands.

KMP and AI workflows are **how we build**, not what we optimize for.

## Time Budget

- **Duration:** ~12 weeks (parental leave)
- **Cadence:** 8–10 hours / week (~96–120 hours total)
- **Strategy:** Phase 1 core flow first → replace legacy screens → delete old UI ("A now, C by deletion")

## Product Concept

**Daily habit loop with spaced repetition (SRS) under the hood.**

- Users open the app, see what's due today, complete a short review session, build a streak.
- Words reappear when they're about to be forgotten — not on a fixed 12-hour test cooldown.
- v1 SRS: simple intervals (Phase 1). Upgrade to SM-2 lite (Phase 2).

### What We're NOT Doing

- Porting old Java/XML screens to Compose 1:1
- Polishing legacy Activities (`TestActivity`, `QuestionsActivity`, etc.)
- Big-bang rewrite before shipping anything

### What We ARE Doing

- **New Compose screens** for the new concept
- **Shared logic in `:shared`** (domain, SQLDelight, SRS scheduler)
- Legacy app stays in the APK until a post–Phase 3 deletion pass (Compose door sealed in Phase 3)

## Target User Flow (Phase 1)

```
Splash
  → Onboarding (first launch only)
       Welcome → pick level (E/M/T) → set daily goal (10/15/20 words)
  → Home
       Streak · due count · "Start review"
  → Review Session
       Show word → reveal meaning → rate (Again / Hard / Good / Easy)
  → Session Complete
       Summary · streak updated
```

Legacy test flow: sealed from Compose (Phase 3 Ticket 1); file deletion later.

## Technical Stack

| Layer | Choice |
|-------|--------|
| Shared logic | Kotlin Multiplatform `:shared` module |
| Data | SQLDelight in `commonMain` (words, review_cards, user_settings) |
| Legacy data | One-time migration from legacy SQLite (favorites + revision lists) |
| Word catalog | Firebase in legacy app; `:shared` seeds 15 starter words if DB empty. Firebase bulk import → Phase 2. |
| New UI | Jetpack Compose (Android only for now) |
| Old UI | Java + XML — do not modify except bug fixes blocking migration |
| iOS | Phase 2+ (after Kotlin 2.0 / Compose Multiplatform evaluation) |

## Architecture Rules

1. **Domain logic lives in `:shared/commonMain`** — models, use cases, SRS scheduler, repository interfaces.
2. **Platform specifics use expect/actual** only when SQLDelight or Android APIs require it.
3. **Compose ViewModels** call `:shared` use cases — no business logic in Composables.
4. **No new code in legacy Java Activities** unless unblocking migration.

## 12-Week Roadmap — Phase Breakdown

### Phase 0 — Foundation · Week 1 · **DONE**

**Goal:** Shared data layer, simple SRS, legacy migration, docs + rules in place.

**Deliverables:**
- SQLDelight schema (words, review_cards, user_settings, migration_state)
- Legacy SQLite migrator (favorites + revision lists)
- Simple SRS scheduler + commonTest
- Repository interfaces + use cases in `:shared`
- `docs/VISION.md`, `docs/AI_WORKFLOW.md`, `.cursor/rules/vocabmania.mdc`

**Done when:**
- [x] `:shared` compiles; unit tests pass for SRS scheduler
- [x] Migration runs on first launch without data loss
- [x] Seed catalog loads when DB empty
- [x] Docs and rules reflect stack + architecture decisions

---

### Phase 1 — Core flow · Weeks 2–4 · **DONE**

**Goal:** Shippable debug build — onboarding through session complete.

| Week | Focus | Status |
|------|-------|--------|
| 2 | Onboarding + navigation shell (`AppShellActivity`, routes) | done |
| 3 | Home screen (streak, due count, start review) | done |
| 4 | Review session + session complete | done |

**Deliverables:**
- Compose onboarding (level + daily goal)
- Home with live due queue count
- Review screen (Again / Hard / Good / Easy)
- Session complete summary + streak update
- End-to-end navigation wired to `:shared` use cases

**Done when:**
- [x] First launch → onboarding → home → review → complete works on device
- [x] Streak and due count reflect real SRS state
- [x] Legacy test flow still reachable from overflow (not removed yet)
- [ ] Committed; Phase 1 debug build installable

---

### Phase 2 — Deepen · Weeks 1–7 (forward) · **DONE**

**Goal:** Richer learning loop — Firebase catalog, SM-2 lite spaced repetition, progress dashboard, favorites, WOTD.

**Grill decisions:** See `docs/prds/phase-2-deepen.md` (approved July 9, 2026).

**Primary outcome:** Motivation & progress; SM-2 lite engine underneath.

| Week (forward) | Focus | Ticket | Status |
|----------------|-------|--------|--------|
| 1 | Real vocabulary library (Firebase + seed fallback) | 1 | done |
| 2 | Reviews adapt to ratings (SM-2 lite) | 2 | done |
| 3–4 | See my progress (activity + mastery + level tabs) | 3 | done |
| 5 | Save and browse favorites | 4 | done |
| 6 | Word of the day on Home | 5 | done |
| 7 | Review favorites only + polish | 6 | done |

Tickets: `docs/prds/phase-2-deepen-tickets.md`

**Deliverables:**
- Full Firebase word catalog in SQLDelight
- SM-2 lite scheduler with ease factor + migration
- Combined progress dashboard with level tabs
- Compose favorites + WOTD on Home
- Legacy favorites/progress paths replaced in new UI (Compose); Activities remain until Phase 3+ deletion

**Done when:**
- [x] Catalog import works; seed fallback on offline
- [x] SM-2 lite tested; Phase 1 review history preserved
- [x] Progress dashboard shows mastery + activity per level
- [x] Favorites usable without legacy FavoritesActivity (Compose path)
- [x] WOTD on Home

---

### Phase 3 — Cohesive app · Weeks 1–6 (forward) · **PENDING**

**Goal:** One Compose product — habit reminder, warm-scholar redesign, signed RC, AI playbook. Legacy UI unreachable (file deletion later).

**Grill decisions:** See `docs/prds/phase-3-cohesive.md` (approved July 13, 2026).

**Primary order:** Seal legacy door → daily reminder → redesign (tokens/dark, then art/motion) → RC → playbook.

| Slice (forward) | Focus | Status |
|-----------------|-------|--------|
| 1 | Seal legacy door + Share/Rate overflow | done |
| 2 | Daily reminder (Home toggle, 7 PM) | done |
| 3a | Theme tokens + system dark + screen pass | done |
| 3b | Empty-state illustrations + motion | done |
| 4 | Signed RC + Play listing draft | done |
| 5 | AI playbook + case study + 2 skills | pending |

Tickets: `docs/prds/phase-3-cohesive-tickets.md`

**Deliverables:**
- No Compose path into legacy test hub
- Opt-in local daily reminder with ShouldNotifyToday rules
- Warm-scholar light/dark UI + illustrations + motion
- Signed release candidate on device + listing draft
- VISION / AI_WORKFLOW / case-study notes + 2 Cursor skills

**Done when:**
- [x] No Compose dependency on legacy for core flow
- [x] Notification opt-in works; copy + fire rules match PRD
- [x] Release build runs on device (emulator RC smoke; see `docs/rc-smoke-checklist.md`)
- [ ] Case study notes captured; 2 skills promoted

**Explicitly later (post–Phase 3):** Delete legacy Activities/XML from the repo.

### When Time Is Tight — Cut In This Order

1. **Never cut:** `:shared` domain + SQLDelight + Review session
2. **Cut first:** Firebase WOTD, notifications, case study draft
3. **Defer:** SM-2 lite (stay on simple SRS), favorites, progress dashboard
4. **Never do:** Polish old Java/XML screens

## Legacy App Reference (Do Not Rebuild)

| Old screen | Old purpose | New equivalent |
|------------|-------------|----------------|
| `TestActivity` | Test hub + 12hr cooldown | `HomeScreen` |
| `QuestionsActivity` | Timed 5-question MCQ | `ReviewScreen` |
| `ReviseActivity` | Missed-word list | SRS due queue |
| `MainActivity` | Progress graphs | Mastery dashboard (Phase 2) |
| `FavoritesActivity` | Saved words | Reimagined favorites (Phase 2) |
| `MatchActivity` | Post-test matching | Cut or reimagine later |

## Success Criteria (End of Leave)

- [ ] Daily habit loop works end-to-end on a real device
- [ ] Streak + due queue + review session feel polished
- [ ] `:shared` owns word model, SRS scheduler, session state
- [ ] SQLDelight schema migrated from legacy word bank
- [ ] 2–3 legacy flows removed or replaced
- [ ] `docs/AI_WORKFLOW.md` reflects what actually worked
- [ ] At least 2 patterns promoted to Cursor skills

## Open Questions

Track unresolved decisions here. Resolve via grill-me before implementing.

- [x] Notification copy and timing (Phase 3) — 7 PM; VocabMania + due-count / Words are waiting; see phase-3-cohesive.md
- [x] Compose theme / color palette for new UI — warm scholar + system dark (Phase 3 redesign)
- [ ] Exact daily goal options in onboarding (still open; not Phase 3)
