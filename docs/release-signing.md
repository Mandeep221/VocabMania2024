# Release signing (Phase 3 RC)

Signed release builds need a `key.properties` file at the **repo root** (gitignored). Gradle wires this into `app/build.gradle.kts` → `signingConfigs.release`.

## Local sideload RC

1. Copy `docs/key.properties.example` → `key.properties`.
2. Point `storeFile` at a keystore in the repo root (path relative to root), e.g. `mykey.jks` (Play upload key) or a local RC-only keystore.
3. Build:

```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

Artifacts:

- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

4. Verify:

```bash
apksigner verify -v --print-certs app/build/outputs/apk/release/app-release.apk
```

5. Install:

```bash
adb uninstall com.msarangal.vocabmania   # if signature differs from an existing install
adb install -r app/build/outputs/apk/release/app-release.apk
```

### Local RC-only keystore

If Play upload credentials are not available on this machine, you may generate a **sideload-only** keystore (never upload that APK to Play):

```bash
keytool -genkeypair -v \
  -keystore rc-local.jks \
  -alias vocabmania-rc \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=VocabMania RC Local, OU=Dev, O=VocabMania, L=Local, ST=NA, C=US"
```

Then set `storeFile=rc-local.jks` in `key.properties`. Both `*.jks` and `key.properties` are gitignored.

Play Store releases must use the existing upload key (`mykey.jks` / CI secrets) — see `SECRETS_SETUP.md`.

## CI (GitHub Actions)

Workflow: `.github/workflows/03-release-build.yml`

Secrets: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.  
CI writes `keystore.jks` + `key.properties` (`storeFile=keystore.jks`) before `assembleRelease` / `bundleRelease`.

Full secret setup: `SECRETS_SETUP.md`, `README_CICD.md`.
