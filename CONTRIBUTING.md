# Contributing to Portfolio Tracker

We welcome contributions to Portfolio Tracker! Whether it is fixing bugs, adding new features, improving localization, or writing documentation, here is how you can help.

---

## 🌲 Git Branching & Workflow Guidelines

1. **Never commit directly to `main`**:
   - Always create a dedicated feature branch from `main`:
     ```bash
     git checkout -b feature/your-feature-name
     ```
2. **Commit Frequently**:
   - Use clear conventional commit messages:
     - `feat: ...` for new features
     - `fix: ...` for bug fixes
     - `docs: ...` for documentation
     - `refactor: ...` for code structure improvements
     - `test: ...` for test suite additions
3. **Pull Request Protocol**:
   - Push your branch to remote and open a Pull Request against `main`.
   - Ensure all automated unit tests pass before requesting review.

---

## 🌐 Localization Guidelines

Portfolio Tracker supports both **Persian (فارسی - RTL)** and **English (LTR)**.

- When adding any UI string:
  - Add the property to the `Strings` interface in `com.example.util.Localization.kt`.
  - Provide an accurate Persian translation in `PersianStrings`.
  - Provide an accurate English translation in `EnglishStrings`.
  - Avoid hardcoding text inside Composable functions.

---

## 🧪 Testing & Build Verification

Before submitting a Pull Request, run the automated test suite:

```bash
./gradlew testDebugUnitTest
```

Build the release APK to ensure R8 shrinking passes:

```bash
./gradlew assembleRelease
```
