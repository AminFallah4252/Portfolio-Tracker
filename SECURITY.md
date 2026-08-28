# Security Policy

## 🛡️ Security Architecture & Privacy by Design

Portfolio Tracker is built with an **Offline-First & Zero-Knowledge Architecture**:

- **100% Offline Storage**: All investment portfolios, asset quantities, prices, target allocations, and snapshots are stored strictly locally on your device via SQLite / Room Database.
- **No Third-Party Analytics / Tracking**: No telemetries, logs, or user financial records are transmitted to remote cloud servers.
- **Local Authentication Guard**:
  - **4-Digit PIN Passcode**: Required on app start and when toggling sensitive settings.
  - **Biometric Authentication**: Fingerprint / Face ID support via `androidx.biometric.BiometricPrompt`.
  - **Privacy Mode**: One-tap masking of monetary balances (`••••••••`) with PIN verification required to reveal.
- **Hardened Release Builds**: Compiled with `android:debuggable="false"` and ProGuard / R8 minification to prevent ADB debugging and memory inspection.

## Reporting a Vulnerability

If you discover a potential security issue or vulnerability in Portfolio Tracker, please contact the maintainer directly via GitHub or open a private security advisory on GitHub.

We take security seriously and will investigate and address reported issues promptly.
