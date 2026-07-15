# Phase 4 Tickets: practice-ux

**Parent PRD:** [phase-4-practice-ux.md](phase-4-practice-ux.md)  
**Approved:** July 15, 2026

Each ticket is a **vertical slice** — demoable end-to-end on device. Solo path: **1 → 2 → 3 → 4 → 5 → 6**.

---

## Ticket 1: Prefactor — ship Practice materials

**Phase:** 4 · **Week:** 1 · **Est. hours:** 8–10  
**Depends on:** none

### What to build

Introduce the Phase 4 material system so later screens stop inventing beige cards. Ship three reusable Compose surfaces with light/dark **role contrast**:

- **PracticeHero** — filled commitment surface (deep/luminous teal field, light type, room for title / meta / CTA slot)
- **PaperContent** — quiet paper card for editorial content (WOTD)
- **UtilityRow** — chrome-light row for settings/nav (switch or chevron)

Prove on device by wiring Home minimally: Reminder → UtilityRow, WOTD → PaperContent, and a thin PracticeHero stub (existing CTA/copy OK for this ticket). Stats card soup can remain until Ticket 2.

### :shared

- None required (tokens/components only)

### :app

- Theme tokens / component APIs under Compose theme or `components/` (match existing `VocabDimens` / color patterns)
- Dark: same action-vs-content roles, retuned (not flat identical cards)
- Home: apply Reminder + WOTD + hero stub to prove materials without full IA rewrite

### Acceptance criteria

- [ ] `PracticeHero`, `PaperContent`, `UtilityRow` exist and are usable from Home
- [ ] Light and dark: filled hero vs paper content remain visually distinct
- [ ] Reminder renders as utility row (not a content card twin)
- [ ] WOTD renders as paper content
- [ ] Runs on device/emulator per demo script
- [ ] No scope creep into full Home IA / Practice rename / session mechanics (PRD Out of Scope for later tickets)

### Demo script

1. Launch app → Home (light mode)
2. Confirm Reminder is a quiet utility row; WOTD sits on paper; hero stub is filled teal (not another beige card)
3. Toggle system dark → relaunch/resume — roles still distinct
4. Reminder toggle and WOTD still function

### Out of scope for this ticket

- Full Home composition (N/queue/caught-up/drop library count)
- Practice rename across the loop
- Session new/repeat mechanics
- Session complete / Progress / Favorites / Onboarding restyle

---

## Ticket 2: Redesign the Home door

**Phase:** 4 · **Week:** 2 · **Est. hours:** 8–10  
**Depends on:** Ticket 1

### What to build

Home becomes a **daily doorway**, not a metric dashboard. First viewport = lean PracticeHero with **N = min(due, goal)**, quiet streak meta, level/goal as page chrome, primary **Start today’s practice**. Soft **“{count} in your queue”** only when due &gt; goal. Due = 0 → caught-up empty illustration + pride tone (no dead primary CTA); quiet Practice favorites only if favorites due. Drop library count. Progress and Favorites become utility/chevron rows. User-facing Practice copy on Home; reminder notification body uses Practice-oriented language (fire rules unchanged).

### :shared

- Thin helpers OK if useful (e.g. expose `sessionBiteSize = min(due, goal)` for Home) — no session builder yet (Ticket 3)
- Prefer keeping display math testable if non-trivial

### :app

- `HomeScreen` / `HomeViewModel`: remove stacked StatCards; compose PRD Home layout
- Caught-up: existing empty illustration + pride tone
- Reminder stays UtilityRow; WOTD stays PaperContent below hero
- Overflow Favorites can remain; primary Favorites entry on Home is quiet row
- Notification copy string update (Practice language)

### Acceptance criteria

- [ ] Due ≥ goal: hero shows Practice N (= goal); queue line when due &gt; goal; CTA starts session
- [ ] 0 &lt; due &lt; goal: CTA/N use actual due; no queue line
- [ ] Due = 0: caught-up empty; no disabled “No words due” primary button
- [ ] No “Words in library” on Home
- [ ] Progress / Favorites / Reminder are utilities, not content-card twins
- [ ] No user-facing “review” on Home (or reminder notification body)
- [ ] Runs on device/emulator per demo script

### Demo script

1. Home with backlog ≫ goal — hero “Practice {goal} today”, soft queue count, Start opens session
2. Simulate/use state with few due — N matches due; no queue line
3. Clear due (or caught-up state) — empty pride moment; no dead CTA
4. Open Progress and Practice favorites from quiet rows
5. Dark mode spot-check: hero vs WOTD roles still clear

### Out of scope for this ticket

- Session builder / new vs repeat UX (Ticket 3)
- Session complete redesign (Ticket 4)
- Progress/Favorites deep restyle (Ticket 5)
- Onboarding (Ticket 6)

---

## Ticket 3: Practice session — new vs repeat

**Phase:** 4 · **Week:** 3 · **Est. hours:** 8–10  
**Depends on:** Ticket 2

### What to build

Practice sessions become conceptually honest. `:shared` builds the ordered bite: **N = min(due, goal)**; **new first** (`reviewCount == 0`); new-cap **min(5, ceil(N/2))**; then repeats. UI: new cards = soft reveal → **Got it** → `GOOD`; repeats = reveal → **Missed · Almost · Got it** → `AGAIN` / `HARD` / `GOOD`. Remove Easy from visible strip. Screen titles / empties use Practice language. Favorites-only path uses the same builder + UX. Prefer fixing interval feedback copy that still says “review.”

### :shared

- `BuildPracticeSessionUseCase` (or equivalent extension of due-word loading)
- Practice card kind / `isNew` for UI
- Pure rating/label mapping → `ReviewRating`
- `commonTest`: N cap, new-first, new-cap formula, mixed/insufficient candidates, favoritesOnly, rating maps
- Wire through `VocabManiaShared` for ViewModels

### :app

- `ReviewViewModel` / `ReviewScreen`: consume builder; branch new vs repeat UI
- Titles: Practice / Practice favorites
- Keep existing motion where it still fits

### Acceptance criteria

- [ ] Session length respects N; new cards appear before repeats (within new-cap)
- [ ] New-cap = `min(5, ceil(N/2))` covered by `commonTest`
- [ ] New: reveal → Got it schedules as GOOD
- [ ] Repeat: Missed/Almost/Got it map to AGAIN/HARD/GOOD; no Easy button
- [ ] Favorites-only practice follows same rules
- [ ] User-facing session chrome avoids “review” where shown
- [ ] Runs on device/emulator per demo script

### Demo script

1. From Home Start → session; confirm length ≈ N
2. First cards are new (if any unseen due) with Got it only after reveal
3. Later cards show Missed / Almost / Got it
4. Rate through a short session → session complete still reachable
5. Practice favorites path (if favorites due) — same UX patterns

### Out of scope for this ticket

- Session complete visual redesign (Ticket 4)
- Progress / Favorites / Onboarding restyle
- Reintroducing Easy; changing SM-2 math; schema changes

---

## Ticket 4: Session complete pride hero

**Phase:** 4 · **Week:** 4 · **Est. hours:** 8–10  
**Depends on:** Ticket 3

### What to build

End the loop the way Home starts it: **one composition**. Replace summary card soup with a pride hero — Practice complete (or equivalent), words practiced + streak payoff, single CTA **Back to Today**. Use Practice materials from Ticket 1.

### :shared

- None required unless copy helpers for summary labeling; `CompleteReviewSessionUseCase` behavior stays

### :app

- `SessionCompleteScreen`: one hero layout; Practice copy; single Done/Back to Today
- No secondary Practice favorites CTA

### Acceptance criteria

- [ ] One primary visual composition (not multi-card dashboard)
- [ ] Shows practiced count + streak payoff
- [ ] Single CTA returns to Home
- [ ] Practice wording (no “review” in user-facing complete chrome)
- [ ] Runs on device/emulator per demo script

### Demo script

1. Complete a Practice session
2. Confirm pride hero + streak; only Back to Today
3. Land on redesigned Home

### Out of scope for this ticket

- Progress / Favorites / Onboarding
- Extra CTAs, share sheet, achievements

---

## Ticket 5: Progress + Favorites visual / light IA

**Phase:** 4 · **Week:** 5 · **Est. hours:** 8–10  
**Depends on:** Ticket 1 (materials); implement after Ticket 4 in solo path

### What to build

Apply Phase 4 materials and light IA so secondary surfaces match the habit loop. **Progress:** keep level tabs and metrics; collapse redundant full-width card stack; Practice wording in empty states. **Favorites:** quieter chrome; entry to practice uses Practice copy. No feature additions.

### :shared

- None required (copy-only if any shared strings)

### :app

- `ProgressScreen`: hierarchy pass with Paper/utility patterns as appropriate
- `FavoritesScreen`: quieter list chrome; Practice favorites CTA/copy
- Strip user-facing “review” on these screens

### Acceptance criteria

- [ ] Progress no longer reads as identical beige card inventory
- [ ] Favorites chrome quieter; Practice language for practice entry
- [ ] Level tabs / favorites list behavior unchanged
- [ ] Dark mode acceptable with material roles
- [ ] Runs on device/emulator per demo script

### Demo script

1. Home → Progress — scan hierarchy; empty state copy if applicable
2. Favorites — open list; start Practice favorites if due
3. Dark mode spot-check

### Out of scope for this ticket

- New progress metrics / charts
- Onboarding restyle (Ticket 6)
- Changing favorites data model

---

## Ticket 6: Onboarding look / copy

**Phase:** 4 · **Week:** 6 · **Est. hours:** 8–10  
**Depends on:** Ticket 1; implement after Ticket 5 in solo path

### What to build

Restyle onboarding with Practice materials / warm-scholar consistency. **Steps unchanged:** welcome → level → daily goal. Copy pass: no review jargon; no settings white lies. Goal option set unchanged (VISION open question stays open).

### :shared

- None required (`CompleteOnboardingUseCase` unchanged)

### :app

- `OnboardingScreen` visual + copy pass
- Confirm navigation into redesigned Home still works

### Acceptance criteria

- [ ] Same three-step flow
- [ ] Visual language related to Home materials
- [ ] No user-facing “review” or nonexistent-settings promises
- [ ] Completing onboarding lands on Ticket-2 Home
- [ ] Runs on device/emulator per demo script

### Demo script

1. Clear app data → cold start onboarding
2. Walk welcome → level → goal
3. Arrive at Home practice hero
4. Dark mode optional spot-check

### Out of scope for this ticket

- Changing goal values / adding settings
- Session or Home behavior changes
- Legacy deletion

---

## Dependency graph

```
1 (materials)
 └──► 2 (Home)
       └──► 3 (session)
             └──► 4 (complete)
                   └──► 5 (progress + favorites)
                         └──► 6 (onboarding)
```

Tickets 5–6 technically need only Ticket 1 for materials, but **solo execution stays linear** so the product never ships a half-migrated loop.
