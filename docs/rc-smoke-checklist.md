# Phase 3 RC smoke checklist

**Build:** signed `assembleRelease` / `bundleRelease` via `key.properties` (see `docs/release-signing.md`)  
**Artifacts:** `app/build/outputs/apk/release/app-release.apk`, `…/bundle/release/app-release.aab`  
**App ID:** `com.msarangal.vocabmania` · **versionName:** `12.0.1`

## Build & install

- [x] `key.properties` present locally (gitignored); release signingConfig applied
- [x] `./gradlew :app:assembleRelease :app:bundleRelease` succeeds
- [x] `apksigner verify` passes (v2+) on release APK
- [x] Sideload install succeeds (`adb install -r …/app-release.apk`)
- [x] Launch `AppShellActivity` after `pm clear` — no startup crash
- [ ] Repeat install on a **physical** device (emulator validated 2026-07-15)

## Habit path smoke

Run with cleared data (`adb shell pm clear com.msarangal.vocabmania`).

| Step | Expect | Status (emulator 2026-07-15) |
|------|--------|------------------------------|
| Cold start | Onboarding welcome (“VocabMania”, Get started) | pass |
| Level + goal | Level step → Daily goal → Start learning | pass |
| Home | Streak, due count, WOTD, reminder row, Start review | pass |
| Reminder | Toggle visible; opt-in only (permission on enable, API 33+) | present (manual toggle OK) |
| Progress | Opens; empty mastery art when no reviews at level | pass |
| Favorites | Opens from overflow; empty art when none saved | manual OK |
| Review | Word card, tap reveal, rating buttons | pass (reveal + Good) |
| Session complete | After finishing a full session — summary + streak | manual (session not drained in automate) |
| Overflow | Share / Rate present; no Legacy / TestActivity | Ticket 1 (spot-check) |
| System dark | Flip device theme — screens stay branded | Ticket 3 (spot-check) |

## Commands cheat sheet

```bash
./gradlew :app:assembleRelease
adb uninstall com.msarangal.vocabmania
adb install -r app/build/outputs/apk/release/app-release.apk
adb shell pm clear com.msarangal.vocabmania
adb shell am start -n com.msarangal.vocabmania/.presentation.activity.AppShellActivity
```

## Out of scope for this RC

- Play Console upload / internal testing track
- Deleting legacy Activities from the APK
- New product features found during smoke
