# 🚀 CI/CD Quick Start Guide

A 30-minute guide to get your CI/CD up and running.

---

## 📦 What You'll Learn

By the end of this guide, you'll have:
- ✅ Automated builds on every push
- ✅ Automated tests running
- ✅ APK artifacts you can download
- ✅ Basic understanding of GitHub Actions

---

## Step 1: Understand What's Already Set Up (5 min)

### Files Created

```
VocabMania2024/
├── .github/
│   └── workflows/
│       ├── 01-basic-build.yml          ← Start here!
│       ├── 02-build-and-test.yml       ← Add testing
│       ├── 03-release-build.yml        ← Signed releases
│       ├── 04-firebase-distribution.yml ← Beta distribution
│       └── 05-complete-pipeline.yml    ← Production ready
├── fastlane/
│   ├── Fastfile                        ← Automation lanes
│   ├── Appfile                         ← App configuration
│   ├── README.md                       ← Fastlane docs
│   └── env.example.txt                 ← Environment template
├── .gitignore                          ← Updated with CI/CD files
├── CICD_LEARNING_GUIDE.md             ← Complete learning path
├── SECRETS_SETUP.md                    ← Security setup
└── CICD_QUICK_START.md                ← This file!
```

---

## Step 2: First GitHub Actions Build (10 min)

### 2.1 Push Your Code

```bash
# Make sure you're on the main branch
git checkout main

# Stage all files
git add .

# Commit
git commit -m "Add CI/CD configuration"

# Push to GitHub
git push origin main
```

### 2.2 Watch the Build

1. Go to your GitHub repository
2. Click the **Actions** tab
3. You should see "01 - Basic Build" running
4. Click on it to see the progress

**Expected result:** ✅ Green checkmark after ~2-5 minutes

### 2.3 Download the APK

1. Click on the completed workflow run
2. Scroll down to **Artifacts**
3. Download `debug-apk`
4. Unzip and install on your device!

**🎉 Congratulations! You just ran your first CI/CD pipeline!**

---

## Step 3: Understanding the Workflow (5 min)

Open `.github/workflows/01-basic-build.yml` and read through it.

### Key Parts Explained

```yaml
name: 01 - Basic Build          # Workflow name (shows in GitHub UI)

on:                             # When to run
  push:
    branches: [ main, develop ] # Run on push to these branches

jobs:                           # Jobs to execute
  build:                        # Job name
    runs-on: ubuntu-latest      # Use Ubuntu VM
    
    steps:                      # Steps in the job
      - name: Checkout code     # Get your code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17     # Install Java
        uses: actions/setup-java@v4
        with:
          distribution: 'zulu'
          java-version: '17'
      
      - name: Build debug APK   # Build your app
        run: ./gradlew assembleDebug
```

---

## Step 4: Add Testing (5 min)

### 4.1 Enable the Test Workflow

The `02-build-and-test.yml` workflow is already created. It will run automatically when you push code.

### 4.2 Test Locally First

```bash
# Run tests locally
./gradlew test

# Run lint
./gradlew lint
```

### 4.3 Push and Watch

```bash
git add .
git commit -m "Enable test workflow"
git push origin main
```

Go to Actions tab and watch both workflows run:
- ✅ Basic Build
- ✅ Build and Test

---

## Step 5: Install Fastlane (5 min)

### macOS

```bash
# Install Fastlane
brew install fastlane

# Verify installation
fastlane --version
```

### Windows/Linux

```bash
# Install Ruby first, then:
gem install fastlane

# Verify
fastlane --version
```

### Test Fastlane

```bash
# Navigate to your project
cd /path/to/VocabMania2024

# Run a simple lane
fastlane debug
```

**Expected result:** APK built successfully!

---

## Step 6: Understanding Fastlane (5 min)

Open `fastlane/Fastfile` and find these lanes:

```ruby
lane :debug do
  gradle(task: "assembleDebug")
end
```

Try running:

```bash
# Build debug APK
fastlane debug

# Run tests
fastlane test

# Run lint
fastlane lint

# Run everything (CI lane)
fastlane ci
```

---

## 🎯 What You've Accomplished

After 30 minutes, you now have:

- ✅ **Automated builds** on every push to GitHub
- ✅ **Automated tests** catching bugs early
- ✅ **Downloadable APKs** from GitHub Actions
- ✅ **Fastlane** for local automation
- ✅ **Understanding** of basic CI/CD concepts

---

## 🚶 Next Steps (Choose Your Path)

### Path A: Learn by Experimenting (Recommended for Learning)

1. **Week 1: GitHub Actions**
   - Modify `01-basic-build.yml` to add your own step
   - Try triggering on pull requests
   - Add a badge to your README
   
2. **Week 2: Testing**
   - Write more unit tests
   - See them run in CI
   - Make a test fail and see what happens
   
3. **Week 3: Fastlane**
   - Create a custom lane
   - Automate version bumping
   - Add Firebase distribution
   
4. **Week 4: Advanced**
   - Set up signed releases
   - Deploy to Play Store
   - Add notifications

### Path B: Production Ready (If You Need It Now)

1. **Secure Your Secrets** (CRITICAL)
   - Remove `mykey.jks` from Git (see `SECRETS_SETUP.md`)
   - Add GitHub Secrets
   - Test release build locally

2. **Set Up Firebase** (for beta testing)
   - Create Firebase project
   - Add app to Firebase
   - Configure workflow 04

3. **Set Up Play Store** (for production)
   - Create service account
   - Configure workflow 03
   - Test internal track deployment

---

## 📚 Learning Resources

### Beginner
- [ ] Read `CICD_LEARNING_GUIDE.md` (comprehensive guide)
- [ ] Watch: [GitHub Actions for Android](https://www.youtube.com/watch?v=7YZq45C4PMg)
- [ ] Try: Modify workflow files and experiment

### Intermediate
- [ ] Set up branch protection rules
- [ ] Add code coverage reports
- [ ] Set up matrix builds (multiple Android versions)

### Advanced
- [ ] Custom GitHub Actions
- [ ] Self-hosted runners
- [ ] Multi-module architecture CI

---

## 🆘 Troubleshooting

### Build Fails: "Permission Denied: gradlew"

```bash
chmod +x gradlew
git add gradlew
git commit -m "Fix gradlew permissions"
git push
```

### Build Fails: "SDK not found"

This is normal - the workflow handles SDK installation automatically.
Check the workflow file has:

```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v4
```

### Fastlane: "Command not found"

```bash
# Verify installation
which fastlane

# If not found, reinstall
brew install fastlane
```

### Workflow Not Running

Check:
1. Workflow file is in `.github/workflows/`
2. File has `.yml` extension
3. YAML syntax is valid (use a validator)
4. Branch name matches trigger configuration

---

## 🎓 Interactive Exercises

### Exercise 1: Add a Build Badge

Add this to your `README.md`:

```markdown
![Build Status](https://github.com/YOUR_USERNAME/VocabMania2024/workflows/01%20-%20Basic%20Build/badge.svg)
```

Replace `YOUR_USERNAME` with your GitHub username.

### Exercise 2: Create a Custom Lane

Add to `fastlane/Fastfile`:

```ruby
desc "My custom lane"
lane :my_lane do
  puts "Hello from Fastlane!"
  gradle(task: "assembleDebug")
  puts "Build completed!"
end
```

Run: `fastlane my_lane`

### Exercise 3: Trigger a Manual Workflow

Add to any workflow:

```yaml
on:
  workflow_dispatch:  # Allows manual trigger
```

Then in GitHub:
1. Go to Actions
2. Select the workflow
3. Click "Run workflow"

---

## ✅ Completion Checklist

Mark these as you complete them:

- [ ] First GitHub Actions build succeeded
- [ ] Downloaded APK artifact
- [ ] Understood basic workflow syntax
- [ ] Installed Fastlane
- [ ] Ran `fastlane debug` successfully
- [ ] Read through workflow files
- [ ] Explored GitHub Actions tab
- [ ] Reviewed `CICD_LEARNING_GUIDE.md`
- [ ] (Optional) Added build badge to README
- [ ] (Optional) Created custom Fastlane lane

---

## 🎊 Congratulations!

You've completed the quick start! You now have a solid foundation in CI/CD for Android.

**What's next?**
1. Dive deeper with `CICD_LEARNING_GUIDE.md`
2. Secure your setup with `SECRETS_SETUP.md`
3. Experiment and break things (in a branch!)
4. Build awesome apps with confidence! 🚀

---

## 💬 Need Help?

- Read the comprehensive guide: `CICD_LEARNING_GUIDE.md`
- Check secrets setup: `SECRETS_SETUP.md`
- GitHub Actions docs: https://docs.github.com/en/actions
- Fastlane docs: https://docs.fastlane.tools/

