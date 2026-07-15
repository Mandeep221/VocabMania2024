# PRD: Phase 3 — Cohesive App

**Status:** approved  
**Grill session:** July 13, 2026 (Phase 3 grill-me, Questions 1–21)  
**Phase:** 3 · Weeks 1–6 (forward from Phase 2 completion; open-ended calendar)  
**Est. total hours:** ~48–60 (6 slices × 8–10 hrs/week; redesign may run longer)  
**Planning baseline:** Phase 0–2 complete in code (core loop + catalog + SM-2 + progress + favorites + WOTD + review-favorites)

## Problem Statement

Phase 2 ships a full Compose learning loop, but the product still feels unfinished:

- Home still links to the **legacy Java test hub**, so the app is two products sharing one package.
- There is **no daily habit cue** — streaks exist, but nothing reminds the user at a consistent time.
- Visual polish is early-prototype: theme tokens exist, but brand moments, dark mode, empty-state art, and motion are thin.
- Release readiness (signed RC, listing draft) and AI workflow documentation are unfinished.

Users who return daily should open **one** app, get a gentle evening nudge when it matters, and feel a cohesive warm-scholar brand through to a shippable candidate build.

## Solution

Phase 3 coheres the product in six layers:

1. **Seal the legacy door** — remove Compose → `TestActivity` entry; add Share / Rate overflow; leave legacy files in the APK for a later deletion pass.
2. **Daily reminder** — Home toggle “Remind me daily at 7 PM”; off by default; permission on enable; fire only when due ≥ 1 or streak at risk.
3. **Visual redesign (part A)** — warm-scholar light + system dark; tokens and screen hierarchy pass.
4. **Visual redesign (part B)** — bookish empty-state illustrations + full intentional motion set across key screens.
5. **Release candidate** — signed release install on a real device + Play listing draft doc (no store upload required).
6. **AI playbook finalize** — update VISION / AI_WORKFLOW; short case-study notes; promote 2 repeated build patterns to Cursor skills.

Primary user outcome: **“This is one finished habit app — it looks intentional, nudges me when I should practice, and doesn’t drag me into the old test UI.”**

## Decisions from grill-me

| # | Decision |
|---|----------|
| 1 | Priority order: **legacy seal → reminders → redesign → RC → playbook** (D) |
| 2 | Legacy: **unreachable first**; file deletion **after** Phase 3 (not this phase) |
| 3 | Reminders: **minimal** — Home toggle, fixed 7 PM, local only (B) |
| 4 | Copy: title **VocabMania**; body **due count** or “Words are waiting” (B+1) |
| 5 | Toggle placement: **Home row/card** (B) |
| 6 | Default: **off**; request `POST_NOTIFICATIONS` only when turning on (A) |
| 7 | Release prep: **signed RC on device + listing draft**; no Play upload required (A) |
| 8 | Priority if capacity ever tight: case study/skills polish → reminders → hard delete (unreachable never cut) — overridden by “no deadline” stance |
| 9 | Week/slice 6: VISION + AI_WORKFLOW sync, short case study, **2** build-pattern skills (A) |
| 10 | Notify when **due ≥ 1** or **streak at risk** (no session today); skip when clear and practiced (C) |
| 11 | No Settings editors; **remove** onboarding “change later in settings” lie (D) |
| 12–15 | Redesign: **warm scholar** + system dark + brand moments + empty-state art + **full motion set** (A/B/C from grill); no deadline pressure |
| 16 | Legacy file delete: **out of Phase 3** — unreachable only |
| 17 | Dark mode: **follow system**; no in-app theme picker (A) |
| 18 | Brand: **warm scholar** — cream/paper, deep teal, coral/amber accent, bookish empties (A) |
| 19–20 | Motions: ship **all** listed (Home/WOTD, Review reveal + ratings, Session complete, Progress bar, Onboarding, reminder toggle, Favorites enter) |
| 21 | After seal: overflow **Share + Rate**; no Help; no Legacy app (B) |
| — | Ticket order: **B** — door → reminder → redesign split (tokens/dark/screens then art/motion) → RC → playbook |

## User Stories

1. As a **daily user**, I want Home to never offer the old test app, so that I only use the new SRS flow.
2. As a **user**, I want Share and Rate from Home overflow, so that I can recommend the app without opening legacy UI.
3. As a **user**, I want an opt-in daily reminder at 7 PM, so that I remember to practice.
4. As a **user who leaves reminders off**, I want no notification spam and no permission prompt until I opt in, so that the app respects my choice.
5. As a **user with words due** (or a streak at risk), I want the 7 PM notification to fire with useful copy, so that tapping it feels purposeful.
6. As a **user with nothing due who already practiced today**, I want no reminder that day, so that I’m not nagged.
7. As a **user on Android 13+**, I want turning the toggle on to request notification permission, so that reminders can actually show.
8. As a **user who denies permission**, I want the toggle to reflect reality (stay/return off) and Home not crash, so that the setting stays honest.
9. As a **user in dark system theme**, I want the Compose app to use a coherent dark palette, so that the brand still feels warm-scholar.
10. As a **user hitting empty states**, I want bookish illustrations (not blank gray), so that empty moments feel intentional.
11. As a **user moving through onboarding → Home → Review → complete**, I want subtle motion on key beats, so that the app feels alive without noise.
12. As a **developer/demo**, I want a signed release build installable on device, so that Phase 3 has a shippable candidate.
13. As a **store-prep owner**, I want a Play listing draft doc, so that upload later is paperwork, not invention.
14. As a **future me / agent**, I want AI_WORKFLOW and two skills updated from what actually worked, so that the next phase starts faster.
15. As a **user**, I do **not** expect Help, Settings editors for level/goal, or legacy Activities to be deleted from the APK in this phase.

## Implementation Decisions

### Module map

| Module | Layer | Responsibility |
|--------|-------|----------------|
| `user_settings` schema | `commonMain` SQLDelight | Add `daily_reminder_enabled INTEGER NOT NULL DEFAULT 0` (+ migration) |
| `UserSettings` / mappers / repo | `commonMain` | Read/write reminder flag |
| `SetDailyReminderEnabledUseCase` / getter | `commonMain` | Persist preference |
| `ShouldNotifyToday` (pure logic or use case) | `commonMain` | Inputs: due count, last session epoch day, today epoch day → Boolean |
| `DailyReminderScheduler` (interface) | `commonMain` | `scheduleDaily(hour=19, minute=0)` / `cancel()` |
| `AndroidDailyReminderScheduler` + worker/receiver | `androidMain` or `:app` | Local schedule; build notification; call ShouldNotifyToday + due count |
| Home reminder row | `:app` Compose | Toggle; permission; wire use case + scheduler |
| Home overflow | `:app` Compose | Remove Legacy; add Share + Rate intents |
| Onboarding copy | `:app` Compose | Remove settings white lie |
| Theme tokens + schemes | `:app` Compose | Warm scholar light + dark; system-driven |
| Screen visual / motion pass | `:app` Compose | Hierarchy + full motion set |
| Empty-state drawables/vectors | `:app` res | Bookish illustrations |
| Release + listing draft | tooling / docs | Signed install checklist; `docs/` listing draft |
| Playbook artifacts | docs / `.cursor/skills` | VISION, AI_WORKFLOW, case-study-notes, 2 skills |

### Legacy seal (slice 1)

- Remove Home overflow “Legacy app” → `TestActivity`.
- Add “Share” (system share sheet with store/app text) and “Rate” (`market://` / Play URL fallback).
- Do **not** delete legacy Activities/XML this phase.
- Done when: no Compose navigation path reaches legacy UI.

### Daily reminder (slice 2)

- Persist `daily_reminder_enabled` in SQLDelight.
- Home UI: “Remind me daily at 7 PM” switch; off by default.
- On enable: request `POST_NOTIFICATIONS` (API 33+); if denied, keep preference/scheduler off.
- On enable success: schedule exact-ish daily local alarm/WorkManager for **19:00 local**.
- On disable: cancel schedule; preference false.
- At fire time: if `ShouldNotifyToday` → show notification; else no-op.
  - Notify if `dueCount >= 1` OR `last_session_epoch_day != today` (streak at risk / no session today).
  - Skip if due == 0 AND session already completed today.
- Notification: title `VocabMania`; body `"$dueCount words due — keep your streak going"` or `"Words are waiting"` when due is 0 but streak at risk.
- Tap opens `AppShellActivity` / Home.
- Business rule for “should notify” lives in `:shared`; Android owns permission + notification channel + schedule.

### Visual redesign (slices 3a / 3b)

**3a — tokens + dark + screen pass**
- Redefine light ColorScheme toward cream/paper surfaces, deep teal primary, coral/amber accent.
- Add dark ColorScheme; `VocabManiaTheme` uses `isSystemInDarkTheme()`.
- Pass Home, Review, Progress, Favorites, Session Complete, Onboarding for hierarchy/spacing consistency.

**3b — illustrations + motion**
- Bookish empty states (e.g. no due words, no favorites, no favorites due, progress empty).
- Motions (all): Home WOTD/card enter; Review meaning reveal; rating press; Session complete streak settle; Progress mastery bar fill; Onboarding step transitions; reminder toggle; Favorites list item enter.

### Release candidate (slice 4)

- Produce signed release APK/AAB via existing keystore/CI docs.
- Smoke-test on device: onboarding → Home → review → progress → favorites → reminder toggle → WOTD.
- Write Play listing draft (title, short/full description, notes) under `docs/` — no upload required.

### Playbook (slice 5)

- Mark Phase 2 done / Phase 3 scope in `docs/VISION.md`.
- Update `docs/AI_WORKFLOW.md` with what worked.
- Add `docs/case-study-notes.md` (short).
- Promote **2** patterns to `.cursor/skills/` (candidates: KMP use case + Compose ViewModel wiring; SQLDelight + in-memory `commonTest`).

### Architectural constraints

- Business logic in `:shared/commonMain` — including notify-or-not rules.
- Scheduling/permission/`NotificationManager` in Android layers only.
- No 1:1 ports of legacy Activities; no polish of legacy Java/XML.
- No Settings screen; no Help screen; no FCM; no in-app theme picker; no level/goal editors.

### Forward calendar (Phase 3 slices)

| Slice | Focus |
|-------|--------|
| 1 | Seal legacy door + Share/Rate |
| 2 | Daily reminder |
| 3a | Theme tokens + dark + screen pass |
| 3b | Illustrations + motion |
| 4 | Signed RC + listing draft |
| 5 | Playbook / case study / 2 skills |

## Testing Decisions

| Module | Tests |
|--------|-------|
| `ShouldNotifyToday` | `commonTest` — due≥1; streak at risk; skip when clear+practiced; timezone/epoch-day edge cases as needed |
| Reminder preference persist | `commonTest` with in-memory SQLDelight |
| Scheduler / notification | Device: enable → permission → 7 PM (or debug inject); deny permission; disable cancels |
| Legacy seal | Device: overflow has no Legacy; Share/Rate work |
| Redesign | Device smoke: light/dark system flip; empty states; motions don’t block interaction |
| RC | Device install signed build; full Phase 1–2 path |

**Prior art:** `GetProgressDashboardUseCaseTest`, `FavoriteUseCasesTest`, `GetDueWordsFavoritesOnlyTest` for in-memory SQLDelight + use case style.

## Out of Scope

- **Deleting** legacy Activities, layouts, adapters, or `MySQLiteAdapter` from the codebase
- Settings screen / editing level or daily goal after onboarding
- Help / FAQ screen
- Firebase Cloud Messaging or server push
- Play Store upload / production publish
- In-app Light/Dark/System picker
- iOS / Compose Multiplatform UI
- Polishing legacy Java/XML screens
- Changing SRS algorithm (SM-2 lite stays)

## Risks & Open Questions

| Risk | Mitigation |
|------|------------|
| Exact alarms restricted on newer Android | Prefer inexact daily + doze-friendly API; document OEM quirks; allow “open app reschedules” |
| Permission denied after toggle on | Revert UI/preference to off; optional one-line explanation |
| Redesign scope expands endlessly | Slice 3a must be shippable alone; 3b is additive |
| Due count in notification worker | Reuse shared due-count use case from Android entry; keep worker thin |
| Legacy deep links / old launcher shortcuts | AppShell stays LAUNCHER; seal Compose door; full delete later |

**Open (non-blocking):**
- Exact AlarmManager vs WorkManager choice — implementer picks with doze reliability in mind
- Illustration asset format (vector preferred)

## Further Notes

- Phase 2 tickets are implemented; update VISION status tables when playbook slice lands (or alongside this PRD).
- “No deadline” does **not** mean unscoped — slices stay vertical and demoable.
- Post–Phase 3 deletion pass can remove legacy UI once no entry points remain and RC is trusted.

### After this PRD

**Tickets:** run `to-vertical-slices` → `docs/prds/phase-3-cohesive-tickets.md`.  
**Execution:** one ticket per chat/week per `docs/AI_WORKFLOW.md`.
