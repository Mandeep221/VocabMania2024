# Play Store listing draft — VocabMania

**Status:** draft (Phase 3 Ticket 5) — not uploaded  
**Package:** `com.msarangal.vocabmania`  
**Version referenced:** `12.0.1` (versionCode `12`)

## Title

VocabMania

*(Store limit 30 characters — fits.)*

## Short description

Daily vocab habit with smart spaced repetition. Review what’s due, keep your streak.

*(Store limit 80 characters.)*

## Full description

Build a vocabulary habit you can keep.

VocabMania shows you what’s due today, walks you through a short review session, and brings words back right when you’re about to forget them — powered by spaced repetition under the hood.

**What you get**
• A focused daily review (Again / Hard / Good / Easy)
• Streaks that reward consistency, not cramming
• Progress by level — see mastery and recent activity
• Favorites you can save and review on their own
• Word of the day on Home
• Optional local reminder at 7 PM when words are due or your streak is at risk

**How it works**
1. Pick a starting level and daily goal
2. Open Home to see due words and your streak
3. Finish a short session — then get back to your day

No account required for the core habit loop. Reviews stay on your device’s local library (with cloud catalog when available).

Learn words that stick. Five minutes a day.

## Notes for later upload (not required for Phase 3 RC)

| Field | Draft direction |
|-------|-----------------|
| Category | Education |
| Content rating | Everyone / tool to complete IARC questionnaire |
| Graphic assets | Feature graphic + phone screenshots of Home, Review, Progress, Session complete (warm-scholar light + one dark) |
| Contact email | Owner’s Play Console email |
| Privacy policy URL | Required before production publish — add when ready |
| Tags / keywords (internal) | vocabulary, spaced repetition, daily habit, GRE words, word of the day |

## Store text fidelity vs product (RC)

Matches shipped Compose flow as of Phase 3 Tickets 1–4:

- Onboarding → Home (streak, due, WOTD, reminder) → Review → Session complete → Progress → Favorites
- Share / Rate in Home overflow; no legacy test hub entry from Compose
- Reminder is opt-in, local, 7 PM; not FCM

Do **not** claim: accounts/sync across devices, custom reminder times, Help/FAQ screen, or iOS.
