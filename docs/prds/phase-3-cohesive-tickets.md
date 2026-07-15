# Phase 3 Tickets: cohesive

**Parent PRD:** [phase-3-cohesive.md](phase-3-cohesive.md)  
**Approved:** July 13, 2026

Each ticket is a **vertical slice** — demoable end-to-end on device. Solo path: **1 → 2 → 3 → 4 → 5 → 6**. Optional: **2 ∥ 3** after Ticket 1 (coordinate `HomeScreen` ownership). See [Dependency graph & parallelization](#dependency-graph--parallelization).

---

## Ticket 1: Seal the legacy door

**Phase:** 3 · **Week:** 1 · **Est. hours:** 8–10  
**Depends on:** none

### What to build

Compose Home is the only product surface. Remove the overflow entry that opens `TestActivity`. Replace it with **Share** and **Rate** (system share sheet + Play Store intent). Fix onboarding copy that promises “change later in settings.” Legacy Activities stay in the APK but are unreachable from the new UI.

### :shared

- None required (UI / navigation only)

### :app

- `HomeScreen` overflow: remove “Legacy app”; add Share + Rate
- `OnboardingScreen`: remove “You can change this later in settings.”
- Confirm no other Compose path starts legacy Activities

### Acceptance criteria

- [x] Home overflow has no Legacy / TestActivity entry
- [x] Share opens system share with sensible app/store text
- [x] Rate opens Play Store listing (or browser fallback)
- [x] Onboarding no longer mentions settings that don’t exist
- [x] Grep/manual pass: Compose presentation layer does not launch `TestActivity`
- [ ] Runs on device/emulator per demo script

### Demo script

1. Launch app → Home → overflow menu
2. Confirm Share and Rate work; no Legacy item
3. Cold start through onboarding (clear data) — copy has no settings lie
4. Confirm full Compose path still works (Home → Review / Progress / Favorites)

### Out of scope for this ticket

- Deleting legacy Java/XML from the project
- Daily reminder, redesign, Help screen

---

## Ticket 2: Opt-in daily reminder at 7 PM

**Phase:** 3 · **Week:** 2 · **Est. hours:** 8–10  
**Depends on:** Ticket 1

### What to build

Home shows **Remind me daily at 7 PM** (off by default). Turning it on requests notification permission (Android 13+), persists preference, and schedules a local daily reminder. At fire time, notify only if due ≥ 1 **or** streak at risk (no session today); skip when clear and already practiced. Copy: title `VocabMania`; body due-count line or “Words are waiting.”

### :shared

- SQLDelight: `daily_reminder_enabled` on `user_settings` (+ migration)
- `UserSettings` / repo / use cases to get/set preference
- `ShouldNotifyToday` (pure logic): due count + last session epoch day + today → Boolean
- `DailyReminderScheduler` interface (`scheduleDaily` / `cancel`) in `commonMain`
- `commonTest` for `ShouldNotifyToday` + preference persist

### :app / androidMain

- Home toggle row; permission flow; revert off if permission denied
- `AndroidDailyReminderScheduler` + worker/receiver; notification channel; tap → `AppShellActivity`
- Implementer chooses AlarmManager vs WorkManager for doze reliability

### Acceptance criteria

- [x] Toggle off by default; no permission prompt until enable
- [x] Enable with grant → preference true + schedule registered
- [x] Enable with deny → preference/schedule stay off; no crash
- [x] Disable → cancel schedule
- [x] `ShouldNotifyToday` covered by `commonTest` (due≥1; streak at risk; skip clear+practiced)
- [ ] Device: notification content matches PRD (debug inject or wait for 7 PM acceptable)
- [ ] Runs on device/emulator per demo script

### Demo script

1. Fresh Home — toggle off
2. Turn on → accept permission → confirm scheduled (logcat / settings)
3. Deny path (revoke or deny) — toggle ends off
4. Turn off — schedule cancelled
5. Optional: force worker — due words → notification; practiced + zero due → no notify

### Out of scope for this ticket

- FCM / server push
- Time picker / custom schedule
- Settings screen
- Theme redesign

---

## Ticket 3: Warm-scholar theme + system dark

**Phase:** 3 · **Week:** 3 · **Est. hours:** 8–10  
**Depends on:** Ticket 1 (optional parallel with Ticket 2 after Ticket 1)

### What to build

Ship the warm-scholar palette: cream/paper surfaces, deep teal primary, coral/amber accent in light mode; coherent dark scheme following **system** dark. Pass main Compose screens for hierarchy/spacing so the brand reads without new illustrations or motion yet.

### :shared

- None

### :app

- Update `Color` / `Theme` — light + dark `ColorScheme`; `isSystemInDarkTheme()`
- Hierarchy/spacing pass: Onboarding, Home, Review, Progress, Favorites, Session Complete
- No in-app theme picker

### Acceptance criteria

- [x] Light theme matches warm-scholar direction (not generic default)
- [x] System dark → dark scheme applied across main screens
- [x] System light → light scheme; no crash flipping system theme
- [x] Screens remain usable (contrast, CTAs clear)
- [ ] Runs on device/emulator per demo script

### Demo script

1. Launch in light system theme — scan Home / Review / Progress / Favorites
2. Flip device to dark theme — relaunch or recreate — same screens still branded and readable
3. Complete a short review session — no contrast regressions on ratings / complete

### Out of scope for this ticket

- Empty-state illustrations
- Motion / animation set
- Reminder behavior (Ticket 2)

---

## Ticket 4: Empty-state art + intentional motion

**Phase:** 3 · **Week:** 4 · **Est. hours:** 8–10  
**Depends on:** Ticket 3

### What to build

Add bookish empty-state illustrations and the full intentional motion set so empty and transitional moments feel designed: Home WOTD/card enter, Review meaning reveal + rating press, Session complete streak settle, Progress mastery bar fill, Onboarding step transitions, reminder toggle, Favorites list enter.

### :shared

- None

### :app

- Vector/drawable empty states (no due, no favorites, no favorites due, progress empty as applicable)
- Motion on the beats listed above — subtle, non-blocking
- Prefer reusable empty/motion helpers over one-off copy-paste

### Acceptance criteria

- [x] Key empty states show illustration + copy (not blank gray only)
- [x] All PRD motion beats present and do not block taps / ratings
- [x] Motions respect reduced-motion / don’t jank low-end (reasonable best effort)
- [ ] Runs on device/emulator per demo script

### Demo script

1. Empty favorites → illustrated empty
2. Review favorites with none due → illustrated empty
3. Walk onboarding → Home (WOTD/card enter) → Review (reveal + rate) → Session complete (streak) → Progress (bar) → Favorites list
4. Toggle reminder on/off — toggle motion feels intentional

### Out of scope for this ticket

- Full design-system library beyond what screens need
- New product features

---

## Ticket 5: Signed release candidate + listing draft

**Phase:** 3 · **Week:** 5 · **Est. hours:** 8–10  
**Depends on:** Tickets 2 and 3 (Ticket 4 preferred if art/motion ready)

### What to build

Produce a **signed release** build, install on a real device, and smoke the full Compose path (onboarding → Home → review → progress → favorites → reminder → WOTD). Write a Play Store listing draft under `docs/` (title, short/full description, notes). No Play upload required.

### :shared / :app

- Fix only blockers found in RC smoke (no feature creep)

### Docs / tooling

- Use existing signing / keystore docs
- Add listing draft markdown under `docs/` (e.g. `docs/play-listing-draft.md`)
- Optional: short RC smoke checklist in that doc or `docs/rc-smoke-checklist.md`

### Acceptance criteria

- [x] Signed release APK/AAB installs on device *(emulator validated; physical optional spot-check in `docs/rc-smoke-checklist.md`)*
- [x] Full Phase 1–3 habit path smoke passes (per demo script) *(see `docs/rc-smoke-checklist.md`)*
- [x] Listing draft exists in repo with title + short + full description
- [x] No Play Console upload required for done

### Demo script

1. Build signed release; sideload to device
2. Clear data → onboarding → Home
3. Review session → session complete → Progress → Favorites
4. Reminder toggle + WOTD visible behavior check
5. Skim listing draft for accuracy vs product

### Out of scope for this ticket

- Production/internal testing track upload
- Legacy file deletion
- New features discovered as “nice to have”

**Deliverables:** `docs/play-listing-draft.md`, `docs/rc-smoke-checklist.md`, `docs/release-signing.md`, `docs/key.properties.example`; `app/build.gradle.kts` release signing via `key.properties`.

---

## Ticket 6: Finalize AI playbook + two skills

**Phase:** 3 · **Week:** 6 · **Est. hours:** 8–10  
**Depends on:** Ticket 5

### What to build

Document what actually worked. Sync VISION / AI_WORKFLOW status for Phases 2–3. Write short `docs/case-study-notes.md`. Promote **two** repeated engineering patterns to `.cursor/skills/` (candidates: KMP use case + Compose ViewModel wiring; SQLDelight + in-memory `commonTest`).

### :shared / :app

- None unless a doc references a wrong path (fix docs only)

### Docs / skills

- `docs/VISION.md`, `docs/AI_WORKFLOW.md` status tables
- `docs/case-study-notes.md`
- Two new or extended skills under `.cursor/skills/`
- Update `.cursor/rules/vocabmania.mdc` current phase if needed

### Acceptance criteria

- [ ] VISION reflects Phase 2 done / Phase 3 ticket status honestly
- [ ] AI_WORKFLOW week table matches tickets + learnings
- [ ] Case study notes exist (short, concrete)
- [ ] Two skills added/updated and discoverable via skill descriptions
- [ ] No scope creep into new app features

### Demo script

1. Open VISION + AI_WORKFLOW — status coherent
2. Open case-study-notes — readable in under 5 minutes
3. List `.cursor/skills/` — two new/updated skills present with clear when-to-use

### Out of scope for this ticket

- Long blog essay
- Deleting legacy codebase
- Play Store publish

---

## Dependency graph & parallelization

```
Ticket 1 — Seal legacy door
    │
    ├──────────────┬──────────────┐
    ▼              ▼              │
Ticket 2      Ticket 3*           │
Reminder      Theme + dark        │
    │              │              │
    │              ▼              │
    │         Ticket 4            │
    │         Art + motion        │
    │              │              │
    └──────┬───────┘              │
           ▼                      │
      Ticket 5 — Signed RC        │
           │                      │
           ▼                      │
      Ticket 6 — Playbook         │
```

\* After Ticket 1, **Ticket 2 ∥ Ticket 3** is optional for two people (or two short tracks). Solo default remains serial **1 → 2 → 3 → 4 → 5 → 6**. Assign one owner of `HomeScreen` if parallelizing 2 and 3.

| Parallel pair | Notes |
|---------------|-------|
| **#2 + #3** | Best optional pair after #1 — watch Home merge conflicts |
| **#4 art ∥ #4 motion** | Same ticket; split workstreams only after #3 |

---

## After this doc

Start implementation:

> Read `docs/VISION.md`, `docs/prds/phase-3-cohesive.md`, and **Ticket 1**. Implement end-to-end.

One ticket per week (or one chat per ticket). Reviewer hat before commit: match acceptance criteria + demo script.
