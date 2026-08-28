# AGENT Guidelines for Portfolio-Tracker

Welcome to the **Portfolio-Tracker** project. This repository is an advanced, offline-first Android application designed for portfolio tracking, asset allocation management, and smart rebalancing with modern Jetpack Compose and Material 3 UI.

---

## 🛠️ Project Architecture & Tech Stack

- **Platform**: Native Android (Kotlin)
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Architecture**: MVVM with Reactive StateFlow & Kotlin Coroutines
- **Local Persistence**: Room Database (`AppDatabase`, `PortfolioDao`, `Entities`)
- **State Management**: Android ViewModel with reactive state combining (`PortfolioViewModel`)
- **Key Modules & Utilities**:
  - `Localization.kt`: Bilingual runtime localization system supporting Persian (فارسی, RTL) and English (LTR).
  - `CurrencyFormatter.kt`: Smart number formatting, currency formatting (Toman, Rial, USD, EUR, etc.), and Persian digit conversions.
  - `SecurityManager.kt`: Encrypted SharedPreferences PIN passcode lock, Biometric authentication (Fingerprint / Face).
  - `SoundHapticHelper.kt`: Audio feedback and tactile haptics.
  - `DataBackupHelper.kt`: Complete JSON import/export and clipboard transfer.

---

## 📋 Git Workflow & Branching Strategy

To maintain repository stability and clean history, adhere strictly to the following workflow:

1. **Always Create a Feature Branch**:
   - For every new feature, bugfix, or task, create and switch to a new branch (e.g., `git checkout -b feature/<task-name>`).
   - Never commit directly to `main` during active development.
2. **Push Rapidly**:
   - Make atomic, descriptive commits and push the feature branch to the remote repository frequently.
3. **User Confirmation & Merging**:
   - After the task is fully completed, verified, and confirmed by the user, merge the branch into `main` and push to remote.
4. **Agent Files Exclusion**:
   - Keep `.agents/` and `.agent*` configuration files excluded from the git repository at all times.

---

## 🚀 Semantic Versioning & APK Release Workflow

Each time a new release APK is generated:
1. **Semantic Versioning (`vX.Y.Z`)**:
   - Update `app/build.gradle.kts`: increment `versionCode` and update `versionName` according to semantic versioning rules (`MAJOR.MINOR.PATCH`).
2. **Build Verification**:
   - Verify compilation using Gradle (`./gradlew assembleDebug` or `assembleRelease`).
3. **GitHub Release Publishing**:
   - Read the GitHub Personal Access Token dynamically from:
     `d:/Projects/Personal/Antigravity/GithubAccessToken.txt`
   - Create a Git tag (e.g. `v1.2.0`).
   - Create a GitHub Release via the GitHub REST API (`https://api.github.com/repos/:owner/:repo/releases`).
   - Upload the generated APK binary asset directly to the release.

---

## 🌐 Localization & Design Standards

- **Full Localization Symmetry**:
  - Every UI string must be declared in the `Strings` interface in `com.example.util.Localization.kt`.
  - Both `PersianStrings` and `EnglishStrings` must implement all properties without missing keys or hardcoded text.
  - Support proper RTL layout directions for Persian and LTR for English.
- **Data Integrity**:
  - Default asset categories and profiles in `AppDatabase.kt` must remain clean, generic, and unopinionated with `0.0` default target weights unless configured by the user.
