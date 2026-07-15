# PRD: Phase 4 — Practice UX

**Status:** approved  
**Grill session:** July 15, 2026 (Practice UX grill-me; Home composition + session mechanics)  
**Phase:** 4 · Weeks 1–6+ (forward from Phase 3; open-ended calendar)  
**Est. total hours:** ~48–70 (materials foundation + 5 screen slices × 8–10 hrs/week; session mechanics may run long)  
**Planning baseline:** Phase 0–3 largely complete (SRS loop, catalog, SM-2 lite, progress, favorites, WOTD, reminder, warm-scholar shell, legacy sealed). Phase 3 playbook/skills slice may finish in parallel or just before this phase starts.

## Problem Statement

Phase 3 ships a cohesive warm-scholar app, but the **daily door and session still feel like a dashboard wrapped around SRS jargon**:

- Home is a stack of identical cards (stats, reminder, WOTD, progress) with no clear purpose hierarchy; the primary action sits below the fold.
- User-facing copy says **“review”** — a legacy word that meant “revise what you saw in tests.” In the new product, first-time cards appear in the same session, so “review” is confusing and dishonest for newcomers.
- The CTA surfaces the full due backlog (e.g. 669) while onboarding promised a small daily goal (10/15/20) — anxiety instead of a finishable habit.
- Session UX grades every card with Again/Hard/Good/Easy, including words the user has never seen — a memory test without a memory.
- Material roles don’t distinguish **action** (start today’s bite) from **content** (WOTD) from **utility** (reminder, progress) — everything reads as the same beige card.

Users who open VocabMania daily should immediately understand **today’s practice**, start a bite-sized session, learn new words without fake self-grading, and practice repeats with plain language — in one premium, role-clear visual system (light and dark).

## Solution

Phase 4 redesigns the habit loop around **Practice** (not Review), a **lean filled practice hero** on Home, and a **new vs repeat** session contract — applied consistently across the Compose daily loop.

Delivery order (locked):

1. **Materials / components foundation** — PracticeHero (filled commitment), PaperContent, UtilityRow; dark mode keeps the same roles.
2. **Home door** — lean hero, Practice copy, goal-sized CTA, soft queue line, caught-up empty art, demoted utilities.
3. **Practice session** — new-card learn treatment + ternary ratings for repeats; session builder (new-first + new-cap).
4. **Session complete** — one pride hero + Back to Today.
5. **Progress + Favorites** — visual + light IA (collapse card soup; quiet chrome).
6. **Onboarding** — look/copy only; steps unchanged; strip any “review” language.

Primary user outcome: **“I open the app, see today’s practice, tap once, learn new words honestly, practice repeats in plain language, and leave proud — not buried in cards or backlog guilt.”**

## Decisions from grill-me

| # | Decision |
|---|----------|
| 1 | Home job: **convert when due**; caught-up uses empty art + pride tone (hybrid D, bias to convert) |
| 2 | User-facing verb: **Practice** (not Review) across the daily habit loop UI; code/route names may stay `Review*` |
| 3 | CTA count: **N = min(due, goal)** on the verb; soft backlog only when due > goal |
| 4 | Soft backlog copy: **“{count} in your queue”** |
| 5 | Due = 0: **caught-up composition** (empty illustration + pride), not a dead CTA; quiet Favorites link only if favorites due |
| 6 | First viewport: **lean filled practice hero** (title + N + CTA; streak as quiet meta; level/goal as page chrome) |
| 7 | WOTD: **full paper brand block below the hero** (not first-viewport; not when competing with action) |
| 8 | Utilities: Reminder = switch row; Progress = chevron row; Favorites = quiet “Practice favorites”; **drop library count** from Home |
| 9 | Hero vs WOTD materials: **filled teal commitment** vs **quiet paper content** (same roles in dark, retuned) |
| 10 | Session mechanics: **new vs repeat split**; new = soft reveal → **Got it**; repeats = **Missed · Almost · Got it** |
| 11 | Rating map: Got it (new) → **GOOD**; Missed → **AGAIN**; Almost → **HARD**; Got it (repeat) → **GOOD**; **EASY unused in UI** |
| 12 | Session order: **new first**; new-cap **min(5, ceil(N/2))**; then fill with repeats up to N |
| 13 | New-card detection: **`reviewCount == 0`** (existing field; no schema change) |
| 14 | Session complete: **one pride hero** (words practiced + streak) + **Back to Today** |
| 15 | Secondary screens: **visual + light IA**; onboarding **steps fixed** |
| 16 | Scope: full-loop visual reset under one material system — not Home-only; not a big-bang unstaged branch |
| 17 | Delivery: **materials first**, then Home → Session → Complete → Progress/Favorites → Onboarding |
| 18 | Planning: this work is **Phase 4** with a full PRD → vertical slices (not a Phase 3 add-on) |

## User Stories

1. As a **daily user with words due**, I want a first-viewport practice hero with a clear Start action sized to my daily goal, so that I know what to do in seconds.
2. As a **user with a large backlog**, I want to see “Practice N today” plus a soft “{count} in your queue” line, so that I’m honest about debt without making debt the hero.
3. As a **user with fewer due than my goal**, I want the CTA to promise only the words actually available, so that the product never overpromises.
4. As a **user who is caught up**, I want a proud empty-state moment (not a disabled button), so that finishing feels like success.
5. As a **new learner**, I want first-seen words to use a learn treatment (reveal → Got it), so that I’m not graded on a memory I don’t have.
6. As a **returning learner**, I want Missed / Almost / Got it on repeats, so that I can rate memory in plain language.
7. As a **user mid-session**, I want new cards first (with a light new-cap), so that learn vs practice feels intentional and sessions don’t become all first exposures after a big catalog import.
8. As a **user finishing a session**, I want one completion moment and a single path back to Today, so that exit isn’t another dashboard.
9. As a **user browsing Home**, I want Reminder, Progress, and Favorites as quiet utilities (not fake content cards), so that purpose stays clear.
10. As a **user in dark mode**, I want the same action-vs-content material roles, so that the redesign doesn’t collapse back into uniform surfaces at night.
11. As a **user reading any daily-loop screen or notification**, I want Practice language (not Review), so that the product speaks one concept.
12. As a **user on Progress / Favorites / Onboarding**, I want the same material language and stripped review jargon, without relearning those flows’ steps.
13. As a **developer**, I want session building and rating maps tested in `:shared` `commonTest`, so that new-cap and mappings don’t regress silently.

## Implementation Decisions

### Module map

| Module | Layer | Responsibility |
|--------|-------|----------------|
| `BuildPracticeSessionUseCase` (or extend `GetDueWordsUseCase`) | `commonMain` | Build ordered session: fetch candidates, classify new vs repeat, apply new-cap, pad with repeats to N |
| Practice session models | `commonMain` | e.g. `PracticeCard` / `isNew` on due words for UI; keep `ReviewRating` as SRS wire format |
| Rating / label mapping | `commonMain` | Map UI actions → `ReviewRating`; pure functions unit-tested |
| `ApplyReviewRatingUseCase` | `commonMain` | Unchanged scheduler entry; still receives domain ratings |
| Reminder notification copy | `:app` / shared strings | Practice-oriented body; keep ShouldNotifyToday rules |
| Theme materials | `:app` Compose | `PracticeHero`, `PaperContentCard`, `UtilityRow` (+ dark) |
| Home | `:app` | Lean hero, queue line, caught-up, utility rows, WOTD paper |
| Practice session UI | `:app` (`ReviewScreen` / VM) | Branch new vs repeat; ternary strip; copy rename |
| Session complete | `:app` | Single pride hero; one CTA |
| Progress / Favorites | `:app` | Visual + light IA pass |
| Onboarding | `:app` | Visual + copy pass; same steps |
| Interval / feedback copy | `commonMain` / UI | Prefer “practice” / “next” wording over user-facing “review” where shown |

### Session builder contract

```
N = min(dueCount, dailyGoal)
newCap = min(5, ceil(N / 2.0))

candidates = due words for level (favoritesOnly if that path), enough to fill after ordering
newCards = candidates where reviewCount == 0, take up to newCap
repeatCards = candidates where reviewCount > 0
session = newCards + repeatCards, truncated to N
```

- Prefer querying/filtering in `:shared` so ViewModels don’t reimplement caps.
- Favorites-only practice uses the **same** new vs repeat UX and builder rules.
- If not enough words after capping news, session may be &lt; N (honest length).

### Home composition (due &gt; 0)

```
Today
{Level} level · goal {G} words/day

┌── Practice hero (filled teal) ─────────────┐
│  Today’s practice                            │
│  quiet streak meta                           │
│  Practice {N} today                          │
│  {due} in your queue     ← only if due > G   │
│  [ Start today’s practice ]                  │
└──────────────────────────────────────────────┘

Remind me daily at 7 PM              [switch]   ← utility row
── Word of the day (paper) ──
Your progress                         ›         ← utility row
Practice favorites                    ›         ← quiet; show due if > 0
```

### Home composition (due = 0)

- Caught-up empty illustration as focal + short pride tone (streak).
- No primary practice CTA (unless product later adds intentional extra practice — out of scope).
- Quiet “Practice favorites” only if favorite due count &gt; 0.
- WOTD remains below as paper brand (not promoted to replace the empty moment).

### Practice session UI

| Card kind | Flow | Actions |
|-----------|------|---------|
| New (`reviewCount == 0`) | Word → tap reveal meaning/example → single CTA | **Got it** → `GOOD` |
| Repeat | Word → tap reveal → ternary strip | **Missed / Almost / Got it** → `AGAIN` / `HARD` / `GOOD` |

- Remove Easy from the visible strip (domain enum may remain).
- Screen titles / empty copy: Practice / Practice favorites (not Review).
- Keep existing motion vocabulary where it still fits; don’t invent a second motion system.

### Session complete

- Headline: Practice complete (or equivalent pride line).
- Show words practiced + streak payoff in one composition (not multi-card summary soup).
- Single CTA: **Back to Today**.
- No secondary Favorites CTA on this screen in Phase 4.

### Secondary screens (light IA)

- **Progress:** Keep level tabs and existing metrics; collapse redundant full-width card stack into clearer hierarchy; Practice wording in empty states.
- **Favorites:** Same list capability; quieter chrome; entry to favorites practice uses Practice copy.
- **Onboarding:** Same welcome → level → goal steps; restyle + copy; remove any review jargon / settings white lies if any remain.

### Architectural constraints

- Business logic (session order, caps, rating maps) in `:shared/commonMain` — never only in Composables.
- No SQLDelight schema change required for new detection.
- Do not modify legacy Java/XML.
- Do not rename navigation route constants unless necessary; user-facing strings change first.
- SM-2 lite scheduler formulas stay; UI maps into existing `ReviewRating`.

## Testing Decisions

### `commonTest` (required)

- Session builder: N capping, new-first order, `min(5, ceil(N/2))` new-cap, mix of new/repeat, favoritesOnly path, insufficient candidates.
- Rating / label mapping: new Got it → GOOD; ternary → AGAIN/HARD/GOOD.
- Regression: existing SM-2 / due-count tests still pass.

Prior art: `GetDueWordsFavoritesOnlyTest`, `Sm2LiteScheduler` tests, `SqlDelightReviewRepositoryTest`.

### Device / demo script

1. Home with due ≥ goal: hero shows Practice N; soft queue when backlog exists; CTA starts session of length N.
2. Home with 0 &lt; due &lt; goal: CTA shows actual due count; no queue line.
3. Home due = 0: caught-up empty; no dead primary CTA.
4. Session with new + repeats: news first (within cap); new cards = reveal → Got it; repeats = Missed/Almost/Got it.
5. Session complete: one hero; Back to Today returns to redesigned Home.
6. Dark mode: filled hero vs paper WOTD roles still distinct.
7. Reminder notification copy uses Practice-oriented language (fire rules unchanged).
8. Progress / Favorites / Onboarding: no user-facing “review”; materials feel related to Home.

## Out of Scope

- Deleting legacy Activities/XML from the APK (post–Phase 3 deletion pass remains separate).
- Reintroducing Easy (or long-press Too easy) in the Practice UI.
- Changing onboarding steps or daily goal option set (goal values still an open VISION question).
- Rewriting SM-2 lite interval math.
- Firebase catalog / WOTD backend changes.
- Settings screens for level/goal editing.
- Second CTA on session complete (Practice favorites).
- Big-bang rename of all `Review*` code/types/routes (optional follow-up).
- Play Store upload / listing rewrite.
- iOS / Compose Multiplatform.

## Risks & Open Questions

| Risk / question | Mitigation |
|-----------------|------------|
| New-cap + new-first changes due “fairness” vs pure `next_review_at` order | Documented product choice; unit-test builder; revisit only if repeats starve |
| Large catalog import → many `reviewCount == 0` due | New-cap protects session quality |
| Phase 3 playbook slice still pending | Finish or park; Phase 4 does not depend on skills docs |
| Interval formatter still says “Next review…” | Include in loop string pass or accept tiny jargon leak — prefer fix in session feedback |
| Filled teal hero contrast / a11y in light and dark | Tune tokens in materials slice; verify on device |
| Users used to four Anki buttons | Ternary is intentional; no in-app tutorial required in Phase 4 |

## Further Notes

- Phase 3 made the app *cohesive*; Phase 4 makes the habit loop *conceptually honest* and *visually purposeful*.
- Prefer deep module for session building in `:shared` so Home and Practice ViewModels stay thin.
- When implementing, one chat per slice per `docs/AI_WORKFLOW.md`.
- After PRD approval in repo, run **`to-vertical-slices`** to produce `docs/prds/phase-4-practice-ux-tickets.md` and the week table.
