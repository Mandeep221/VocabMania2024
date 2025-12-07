# Fastlane Setup for VocabMania

This directory contains Fastlane configuration for automating builds, tests, and deployments.

## 📦 Installation

### Prerequisites
- Ruby 2.6 or newer
- Bundler

### Install Fastlane

```bash
# Using Homebrew (macOS)
brew install fastlane

# Or using RubyGems
sudo gem install fastlane

# Or using Bundler (recommended for project)
bundle install
```

## 🚀 Quick Start

### 1. Setup Environment Variables

```bash
# Copy example environment file
cp fastlane/.env.example fastlane/.env.default

# Edit with your actual values
nano fastlane/.env.default
```

### 2. Run Your First Lane

```bash
# Build debug APK
fastlane debug

# Run tests
fastlane test

# Run lint
fastlane lint
```

## 📋 Available Lanes

### Level 1: Basic Lanes

```bash
# Build debug APK
fastlane debug

# Build release APK (unsigned)
fastlane release
```

### Level 2: Testing & Quality

```bash
# Run unit tests
fastlane test

# Run lint checks
fastlane lint

# Run both tests and lint
fastlane check
```

### Level 3: CI Lane

```bash
# Complete CI pipeline (lint + test + build)
fastlane ci
```

### Level 4: Signed Builds

```bash
# Build signed release APK
fastlane release_signed

# Build signed AAB for Play Store
fastlane bundle_signed
```

**Required environment variables:**
- `KEYSTORE_PATH`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

### Level 5: Firebase Distribution

```bash
# Build and distribute to Firebase
fastlane firebase_beta
```

**Required environment variables:**
- `FIREBASE_APP_ID`
- `FIREBASE_GROUPS` (optional, defaults to "testers")

### Level 6: Play Store Deployment

```bash
# Deploy to internal track
fastlane deploy_internal

# Promote to beta
fastlane promote_to_beta

# Promote to production (10% rollout)
fastlane promote_to_production
```

**Required environment variables:**
- `GOOGLE_PLAY_JSON_KEY` (service account JSON)

### Level 7: Complete Pipeline

```bash
# Complete release pipeline
fastlane release_pipeline env:staging track:internal

# Production release
fastlane release_pipeline env:production track:production
```

## 🔐 Setting Up Secrets

### 1. Keystore

```bash
# Set in .env.default
KEYSTORE_PATH=../mykey.jks
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=your_alias
KEY_PASSWORD=your_key_password
```

### 2. Firebase App Distribution

1. Get Firebase App ID from Firebase Console
2. Install Firebase CLI: `npm install -g firebase-tools`
3. Login: `firebase login:ci` (get token)
4. Add to `.env.default`:

```bash
FIREBASE_APP_ID=1:123456789:android:abc123
FIREBASE_TOKEN=your_token
```

### 3. Google Play Store

1. Create service account in Google Cloud Console
2. Download JSON key file
3. Enable Google Play Developer API
4. Grant access in Play Console
5. Add to `.env.default`:

```bash
GOOGLE_PLAY_JSON_KEY=$(cat service-account.json)
```

## 🔄 Using with CI/CD

### GitHub Actions

```yaml
- name: Run Fastlane
  run: fastlane ci
  env:
    KEYSTORE_PATH: ${{ secrets.KEYSTORE_PATH }}
    KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
    KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

### GitLab CI

```yaml
build:
  script:
    - fastlane ci
  variables:
    KEYSTORE_PASSWORD: $KEYSTORE_PASSWORD
```

## 🐛 Troubleshooting

### "Bundle not found"

```bash
bundle install
```

### "Gradle command not found"

```bash
chmod +x ../gradlew
```

### "Missing environment variables"

Check your `.env.default` file has all required variables.

## 📚 Learn More

- [Fastlane Documentation](https://docs.fastlane.tools/)
- [Android Setup Guide](https://docs.fastlane.tools/getting-started/android/setup/)
- [Available Actions](https://docs.fastlane.tools/actions/)

## 🎯 Best Practices

1. **Never commit `.env` files** with real credentials
2. **Use different keystores** for debug and release
3. **Test lanes locally** before using in CI
4. **Use Bundler** for consistent Fastlane versions
5. **Keep Fastfile organized** with clear lane descriptions

