<div align="center">

# 📊 Portfolio Tracker & Smart Rebalancer

**A modern, offline-first Android application for intelligent investment portfolio management, asset allocation, and mathematical rebalancing.**

[![Release](https://img.shields.io/badge/Release-v1.2.0-blue.svg)](https://github.com/AminFallah4252/Portfolio-Tracker/releases/tag/v1.2.0)
[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2024%2B)-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline%20%26%20Zero--Knowledge-success)](#-security-privacy--zero-knowledge)

[English](#english) • [فارسی](#فارسی)

</div>

---

<a name="english"></a>
## 🚀 Overview

**Portfolio Tracker** empowers investors to manage multiple portfolios (stocks, gold, crypto, cash, real estate), visualize asset allocation drift, and calculate exact buy/sell rebalancing trade orders with mathematical precision. 

Everything runs **100% locally and offline on your device**—ensuring absolute financial privacy with zero external tracking.

---

## ✨ Core Features

### 1. 💼 Multi-Portfolio Management
- Create, customize, and manage multiple distinct investment portfolios (e.g., *Main Portfolio*, *Retirement Fund*, *High Growth*, *Trading*).
- Isolate asset holdings, categories, and target allocations per portfolio.
- Set a default portfolio for instant launch.

### 2. ⚖️ Smart Mathematical Rebalance Engine
- Automatically compares current asset weights against your target allocation percentages.
- Computes exact trade execution plans:
  - 🟢 **Buy Orders** for deficit positions.
  - 🔴 **Sell Orders (Profit Taking)** for surplus positions.
  - ⚪ **Balanced** status based on customizable tolerance thresholds (e.g., $\pm 1.0\%$).
- **One-Tap Normalization**: Proportionally adjust liquid targets to ensure $100\%$ sum.

### 3. 💵 Smart Cash Injection Simulator
- Calculate optimal distribution when depositing fresh capital (savings, paychecks, dividends).
- Distributes new cash exclusively to underweight assets to restore balance **without selling winning positions** or triggering transaction fees/taxes.

### 4. 📈 Balance Health Score & Drift Visualizer
- **Health Score (0–100)**: Real-time metric quantifying how closely your current holdings match target weights.
- **Allocation Drift Chart**: Visual green/red bar charts highlighting over-allocated and under-allocated positions.

### 5. ❄️ Frozen / Illiquid Asset Support
- Support for illiquid assets (real estate, locked deposits, staked funds, physical property).
- Freeze $100\%$ or specify a custom percentage (e.g., $40\%$ frozen / $60\%$ liquid).
- Frozen assets contribute to Total Net Worth but are strictly excluded from liquid rebalance trade calculations.

### 6. 🏷️ Asset Classes & Risk Allocation Guardrails
- Categorize holdings (Gold, Equities, Fixed Income, Crypto, Real Estate, Cash).
- Define custom floor (Min $\%$) and cap (Max $\%$) bounds to prevent portfolio over-concentration.
- Warning badges trigger when asset classes drift outside safety limits.

### 7. 📊 Historical Analytics & Diversification
- Record point-in-time portfolio snapshots.
- Interactive growth trend charts tracking portfolio evolution over time.
- Diversification level and top-holding risk concentration analyzer.

### 8. 🛡️ Security, Privacy & Biometric Lock
- **4-Digit PIN Passcode**: Protects app access.
- **Biometric Unlock**: Fingerprint and Face ID support via AndroidX Biometric.
- **Privacy Mode**: One-tap masking of monetary balances (`••••••••`) with PIN verification required to reveal.

### 9. 💾 Offline Backup & Cross-Device Portability
- Full JSON backup export and import.
- Instant clipboard copy/paste for seamless device migration.
- 100% offline local Room database.

### 10. 📚 Interactive In-App User Guide & Tutorials
- Comprehensive, step-by-step interactive guide covering all 9 modules.
- Real-time search filter and category selector.
- Pro tips and actionable instructions.

---

## 🏛️ Architecture & Tech Stack

```
┌─────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI                    │
│   Material 3 • RTL/LTR Layout Direction • Animations   │
└────────────────────────────┬────────────────────────────┘
                             │ StateFlow / Events
┌────────────────────────────▼────────────────────────────┐
│                   PortfolioViewModel                    │
│    Unidirectional Data Flow • Coroutines Dispatchers    │
└──────────────┬───────────────────────────┬──────────────┘
               │                           │
┌──────────────▼──────────┐ ┌──────────────▼──────────────┐
│  PortfolioCalculation   │ │    PortfolioRepository      │
│  Mathematical Rebalance │ │  Single Source of Truth     │
└─────────────────────────┘ └──────────────┬──────────────┘
                                           │
                            ┌──────────────▼──────────────┐
                            │    Room Database (SQLite)   │
                            │  Portfolios • Assets • Cats │
                            └─────────────────────────────┘
```

- **UI Framework**: Jetpack Compose with Material 3 design tokens.
- **Architecture**: MVVM + Unidirectional Data Flow (UDF).
- **Database**: Room Database with Kotlin Coroutines & Flow.
- **Serialization**: Moshi Kotlin Codegen & Kotlinx Serialization.
- **Security**: AndroidX Biometric API & Encrypted SharedPreferences.
- **Testing**: JUnit 4, Robolectric, and Roborazzi screenshot testing.
- **Build System**: Gradle Kotlin DSL with R8 / ProGuard whole-program optimization.

---

## 🛠️ Build & Installation

### Prerequisites
- **JDK**: OpenJDK 17 or higher
- **Android SDK**: API Level 36 (Build-Tools 36.0.0, Min SDK 24 / Android 7.0+)

### Building the Release APK
```bash
# Clone the repository
git clone https://github.com/AminFallah4252/Portfolio-Tracker.git
cd Portfolio-Tracker

# Build the optimized production release APK
./gradlew assembleRelease

# Run automated unit test suite
./gradlew testDebugUnitTest
```

The compiled release APK will be located at:
`app/build/outputs/apk/release/app-release.apk`

---

<a name="فارسی"></a>
## 🇮🇷 معرفی به زبان فارسی

**مدیریت سبد دارایی (Portfolio Tracker)** یک برنامه مدرن، کاملاً آفلاین و امن برای مدیریت پورتفوی سرمایه‌گذاری، بررسی انحراف اوزان و محاسبه دقیق معاملات ریبالانس و تزریق نقدینگی است.

### قابلیت‌های کلیدی:
- **مدیریت چند سبد مجزا**: تفکیک پورتفوهای مختلف با استراتژی‌ها و اوزان هدف جداگانه.
- **ماتریس ریبالانس هوشمند**: محاسبه دقیق تعداد و مبلغ خرید (کسری) و فروش (سیو سود).
- **شبیه‌ساز تزریق نقدینگی**: متعادل‌سازی سبد با ورود پول جدید بدون نیاز به فروش دارایی‌های سودده.
- **شاخص سلامت توازن (Health Score)**: ارزیابی میزان همگرایی پورتفوی با اوزان هدف.
- **پشتیبانی از دارایی‌های منجمد (Frozen)**: لحاظ املاک و وجوه قفل شده در ارزش کل بدون دخالت در معاملات ریبالانس.
- **حدود ریسک کلاس‌های دارایی**: تعیین کف و سقف مجاز برای هر طبقه دارایی (طلا، سهام، کریپتو و...).
- **امنیت و حریم خصوصی کامل**: قفل با پین ۴ رقمی، اثر انگشت / تشخیص چهره و مخفی‌سازی ارقام.
- **پشتیبان‌گیری استاندارد JSON**: انتقال آسان داده‌ها بین دستگاه‌ها بدون نیاز به اینترنت.
- **راهنما و آموزش جامع درون‌برنامه‌ای**: آموزش گام‌به‌گام تمامی بخش‌ها با جستجوی زنده.

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.
