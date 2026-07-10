# Phase 2 Tickets: deepen

**Parent PRD:** [phase-2-deepen.md](phase-2-deepen.md)  
**Approved:** July 9, 2026

Each ticket is a **vertical slice** — demoable end-to-end on device. Not every ticket is parallelizable; see [Dependency graph & parallelization](#dependency-graph--parallelization) below.

---

## Ticket 1: Real vocabulary library

**Phase:** 2 · **Week:** 1 · **Est. hours:** 8–10  
**Depends on:** none (foundational wave)

### What to build

On first launch after this ships, the app downloads the full Firebase word catalog into SQLDelight. Home shows catalog status (e.g. word count, import in progress, or seed fallback). Review sessions draw from imported words — not just the 15-word seed. If Firebase is unreachable, the app keeps working with `SeedCatalog` and records import state so a retry can happen later.

### :shared

- `WordCatalogRepository` interface in `commonMain`
- `ImportWordCatalogUseCase` — trigger import, report status
- `FirebaseWordCatalogImporter` in `androidMain` — pull `/vocabmania/questions`, map MCQ → word/meaning/usage/level
- `migration_state` key `firebase_word_catalog_import` (pending / complete / failed)
- Insert words + `review_card` rows for new words; `INSERT OR IGNORE` for duplicates
- Wire import into `SharedBootstrap.initialize()` (background coroutine)
- Optional: fixture JSON unit test for mapping logic

### :app

- `HomeScreen` / `HomeViewModel` — show total word count and import status (spinner / “Using offline catalog”)
- No new routes required; existing Review flow must surface imported words automatically via `GetDueWordsUseCase`

### Acceptance criteria

- [ ] Fresh install with network: Firebase catalog imports; Home shows word count ≫ 15
- [ ] Airplane mode on first launch: app uses 15-word seed; Home indicates offline/fallback state
- [ ] Review session includes words from imported catalog (not seed-only)
- [ ] Import state persisted in `migration_state`; re-launch does not re-download full catalog if complete
- [ ] Malformed Firebase rows skipped without crashing; import completes with partial success
- [ ] Runs on device/emulator per demo script

### Demo script

1. Clear app data; enable network; launch app
2. Wait for import (watch Home status / logcat)
3. Confirm Home word count is large (hundreds+)
4. Start Review — words should not repeat only the 15 seed entries
5. Clear data again; enable airplane mode; launch — Home shows fallback; Review still works with seed words

### Out of scope for this ticket

- SM-2 lite scheduling changes
- Progress dashboard, favorites, WOTD
- Periodic Firebase sync (one-time import only)

---

## Ticket 2: Reviews adapt to ratings

**Phase:** 2 · **Week:** 2 · **Est. hours:** 8–10  
**Depends on:** Ticket 1

### What to build

Review intervals adapt to Again / Hard / Good / Easy using SM-2 lite (ease factor + interval math). Existing Phase 1 review history is preserved — no reset to day zero. After rating a word, Review or Session Complete shows the **next interval** (e.g. “Next review in 6 days”) so the user sees scheduling change on device.

### :shared

- Add `ease_factor REAL NOT NULL DEFAULT 2.5` to `review_card` (SQLDelight migration)
- `SpacedRepetitionScheduler` interface; `Sm2LiteScheduler` implementation
- Map `ReviewRating` → SM-2 quality (Again=1, Hard=3, Good=4, Easy=5)
- Migration: set `ease_factor = 2.5` on existing cards; derive interval from existing `interval_days` / `review_count` — do not reset `next_review_at` to now
- Wire `SqlDelightReviewRepository` / `ApplyReviewRatingUseCase` to new scheduler
- `commonTest`: interval progression, Again resets interval, history-preservation migration helper

### :app

- `ReviewScreen` or `SessionCompleteScreen` — display next interval after rating (read from schedule result or card state)
- Optional: subtle label on Review card (“Interval: X days”) for current card

### Acceptance criteria

- [ ] Rate Easy on a new word — next interval visibly longer than Again on same word
- [ ] Existing Phase 1 `review_card` rows survive app upgrade; due queue not wiped
- [ ] `Sm2LiteScheduler` covered by `commonTest`
- [ ] `SimpleSrsScheduler` removed from production path (may keep in tests/reference)
- [ ] Runs on device/emulator per demo script

### Demo script

1. Upgrade from Phase 1 DB (or complete a few Phase 1 reviews first)
2. Note due count and a specific word’s next review timing
3. Update app with this ticket; launch — due words and progress still present
4. Review one word → rate **Again** — note short next interval shown
5. Review another word → rate **Easy** — note longer next interval shown

### Out of scope for this ticket

- Progress dashboard / mastery metrics
- Favorites, WOTD
- Full Anki SM-2 (leeches, cram mode, daily limits)

---

## Ticket 3: See my progress

**Phase:** 2 · **Weeks:** 3–4 · **Est. hours:** 16–20  
**Depends on:** Ticket 2

### What to build

A combined progress dashboard: **mastery summary on top**, **activity + streak below**, **Easy / Medium / Tough level tabs** defaulting to the user’s onboarding level. Entry from Home (card tap, toolbar button, or nav — implementer chooses). User can answer: “Am I improving?” from real SRS data.

### :shared

- `ProgressRepository` + use cases: activity (words reviewed last 7 days), streak (reuse `user_settings`), mastery %
- **Maturity definition:** `interval_days >= 21` (constant in `commonMain`)
- Mastery % = mature cards / total cards with review history, per level
- SQLDelight queries or use-case aggregation from `review_card` + `word`
- `commonTest`: maturity %, per-level counts, empty-state handling

### :app

- `ProgressScreen` + `ProgressViewModel` + route `PROGRESS`
- Level tabs: Easy / Medium / Tough; default tab = `user_settings.selected_level`
- 7-day activity indicator (dots or bar)
- Home → Progress entry point in `AppNavHost`

### Acceptance criteria

- [ ] Home navigates to Progress screen
- [ ] Default tab matches user’s selected level from onboarding
- [ ] Mastery % updates after completing reviews (with SM-2 intervals)
- [ ] Activity section shows last-7-days count; streak matches Home
- [ ] `commonTest` covers progress use cases
- [ ] Runs on device/emulator per demo script

### Demo script

1. Complete onboarding at **Medium** level
2. Complete 5–10 reviews across a few days (or manipulate DB for demo)
3. Open Progress from Home
4. Confirm default tab is **Medium**; switch Easy / Tough tabs — counts differ appropriately
5. Confirm mastery and activity sections render; not empty placeholders

### Out of scope for this ticket

- Legacy test-score graphs
- Favorites, WOTD
- Bottom nav shell (simple entry point is fine)

---

## Ticket 4: Save and browse favorites

**Phase:** 2 · **Week:** 5 · **Est. hours:** 8–10  
**Depends on:** Ticket 1

### What to build

User favorites a word during review with one tap. A Favorites screen lists saved words with client-side search. Legacy **FavoritesActivity** link removed from overflow when Compose favorites ships.

### :shared

- `ToggleFavoriteUseCase` — set/clear `is_favorite` on `word`
- `GetFavoritesUseCase` — list favorite words
- Ensure `word.is_favorite` column used (add if missing in schema)

### :app

- Favorite toggle on `ReviewScreen` (icon/button)
- `FavoritesScreen` + `FavoritesViewModel` + route `FAVORITES`
- Home or overflow entry to Favorites
- Remove legacy Favorites overflow link

### Acceptance criteria

- [ ] Toggle favorite on Review — word appears in Favorites list
- [ ] Toggle again — word removed from list
- [ ] Search filters list by word text
- [ ] Legacy FavoritesActivity no longer linked from new app shell
- [ ] Runs on device/emulator per demo script

### Demo script

1. Start Review; tap favorite on two words
2. Navigate to Favorites — both words listed with meaning
3. Search for one word — list filters correctly
4. Unfavorite one from Review — list updates on return
5. Confirm overflow has no legacy Favorites entry

### Out of scope for this ticket

- “Review favorites only” mode (Ticket 6 stretch)
- WOTD

---

## Ticket 5: Word of the day on Home

**Phase:** 2 · **Week:** 6 · **Est. hours:** 8–10  
**Depends on:** Ticket 1

### What to build

Home shows a **word of the day** card fetched from Firebase `/vocabmania/randomize`. Last successful fetch cached locally; offline shows cache or card hides gracefully.

### :shared

- `WordOfTheDayRepository` interface in `commonMain`
- `FirebaseWordOfTheDayFetcher` in `androidMain`
- Cache: dedicated `word_of_the_day` table or columns on `user_settings` with `fetched_at` epoch
- `GetWordOfTheDayUseCase`

### :app

- WOTD card on `HomeScreen` below streak/due section
- Loading / cached / hidden states

### Acceptance criteria

- [ ] Online: Home shows fresh WOTD (word, meaning, usage)
- [ ] Offline after prior fetch: cached WOTD shown
- [ ] Offline cold (no cache): card hidden or friendly empty state — Home does not crash
- [ ] Runs on device/emulator per demo script

### Demo script

1. Launch with network — WOTD card visible with word content
2. Kill app; enable airplane mode; relaunch — cached WOTD still shown
3. Clear app data; airplane mode; launch — no crash; card hidden or “offline” message

### Out of scope for this ticket

- WOTD in Review flow
- Push notification for WOTD

**Cut priority:** First ticket to defer if Phase 2 slips (per PRD).

---

## Ticket 6: Review favorites only + Phase 2 polish

**Phase:** 2 · **Week:** 7 · **Est. hours:** 8–10  
**Depends on:** Tickets 4, 5 (5 optional if deferred)

### What to build

**Stretch:** Entry on Home or Review to run a session with **due words filtered to favorites only**. Buffer week for regressions, copy polish, and any skipped WOTD work.

### :shared

- Extend `GetDueWordsUseCase` (or sibling use case) with `favoritesOnly: Boolean` filter — `is_favorite = 1`

### :app

- “Review favorites” entry point on Home or Review
- Review flow unchanged except card source filter
- Light UX polish across Phase 2 screens if time permits

### Acceptance criteria

- [ ] With favorited due words: “Review favorites” starts session with only those cards
- [ ] With no favorited due words: friendly empty state, no crash
- [ ] Full Phase 1 + Phase 2 demo path works without regression
- [ ] Runs on device/emulator per demo script

### Demo script

1. Favorite 3 words; ensure at least 1 is due (or adjust DB)
2. Tap “Review favorites” — only favorited cards appear
3. Complete session — streak and due counts update correctly
4. Run full regression: onboarding → Home → Review → Progress → Favorites

### Out of scope for this ticket

- Phase 3 legacy deletion
- Notifications, Play Store release

---

## Dependency graph & parallelization

Vertical slices guarantee **end-to-end demoability**. Parallelization only applies when tickets are **independent** — plan in waves.

```
Wave 0 (blocking)     Ticket 1 — Real vocabulary library
                          │
          ┌───────────────┼───────────────┐
          ▼               ▼               ▼
Wave 1              Ticket 2        Ticket 4*       Ticket 5*
                    SM-2 lite       Favorites       WOTD
                    (serial         (parallel       (parallel
                     preferred)     after #1)       after #1)
                          │
                          ▼
Wave 2              Ticket 3 — Progress dashboard
                    (needs SM-2 + review history for meaningful mastery)
                          │
                          ▼
Wave 3              Ticket 6 — Review favorites only + polish
                    (needs #4; #5 optional)
```

\* Tickets 4 and 5 can run **in parallel with Ticket 2** after Ticket 1 lands, if `HomeScreen` edits are coordinated (merge conflict risk). Solo dev: run **1 → 2 → 3 → 4 → 5 → 6** sequentially.

| Parallel pair (2 devs) | Notes |
|------------------------|-------|
| **#2 + #4** | Different modules; watch shared `ReviewScreen` if favorites toggle added during SM-2 work |
| **#2 + #5** | Mostly isolated; both touch `HomeScreen` — assign one owner per file or sequence |
| **#3 + #4** | Only after #2 done; progress vs favorites are separate screens |
| **#4 + #5** | Best parallel pair after #1 — low coupling |

**Foundational work** (Ticket 1) is still a vertical slice (import + Home status + review with real words), but it **gates** the backlog by design — not a failure of slicing.

---

## After this doc

Start implementation:

> Read `docs/VISION.md`, `docs/prds/phase-2-deepen.md`, and **Ticket 1**. Implement end-to-end.

One ticket per week (Ticket 3 may span two calendar weeks). Reviewer hat before commit: match acceptance criteria + demo script.
