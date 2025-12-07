# CI/CD Learning Guide for Android Development

## 📚 Complete Guide from Scratch to Advanced

This guide will help you master CI/CD for Android development, from basic concepts to production-ready pipelines.

---

## Table of Contents

1. [CI/CD Fundamentals](#1-cicd-fundamentals)
2. [Tools Overview](#2-tools-overview)
3. [GitHub Actions (Start Here)](#3-github-actions)
4. [Fastlane (Industry Standard)](#4-fastlane)
5. [Advanced Topics](#5-advanced-topics)
6. [Best Practices](#6-best-practices)
7. [Troubleshooting](#7-troubleshooting)

---

## 1. CI/CD Fundamentals

### What is CI/CD?

**Continuous Integration (CI)**
- Automatically build and test code when changes are pushed
- Catch bugs early in development
- Ensure code quality standards are met

**Continuous Deployment/Delivery (CD)**
- Automatically deploy apps to testers or stores
- Streamline release process
- Reduce manual errors

### Why CI/CD for Android?

```
Developer pushes code
    ↓
CI Server detects change
    ↓
Build APK/AAB
    ↓
Run tests (Unit, UI, Lint)
    ↓
Code quality checks
    ↓
Generate signed builds
    ↓
Deploy to Play Store / Firebase
    ↓
Notify team
```

### Key Concepts

1. **Pipeline**: Series of automated steps
2. **Job**: Individual task (build, test, deploy)
3. **Artifact**: Output files (APK, AAB, reports)
4. **Environment Variables**: Configuration values
5. **Secrets**: Sensitive data (API keys, passwords)
6. **Trigger**: Event that starts pipeline (push, PR, schedule)

---

## 2. Tools Overview

### For Android Development (2024)

| Tool | Best For | Learning Curve | Cost | Recommendation |
|------|----------|----------------|------|----------------|
| **GitHub Actions** | Open source, GitHub repos | Easy | Free for public | ⭐ Start here |
| **Fastlane** | Mobile automation tasks | Medium | Free | ⭐ Must learn |
| **GitLab CI** | Self-hosted, GitLab repos | Medium | Free tier | Good alternative |
| **Bitrise** | Mobile-specific, fast setup | Easy | Paid (free tier) | Great for mobile |
| **CircleCI** | Docker-based, flexible | Medium | Free tier | Solid choice |
| **Jenkins** | Self-hosted, customizable | Hard | Free (hosting cost) | Legacy/Enterprise |
| **AppCenter** | Distribution + Analytics | Easy | Free tier | Complementary |

### My Recommendation for Learning

**Phase 1: Foundation (Week 1-2)**
- ✅ GitHub Actions (easiest to start, most popular)
- ✅ Fastlane (industry standard for mobile)

**Phase 2: Intermediate (Week 3-4)**
- GitLab CI (if using GitLab)
- OR Bitrise (mobile-optimized)

**Phase 3: Advanced (Optional)**
- Jenkins (for enterprise/self-hosted)
- CircleCI (for advanced workflows)

---

## 3. GitHub Actions

### Why Start with GitHub Actions?

- ✅ Free for public repos
- ✅ Integrated with GitHub
- ✅ Large marketplace of actions
- ✅ Easy YAML syntax
- ✅ Great documentation

### Core Concepts

```yaml
# .github/workflows/android.yml

name: Android CI                    # Workflow name
on: [push, pull_request]           # Triggers
jobs:                              # Jobs to run
  build:                           # Job name
    runs-on: ubuntu-latest         # Runner OS
    steps:                         # Steps in job
      - uses: actions/checkout@v3  # Use existing action
      - name: Build APK            # Step name
        run: ./gradlew build       # Command to run
```

### Key Components

1. **Workflows**: `.github/workflows/*.yml`
2. **Events**: `push`, `pull_request`, `schedule`, `workflow_dispatch`
3. **Jobs**: Run in parallel or sequentially
4. **Steps**: Individual commands or actions
5. **Actions**: Reusable units (from marketplace or custom)

### Learning Path

#### Level 1: Basic Build
```yaml
name: Basic Build
on: push
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          distribution: 'zulu'
          java-version: '17'
      
      - name: Build with Gradle
        run: ./gradlew assembleDebug
```

#### Level 2: Build + Test
```yaml
- name: Run tests
  run: ./gradlew test

- name: Run lint
  run: ./gradlew lint

- name: Upload test results
  uses: actions/upload-artifact@v3
  with:
    name: test-results
    path: app/build/test-results/
```

#### Level 3: Build + Sign + Deploy
```yaml
- name: Build signed APK
  run: ./gradlew assembleRelease
  env:
    SIGNING_KEY: ${{ secrets.SIGNING_KEY }}
    
- name: Upload to Play Store
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.SERVICE_ACCOUNT_JSON }}
    packageName: com.example.app
    releaseFiles: app/build/outputs/apk/release/*.apk
```

### Caching (Speed Up Builds)

```yaml
- name: Cache Gradle
  uses: actions/cache@v3
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
    restore-keys: |
      ${{ runner.os }}-gradle-
```

---

## 4. Fastlane

### Why Fastlane?

- ✅ Industry standard for mobile CI/CD
- ✅ Automates screenshots, signing, deployment
- ✅ Works with all CI platforms
- ✅ Ruby-based, highly customizable

### Installation

```bash
# Install Fastlane
brew install fastlane  # macOS
gem install fastlane   # Or via Ruby gems

# Initialize in project
cd your-android-project
fastlane init
```

### Core Concepts

1. **Lanes**: Automated workflows (like recipes)
2. **Actions**: Individual tasks
3. **Plugins**: Extended functionality
4. **Fastfile**: Main configuration file

### Project Structure

```
fastlane/
├── Fastfile          # Main configuration
├── Appfile           # App identifiers
├── Pluginfile        # Plugins
├── .env.default      # Default environment variables
├── .env.production   # Production secrets
└── README.md         # Generated docs
```

### Learning Path

#### Level 1: Basic Lane

```ruby
# fastlane/Fastfile

default_platform(:android)

platform :android do
  desc "Build debug APK"
  lane :debug do
    gradle(task: "assembleDebug")
  end
  
  desc "Build release APK"
  lane :release do
    gradle(task: "assembleRelease")
  end
end
```

**Usage:**
```bash
fastlane debug
fastlane release
```

#### Level 2: Testing + Linting

```ruby
lane :test do
  gradle(task: "test")
  gradle(task: "lint")
end

lane :ci do
  test
  debug
end
```

#### Level 3: Signing + Deploy

```ruby
lane :deploy_internal do
  gradle(
    task: "bundle",
    build_type: "Release",
    properties: {
      "android.injected.signing.store.file" => ENV["KEYSTORE_PATH"],
      "android.injected.signing.store.password" => ENV["KEYSTORE_PASSWORD"],
      "android.injected.signing.key.alias" => ENV["KEY_ALIAS"],
      "android.injected.signing.key.password" => ENV["KEY_PASSWORD"]
    }
  )
  
  upload_to_play_store(
    track: 'internal',
    aab: 'app/build/outputs/bundle/release/app-release.aab'
  )
end
```

#### Level 4: Complete CI/CD Pipeline

```ruby
lane :beta do
  # Increment version
  increment_version_code
  
  # Build
  gradle(task: "clean bundleRelease")
  
  # Upload to Firebase
  firebase_app_distribution(
    app: ENV["FIREBASE_APP_ID"],
    groups: "beta-testers",
    release_notes: "New beta build"
  )
  
  # Notify Slack
  slack(
    message: "New beta build available!",
    channel: "#mobile-releases"
  )
end
```

---

## 5. Advanced Topics

### 5.1 Matrix Builds (Multiple Configurations)

```yaml
# GitHub Actions
strategy:
  matrix:
    api-level: [26, 29, 33]
    arch: [x86, x86_64]
steps:
  - name: Run tests on Android ${{ matrix.api-level }}
    uses: reactivecircus/android-emulator-runner@v2
    with:
      api-level: ${{ matrix.api-level }}
      arch: ${{ matrix.arch }}
      script: ./gradlew connectedCheck
```

### 5.2 Automated Screenshot Testing

```ruby
# Fastlane
lane :screenshots do
  gradle(task: "assembleDebug assembleAndroidTest")
  
  screengrab(
    locales: ["en-US", "es-ES", "fr-FR"],
    clear_previous_screenshots: true
  )
end
```

### 5.3 Version Management

```ruby
lane :bump_version do
  increment_version_code(
    gradle_file_path: "app/build.gradle.kts"
  )
  
  version = get_version_code(
    gradle_file_path: "app/build.gradle.kts"
  )
  
  git_commit(
    path: "app/build.gradle.kts",
    message: "Bump version to #{version}"
  )
end
```

### 5.4 Multi-Environment Deployment

```ruby
lane :deploy do |options|
  environment = options[:env] || "staging"
  
  gradle(
    task: "assemble",
    build_type: "Release",
    flavor: environment
  )
  
  case environment
  when "staging"
    firebase_app_distribution(groups: "internal-testers")
  when "production"
    upload_to_play_store(track: "production")
  end
end
```

**Usage:**
```bash
fastlane deploy env:staging
fastlane deploy env:production
```

### 5.5 Code Quality Gates

```yaml
# GitHub Actions
- name: Run detekt
  run: ./gradlew detekt

- name: Run ktlint
  run: ./gradlew ktlintCheck

- name: SonarQube Scan
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: ./gradlew sonarqube
```

---

## 6. Best Practices

### 6.1 Security

✅ **DO:**
- Store secrets in CI/CD secrets manager
- Use environment variables for sensitive data
- Rotate credentials regularly
- Encrypt keystore files
- Use separate keystores for debug/release

❌ **DON'T:**
- Commit secrets to Git
- Hardcode passwords
- Share production keys in plain text
- Use same keystore for all environments

### 6.2 Speed Optimization

```yaml
# Cache dependencies
- uses: actions/cache@v3
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
      ~/.android/build-cache
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}

# Use Gradle daemon
- name: Build with Gradle
  run: ./gradlew build --daemon --parallel --build-cache
```

### 6.3 Testing Strategy

```
1. Unit Tests          → Every commit
2. Lint                → Every commit
3. Integration Tests   → Every PR
4. UI Tests            → Nightly / Before release
5. Screenshot Tests    → Before release
```

### 6.4 Deployment Strategy

```
Feature Branch → CI Build + Test
       ↓
Pull Request → CI + Code Review
       ↓
Main Branch → Deploy to Internal Track
       ↓
Tagged Release → Deploy to Beta
       ↓
Manual Promotion → Deploy to Production
```

---

## 7. Troubleshooting

### Common Issues

#### Build Fails: "SDK not found"

```yaml
# Solution: Install Android SDK
- name: Set up Android SDK
  uses: android-actions/setup-android@v2
```

#### Build Fails: "Out of Memory"

```yaml
# Solution: Increase Gradle memory
- name: Build
  run: ./gradlew build
  env:
    GRADLE_OPTS: -Xmx4096m -XX:MaxPermSize=512m
```

#### Tests Timeout

```yaml
# Solution: Increase timeout
- name: Run tests
  run: ./gradlew test
  timeout-minutes: 30
```

#### Signing Fails

```bash
# Check keystore
keytool -list -v -keystore mykey.jks

# Verify environment variables
echo $SIGNING_KEY | base64 -d > keystore.jks
```

---

## 8. Practice Projects

### Beginner

1. Set up basic GitHub Actions workflow
2. Add Gradle caching
3. Run unit tests on CI
4. Generate APK artifact

### Intermediate

5. Set up Fastlane
6. Automate signing
7. Deploy to Firebase App Distribution
8. Add screenshot testing

### Advanced

9. Multi-environment deployment
10. Automated Play Store releases
11. Slack/Discord notifications
12. Version bumping automation

---

## 9. Resources

### Documentation
- [GitHub Actions for Android](https://docs.github.com/en/actions)
- [Fastlane Documentation](https://docs.fastlane.tools/)
- [Gradle Plugin User Guide](https://developer.android.com/studio/build)

### Example Projects
- [GitHub Actions Examples](https://github.com/android/compose-samples)
- [Fastlane Examples](https://github.com/fastlane/examples)

### Video Tutorials
- [GitHub Actions for Android](https://www.youtube.com/results?search_query=github+actions+android)
- [Fastlane Mobile DevOps](https://www.youtube.com/results?search_query=fastlane+android)

---

## Next Steps

1. ✅ Review `.github/workflows/` directory for example workflows
2. ✅ Initialize Fastlane in your project
3. ✅ Set up secrets in GitHub repository settings
4. ✅ Test each workflow individually
5. ✅ Gradually add more automation

**Start with the simplest workflow and build up complexity gradually!**

