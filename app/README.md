# Portfolio Tracker - Android App Module (`:app`)

The core Android application module for **Portfolio Tracker**, built with Jetpack Compose, Material 3, and Room.

---

## 🏛️ Module Architecture

The `:app` module follows the **MVVM (Model-View-ViewModel)** and **Unidirectional Data Flow (UDF)** patterns:

```
app/src/main/java/com/example/
├── data/
│   ├── local/              # Room Database, DAOs & Type Converters
│   │   ├── AppDatabase.kt
│   │   └── PortfolioDao.kt
│   ├── model/              # Domain Models, Database Entities & Math Logic
│   │   ├── Entities.kt
│   │   └── PortfolioCalculation.kt
│   └── repository/         # Single source of truth data repository
│       └── PortfolioRepository.kt
├── ui/
│   ├── components/         # Reusable Compose widgets & modal dialogs
│   │   ├── AddEditAssetDialog.kt
│   │   ├── AllocationDonutChart.kt
│   │   ├── CashInjectionModal.kt
│   │   ├── CategoryManagerDialog.kt
│   │   ├── HelpTutorialDialog.kt
│   │   ├── PasscodeUnlockDialog.kt
│   │   ├── SettingsDialog.kt
│   │   └── TrendGrowthChart.kt
│   ├── screens/            # Full-screen destination composables
│   │   ├── AnalyticsScreen.kt
│   │   ├── AssetsListScreen.kt
│   │   ├── CategoriesScreen.kt
│   │   ├── DashboardScreen.kt
│   │   ├── LockScreen.kt
│   │   ├── RebalanceScreen.kt
│   │   └── SplashScreen.kt
│   ├── theme/              # Material 3 design tokens, colors & typography
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/          # Reactive StateFlow ViewModel
│       └── PortfolioViewModel.kt
└── util/                   # Formatting, Localization & Security Utilities
    ├── CurrencyFormatter.kt
    ├── DataBackupHelper.kt
    ├── Localization.kt
    ├── SecurityManager.kt
    ├── SettingsPreferencesManager.kt
    └── SoundHapticHelper.kt
```

---

## 🔑 Key Components

- **`PortfolioViewModel`**: Exposes reactive `StateFlow` streams (`summary`, `allAssets`, `allCategories`, `allPortfolios`, `snapshots`, `selectedPortfolioId`).
- **`PortfolioCalculation`**: High-performance mathematical engine calculating net worth, liquid vs. frozen values, target weights, rebalance trade orders (units and amounts), and health drift scores.
- **`SecurityManager`**: Encrypted SharedPreferences management for 4-digit PIN lock and Biometric sensor prompt dispatcher.
- **`HelpTutorialDialog`**: Step-by-step interactive documentation for all 9 application modules with live search filtering.
