# PRD: Phase 2 — Deepen the Learning Loop

**Status:** approved  
**Grill session:** July 9, 2026 (Phase 2 grill-me, Questions 1–10)  
**Phase:** 2 · Weeks 1–7 (forward calendar from Phase 1 completion)  
**Est. total hours:** ~56–70 (7 weeks × 8–10 hrs/week)  
**Planning baseline:** Phase 1 complete; ~12 weeks total runway remaining (Phase 2 + Phase 3)

## Problem Statement

Phase 1 delivers a daily spaced-repetition (SRS) habit loop, but the app still feels like an early prototype:

- Only **15 seed words** exist locally — not enough for meaningful progress or long-term scheduling.
- The **simple SRS scheduler** uses fixed intervals; it does not adapt to how well the user knows each word.
- Users cannot **see growth** — no mastery view, no activity history beyond streak and due count on Home.
- **Favorites** and **word of the day (WOTD)** live only in the legacy Java app, not the new Compose experience.

Users who return daily need to **feel improvement** (motivation) while reviews become **smarter over time** (SuperMemo 2-style / SM-2 lite engine underneath).

## Solution

Phase 2 deepens the learning loop in four layers:

1. **Firebase bulk import** — one-time download of the full legacy word catalog into SQLDelight; 15-word seed as offline fallback.
2. **SM-2 lite spaced repetition** — replace simple scheduler with ease-factor-based intervals; preserve existing review history.
3. **Combined progress dashboard** — mastery summary on top, activity and streak below, Easy / Medium / Tough level tabs (default: user’s onboarding level).
4. **Favorites + WOTD** — save words from review, browse favorites in Compose; daily word card on Home with offline cache.

Primary user outcome: **“I can see I’m improving, and reviews feel appropriately spaced.”**

## Decisions from grill-me

| # | Decision |
|---|----------|
| 1 | Primary outcome: **motivation & progress (B)**; **SM-2 lite (A)** as engine underneath |
| 2 | Progress dashboard: **combined (D)** — mastery top, activity + streak below |
| 3 | Dashboard level tabs: **tabs defaulting to selected level (B)** |
| 4 | Word catalog: **Firebase bulk import first (A)**; **15-word seed fallback (D)** |
| 5 | SM-2 lite: **minimal + preserve existing review history (B)** |
| 6 | Favorites: **save from review + list screen (A)**; stretch: **review favorites only (B)** |
| 7 | WOTD: **Home card (A)** with offline/cached fallback |
| 8 | If time slips: **defer WOTD first (A)** — never cut Firebase import |
| 9 | Forward calendar: **12 weeks from Phase 1 completion** (this PRD covers Phase 2 weeks 1–7) |

## User Stories

1. As a **new user after Phase 2 ships**, I want the app to download the full vocabulary catalog on first launch, so that I have enough words for daily review beyond the 15-word seed.
2. As a **user with poor connectivity**, I want the app to fall back to seed words when Firebase fails, so that I can still open and use the app.
3. As a **returning user**, I want review intervals to adapt when I rate Again / Hard / Good / Easy, so that words I struggle with appear sooner and known words appear later.
4. As a **user who reviewed words in Phase 1**, I want my existing review progress preserved when SM-2 lite ships, so that I am not reset to day zero.
5. As a **daily user**, I want a progress dashboard showing mastery, recent activity, and streak, so that I feel improvement over time.
6. As a **user at one difficulty level**, I want the dashboard to default to my level with tabs for others, so that progress is meaningful per track.
7. As a **user reviewing a word**, I want to favorite it with one action, so that I can revisit words I care about.
8. As a **user**, I want a favorites list with search, so that I can find saved words quickly.
9. As a **user (stretch)**, I want to review only my favorited due words, so that I can focus on personal vocabulary.
10. As a **daily user**, I want to see word of the day on Home, so that each open feels fresh.
11. As a **user offline**, I want the last cached word of the day or a graceful hide, so that Home does not break without network.
12. As a **user**, I want legacy favorites and test flows still reachable until removed in Phase 3, so that nothing regresses during migration.

## Implementation Decisions

### Module map

| Module | Layer | Responsibility |
|--------|-------|----------------|
| `WordCatalogRepository` (interface) | `commonMain` | Trigger import, report catalog status, word counts |
| `FirebaseWordCatalogImporter` | `androidMain` | One-time pull from `/vocabmania/questions`; map MCQ → word + meaning + usage |
| `SpacedRepetitionScheduler` (interface) | `commonMain` | Abstraction over simple vs SM-2 lite |
| `Sm2LiteScheduler` | `commonMain` | Ease factor, interval calculation, quality mapping from ReviewRating |
| `ReviewRepository` / `ApplyReviewRatingUseCase` | `commonMain` | Wire to new scheduler |
| `ProgressRepository` / progress use cases | `commonMain` | Mastery %, activity counts, per-level aggregates |
| `ToggleFavoriteUseCase` | `commonMain` | Set/clear `is_favorite` on word |
| `GetFavoritesUseCase` | `commonMain` | List favorite words |
| `WordOfTheDayRepository` (interface) | `commonMain` | Get/cache WOTD |
| `FirebaseWordOfTheDayFetcher` | `androidMain` | Pull from `/vocabmania/randomize` (legacy path) |
| `ProgressScreen` + ViewModel | `:app` Compose | Dashboard UI with level tabs |
| `FavoritesScreen` + ViewModel | `:app` Compose | List + search |
| Home / Review updates | `:app` Compose | WOTD card; favorite toggle on review |

### Firebase bulk import (Week 1)

- **Source:** Firebase Realtime Database `https://boiling-torch-469.firebaseio.com`, path `/vocabmania/questions`
- **Legacy record shape:** `word`, `question` (usage), `op1`/`op2`/`op3`, `answer` (1–3), `level_attempt` (e.g. `E_1`, `M_3`)
- **Mapping:** `meaning` = option matching `answer`; `usage_example` = `question`; `level` derived from `level_attempt` prefix (E/M/T); store `firebase_level_attempt` on `word` row (column exists)
- **Behavior:** One-time full download on first launch after Phase 2; insert words + create `review_card` rows for new words; `INSERT OR IGNORE` for duplicates
- **Fallback:** If Firebase unreachable, keep/use 15-word `SeedCatalog`; mark import state in `migration_state` table
- **Import state key:** e.g. `firebase_word_catalog_import`

### SM-2 lite spaced repetition (Week 2)

- **Schema:** Add `ease_factor REAL NOT NULL DEFAULT 2.5` to `review_card`
- **Scheduler:** `Sm2LiteScheduler` in `commonMain`; map ReviewRating → SM-2 quality (e.g. Again=1, Hard=3, Good=4, Easy=5)
- **History preservation:** Migration sets `ease_factor = 2.5` for existing cards; derive initial interval from existing `interval_days` / `review_count` rather than resetting `next_review_at` to now
- **Replace** `SimpleSrsScheduler` as default in `SqlDelightReviewRepository` (keep simple scheduler in codebase for tests/reference or delete after cutover)
- **Interface:** `SpacedRepetitionScheduler` with single `schedule(...)` method — enables future algorithm swaps

```kotlin
// Decision-rich contract (illustrative)
interface SpacedRepetitionScheduler {
    fun schedule(
        rating: ReviewRating,
        nowEpochMillis: Long,
        currentIntervalDays: Double,
        currentEaseFactor: Double,
        reviewCount: Int,
    ): ReviewSchedule // includes nextReviewAt, intervalDays, easeFactor, reviewCount
}
```

### Progress dashboard (Weeks 3–4)

**Week 3 — activity layer:**
- Words reviewed in last 7 days (global + per level)
- Current / longest streak (reuse `user_settings`)
- Simple 7-day activity indicator (dots or bar)
- Navigation route from Home (e.g. bottom nav, toolbar, or card tap — implementer chooses; add route to `Routes`)

**Week 4 — mastery layer:**
- **Maturity definition:** word is “mature” when `interval_days >= 21` (configurable constant in `commonMain`)
- Mastery % = mature cards / total cards with review history, per level
- Level tabs: Easy / Medium / Tough; default tab = `user_settings.selected_level`
- Queries in SQLDelight or computed in use case from `review_card` + `word`

### Favorites (Week 5)

- Toggle on `ReviewScreen` (icon/button); calls `ToggleFavoriteUseCase`
- `FavoritesScreen`: list from `GetFavoritesUseCase`; search by word text (client-side filter acceptable for v1)
- Remove legacy **FavoritesActivity** entry from overflow when Compose favorites ships

**Stretch (Week 6):** Home or Review entry “Review favorites only” — `getDueWords` filtered to `is_favorite = 1`

### Word of the day (Week 6)

- **Source:** `/vocabmania/randomize` (legacy `SplashActivity` shape: `word`, `meaning`, `usage` among cap/update nodes)
- **UI:** Card on `HomeScreen` below streak/due section
- **Cache:** Store in SQLDelight (new `word_of_the_day` table or columns on `user_settings`) with `fetched_at` epoch; show cached when offline
- **Cut priority:** First item to defer if Phase 2 slips

### Navigation

- New routes: `PROGRESS`, `FAVORITES`; wire in `AppNavHost`
- Home → Progress entry point
- Overflow: keep Legacy app until Phase 3

### Architectural constraints

- Business logic in `:shared/commonMain`
- Firebase implementations in `:androidMain` only
- No 1:1 ports of legacy Activities
- No polish on legacy Java/XML except removing overflow links when Compose replaces a flow

## Testing Decisions

| Module | Tests |
|--------|-------|
| `Sm2LiteScheduler` | `commonTest` — interval/ease progression, Again resets, preserve-history migration helper |
| `SimpleSrsScheduler` | Keep existing tests until removed |
| Firebase mapping | `androidTest` or unit test with fixture JSON (optional Week 1) |
| Progress use cases | `commonTest` with in-memory SQLDelight driver — maturity %, per-level counts |
| Device | Demo script per vertical ticket (see `to-vertical-slices`) |

**Prior art:** `SimpleSrsSchedulerTest.kt` in `shared/src/commonTest`

## Out of Scope

- **Phase 3 work:** deleting legacy Activities, push notifications, Play Store release
- **Full Anki SM-2:** leeches, graduating intervals, daily limits, cram mode
- **Periodic Firebase sync** — one-time import only in Phase 2
- **iOS targets** or Compose Multiplatform shared UI
- **Firebase import in `commonMain`** — Android-only for Phase 2
- **Rebuilding legacy test-score graphs** — progress is SRS-native, not test marks
- **Polishing legacy Java/XML screens**

## Risks & Open Questions

| Risk | Mitigation |
|------|------------|
| Firebase catalog size / download time | Progress indicator during import; background coroutine on app start |
| MCQ → meaning mapping errors | Log malformed records; skip bad rows; spot-check counts vs legacy |
| SM-2 migration surprises for Phase 1 users | Unit tests for migration; manual test with existing `vocabmania.db` |
| 12-week assumption slips | Defer WOTD first; then dashboard polish; never cut Firebase import |
| `mattpocock`-style GitHub agent pipeline | Not required; repo-local tickets only |

**Open (non-blocking):**
- Exact Home → Progress navigation pattern (card vs bottom nav)
- WOTD cache schema preference (dedicated table vs settings columns)

## Further Notes

### Forward calendar (Phase 2 portion)

| Week (from now) | Focus |
|-----------------|-------|
| 1 | Firebase bulk import |
| 2 | SM-2 lite spaced repetition |
| 3 | Progress dashboard — activity + streak |
| 4 | Progress dashboard — mastery + level tabs |
| 5 | Favorites |
| 6 | WOTD + stretch (review favorites only) |
| 7 | Phase 2 buffer / polish |

Weeks 8–12 reserved for **Phase 3** (separate PRD later).

### Legacy reference

| Legacy | Phase 2 replacement |
|--------|---------------------|
| `QuestionsActivity` Firebase fetch | `FirebaseWordCatalogImporter` |
| `SplashActivity` WOTD fetch | `FirebaseWordOfTheDayFetcher` + Home card |
| `MainActivity` graphs | Progress dashboard |
| `FavoritesActivity` | `FavoritesScreen` |

### After this PRD

**Tickets:** [phase-2-deepen-tickets.md](phase-2-deepen-tickets.md) (6 vertical slices, approved July 9, 2026).  
**Execution tracker:** `docs/AI_WORKFLOW.md` week table synced to tickets.
