# 🔐 Secrets & Configuration Setup Guide

## ⚠️ CRITICAL: Files Already Tracked by Git

Your repository currently has sensitive files that should NOT be in version control:

### Files to Remove from Git History

1. **`mykey.jks`** - Your signing keystore (CRITICAL)
2. **`google-services.json`** - Firebase configuration
3. **`local.properties`** - Local SDK paths

### How to Remove Sensitive Files from Git

```bash
# IMPORTANT: Backup these files first!
cp mykey.jks ~/Desktop/mykey-backup.jks
cp app/google-services.json ~/Desktop/google-services-backup.json

# Remove from Git (keeps local copy)
git rm --cached mykey.jks
git rm --cached app/google-services.json
git rm --cached local.properties

# Commit the removal
git commit -m "Remove sensitive files from version control"

# Push changes
git push origin main
```

**Note:** If these files have been public, you should:
1. **Rotate your keystore** (create a new one)
2. **Regenerate Firebase config**
3. Consider the old keystore compromised

---

## 🔑 GitHub Secrets Setup

For CI/CD to work, you need to add secrets to your GitHub repository.

### Step 1: Navigate to Secrets

1. Go to your GitHub repository
2. Click **Settings**
3. Click **Secrets and variables** → **Actions**
4. Click **New repository secret**

### Step 2: Add Required Secrets

#### For Release Builds (Workflow 03)

| Secret Name | How to Get | Command |
|-------------|------------|---------|
| `KEYSTORE_BASE64` | Base64 encode your keystore | `base64 -i mykey.jks \| pbcopy` (macOS)<br>`base64 mykey.jks \| clip` (Windows) |
| `KEYSTORE_PASSWORD` | Your keystore password | From your records |
| `KEY_ALIAS` | Your key alias | `keytool -list -v -keystore mykey.jks` |
| `KEY_PASSWORD` | Your key password | From your records |

**Verify your keystore details:**
```bash
keytool -list -v -keystore mykey.jks
```

#### For Firebase Distribution (Workflow 04)

| Secret Name | How to Get |
|-------------|------------|
| `FIREBASE_APP_ID` | Firebase Console → Project Settings → General → Your apps |
| `FIREBASE_SERVICE_ACCOUNT` | Firebase Console → Project Settings → Service accounts → Generate new private key |

**Get Firebase App ID:**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Click gear icon → Project settings
4. Scroll to "Your apps" section
5. Copy the App ID (format: `1:123456789:android:abc123`)

**Create Firebase Service Account:**
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login
firebase login

# Get your project ID
firebase projects:list

# Generate token (for CI/CD)
firebase login:ci
```

Then in GitHub Secrets, add the token as base64:
```bash
echo "YOUR_FIREBASE_TOKEN" | base64 | pbcopy
```

#### For Play Store Deployment (Advanced)

| Secret Name | How to Get |
|-------------|------------|
| `GOOGLE_PLAY_JSON_KEY` | Create service account in Google Cloud Console |

**Create Google Play Service Account:**

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project (or select existing)
3. Enable **Google Play Developer API**
4. Create service account:
   - IAM & Admin → Service Accounts → Create Service Account
   - Name it `github-actions-play-store`
   - Grant role: **Service Account User**
   - Create key (JSON format)
5. Download the JSON file
6. Go to [Google Play Console](https://play.google.com/console)
7. Setup → API access
8. Link the service account
9. Grant permissions: **Release to production, internal testing**

**Add to GitHub Secrets:**
```bash
# Copy the entire JSON content
cat service-account.json | pbcopy

# Paste into GitHub Secrets as GOOGLE_PLAY_JSON_KEY
```

---

## 🔧 Local Development Setup

### Option 1: Using Fastlane (Recommended)

```bash
# Copy environment template
cp fastlane/env.example.txt fastlane/.env.default

# Edit with your values
nano fastlane/.env.default
```

Add:
```bash
KEYSTORE_PATH=../mykey.jks
KEYSTORE_PASSWORD=your_actual_password
KEY_ALIAS=your_alias
KEY_PASSWORD=your_key_password
```

### Option 2: Using key.properties (Android Studio)

Create `key.properties` in project root:

```properties
storePassword=your_keystore_password
keyPassword=your_key_password
keyAlias=your_key_alias
storeFile=mykey.jks
```

Update `app/build.gradle.kts`:

```kotlin
// Load keystore properties
val keystorePropertiesFile = rootProject.file("key.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... other config
        }
    }
}
```

---

## 📱 Firebase Setup

### Initial Setup

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login
firebase login

# Initialize Firebase in your project
firebase init
```

### Get Firebase Configuration

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project (or create new)
3. Add Android app (if not already added)
4. Download `google-services.json`
5. Place in `app/google-services.json`
6. **DO NOT commit this file!** (It's now in .gitignore)

### For Multiple Environments

Create separate Firebase projects:
- `vocabmania-dev` (Development)
- `vocabmania-staging` (Staging)
- `vocabmania-prod` (Production)

Then create build flavors:

```kotlin
// app/build.gradle.kts
android {
    flavorDimensions += "environment"
    
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            // Use app/src/dev/google-services.json
        }
        
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            // Use app/src/staging/google-services.json
        }
        
        create("production") {
            dimension = "environment"
            // Use app/src/production/google-services.json
        }
    }
}
```

---

## 🧪 Testing Your Setup

### Test 1: Local Build

```bash
# Clean build
./gradlew clean

# Build debug (should work without any secrets)
./gradlew assembleDebug

# Build release (needs signing configured)
./gradlew assembleRelease
```

### Test 2: Fastlane

```bash
# Test basic lane
fastlane debug

# Test with signing
fastlane release_signed
```

### Test 3: GitHub Actions

Push code and check Actions tab in GitHub:

```bash
git add .
git commit -m "Test CI/CD setup"
git push origin main
```

---

## 🔒 Security Best Practices

### DO ✅

1. **Use different keystores** for debug and release
2. **Store secrets in CI/CD secrets manager**
3. **Use environment variables** for configuration
4. **Rotate credentials** regularly
5. **Use separate Firebase projects** for dev/staging/prod
6. **Enable 2FA** on all accounts
7. **Audit access** to Google Play Console

### DON'T ❌

1. **Never commit keystores** to Git
2. **Never hardcode passwords** in code
3. **Never share keystores** via email/Slack
4. **Never use production keys** in debug builds
5. **Never commit `google-services.json`**
6. **Never log sensitive information**

---

## 🆘 Emergency: If Secrets Are Leaked

If you accidentally committed secrets to Git:

### 1. Rotate Immediately

```bash
# Create new keystore
keytool -genkey -v -keystore new-keystore.jks \
  -alias new-alias -keyalg RSA -keysize 2048 -validity 10000

# Regenerate Firebase config
# (In Firebase Console → Delete app → Re-add app)

# Regenerate Google Play service account
# (In Google Cloud Console → Delete old account → Create new)
```

### 2. Remove from Git History

```bash
# Using BFG Repo Cleaner (recommended)
brew install bfg
bfg --delete-files mykey.jks
git reflog expire --expire=now --all
git gc --prune=now --aggressive

# Force push (be careful!)
git push origin --force --all
```

### 3. Notify Team

Inform your team about the incident and new credentials.

---

## 📋 Checklist

Before running CI/CD:

- [ ] Removed sensitive files from Git (`mykey.jks`, `google-services.json`)
- [ ] Added all files to `.gitignore`
- [ ] Created GitHub Secrets
- [ ] Tested build locally
- [ ] Tested Fastlane lanes
- [ ] Verified Firebase configuration
- [ ] Set up Google Play service account (if deploying)
- [ ] Backed up keystore securely

---

## 📚 Additional Resources

- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Android App Signing](https://developer.android.com/studio/publish/app-signing)
- [Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
- [Google Play Publishing API](https://developers.google.com/android-publisher)

