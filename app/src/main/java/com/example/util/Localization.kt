package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    PERSIAN("fa", "Persian", "فارسی"),
    ENGLISH("en", "English", "English")
}

enum class AppThemeMode(val titleFa: String, val titleEn: String) {
    SYSTEM("خودکار (سیستم)", "System Default"),
    LIGHT("روشن", "Light Mode"),
    DARK("تاریک", "Dark Mode")
}

object AppStrings {
    fun get(lang: AppLanguage): Strings = if (lang == AppLanguage.ENGLISH) EnglishStrings else PersianStrings
}

interface Strings {
    // App & Navigation
    val appTitle: String
    val appSubtitle: String
    val tabDashboard: String
    val tabAssets: String
    val tabRebalance: String
    val tabAnalytics: String
    val tabCategories: String

    // Multi-Portfolio Management
    val portfolioTitle: String
    val currentPortfolio: String
    val allPortfolios: String
    val switchPortfolio: String
    val newPortfolio: String
    val editPortfolio: String
    val deletePortfolio: String
    val portfolioName: String
    val portfolioDescription: String
    val portfolioDesc: String
    val portfolioColor: String
    val isDefaultPortfolio: String
    val deletePortfolioConfirmTitle: String
    val deletePortfolioConfirmText: (String) -> String
    val portfolioCreatedSuccess: String
    val portfolioUpdatedSuccess: String
    val createPortfolio: String
    val managePortfolios: String
    val activePortfolioBadge: String
    val createNewPortfolio: String

    // Common Actions
    val save: String
    val cancel: String
    val delete: String
    val edit: String
    val quickUpdate: String
    val add: String
    val confirm: String
    val close: String
    val apply: String
    val search: String
    val searchPlaceholder: String
    val filterAll: String
    val filterAllCategories: String
    val filterAllActions: String
    val sort: String
    val sortBy: String
    val copy: String
    val copiedToClipboard: (String) -> String
    val tapToCopyHint: String
    val categoryColor: String

    // Sort Options
    val sortValueDesc: String
    val sortValueAsc: String
    val sortWeightDesc: String
    val sortUrgency: String
    val sortNameAsc: String

    // Dashboard
    val totalPortfolioValue: String
    val liquidPortfolioValue: String
    val frozenPortfolioValue: String
    val hideValues: String
    val showValues: String
    val privacyMode: String
    val unlockToReveal: String
    val assetsCount: (Int) -> String
    val buyNeeded: String
    val sellSurplus: String
    val assetsNeedBuy: (Int) -> String
    val assetsNeedSell: (Int) -> String
    val frozenAssetsCount: (Int) -> String
    val targetWeightsValid: String
    val targetWeightsInvalid: (String) -> String
    val normalizeWeightsHint: String
    val autoNormalize: String
    val allocationBreakdown: String
    val tapForDetails: String
    val assetClasses: String
    val quickActions: String
    val smartRebalance: String
    val cashInjection: String
    val takeSnapshot: String
    val priorityRebalanceActions: String
    val itemsNeedRebalance: (Int) -> String
    val currentVsTarget: String
    val viewFullRebalance: String

    // Balance Health & Drift Visualizer
    val balanceHealthScore: String
    val balanceHealthOptimal: String
    val balanceHealthModerate: String
    val balanceHealthPoor: String
    val portfolioDriftChart: String
    val portfolioDriftSubtitle: String
    val targetAllocationDelta: String
    val underAllocated: String
    val overAllocated: String
    val wellBalanced: String

    // Frozen Asset Options
    val frozenAssetToggle: String
    val frozenAssetSubtitle: String
    val frozenAssetBadge: String
    val frozenAssetLockedHint: String
    val filterFrozenOnly: String
    val frozenPercentage: String
    val frozenPercentageLabel: (Double) -> String
    val releasedPercentageLabel: (Double) -> String
    val frozenPortion: String
    val releasedPortion: String
    val fullyFrozenNote: String
    val partiallyFrozenHint: (Double) -> String

    // Assets Screen
    val addNewAsset: String
    val editAsset: String
    val assetListTitle: (Int) -> String
    val emptyAssetsTitle: String
    val emptyAssetsSubtitle: String
    val deleteAssetConfirmTitle: String
    val deleteAssetConfirmText: (String) -> String

    // Action Statuses
    val actionBuy: String
    val actionSell: String
    val actionBalanced: String
    val actionFrozen: String
    val actionBuyLabel: String
    val actionSellLabel: String
    val actionBalancedLabel: String
    val actionFrozenLabel: String

    // Asset Card Details
    val quantity: String
    val unitPrice: String
    val totalValue: String
    val currentWeight: String
    val targetWeight: String
    val deviation: String
    val rebalanceSuggestion: String
    val notes: String
    val manualEditValue: String
    val stepDecrease: String
    val stepIncrease: String

    // Classes / Categories Screen
    val categoriesTitle: String
    val categoriesSubtitle: String
    val activeCategories: (Int) -> String
    val newCategory: String
    val editCategory: String
    val categoryName: String
    val categoryIcon: String
    val categoryTotalValue: String
    val categoryPortfolioShare: String
    val assetsInClass: (Int) -> String
    val deleteCategoryConfirmTitle: String
    val deleteCategoryConfirmText: (String) -> String
    val classGeneralTab: String
    val classAllocationTab: String
    val classTargetWeight: String
    val classTargetWeightHint: String
    val classAllocationBounds: String
    val classMinWeight: String
    val classMaxWeight: String
    val classTolerance: String
    val classToleranceHint: String
    val classDescription: String
    val classDescriptionPlaceholder: String
    val classStatusWithinRange: String
    val classStatusBelowMin: String
    val classStatusAboveMax: String

    // Rebalance Screen
    val rebalanceOverviewTitle: String
    val rebalanceOverviewSubtitle: String
    val rebalanceSummaryTotal: String
    val rebalancePlan: String
    val unitsToBuy: (String, Double) -> String
    val unitsToSell: (String, Double) -> String
    val rebalanceFrozenDisclaimer: String
    val applyRebalance: String

    // Analytics Screen
    val analyticsTitle: String
    val snapshotSuccess: String
    val recordNewSnapshot: String
    val snapshotHistory: String
    val snapshotCount: (Int) -> String
    val diversificationTitle: String
    val topHolding: String
    val riskConcentration: String
    val riskLow: String
    val riskMedium: String
    val riskHigh: String
    val assetClassBreakdown: String
    val deleteSnapshotConfirm: String

    // Cash Injection Simulator
    val cashSimulatorTitle: String
    val cashSimulatorSubtitle: String
    val injectionAmount: String
    val simulateButton: String
    val simulationResultTitle: String
    val optimalCashDistribution: String
    val cashInjectionTitle: String
    val cashInjectionDesc: String
    val cashAmount: String
    val smartAllocation: String

    // Settings
    val settingsTitle: String
    val generalSettingsSection: String
    val languageSetting: String
    val themeModeSetting: String
    val themeSystem: String
    val themeLight: String
    val themeDark: String
    val currencySetting: String
    val persianDigitsSetting: String
    val persianDigitsSubtitle: String
    val toleranceSetting: String
    val toleranceSubtitle: String
    val resetSettingsButton: String
    val resetSettingsConfirmTitle: String
    val resetSettingsConfirmText: String
    val resetSettingsConfirmAction: String
    val resetDataButton: String
    val resetConfirmTitle: String
    val resetConfirmText: String
    val resetConfirmAction: String
    val applyAndClose: String
    val backupRestoreDesc: String
    val backupRestoreAction: String

    // Sound & Haptics
    val soundHapticsSection: String
    val soundEffects: String
    val soundEffectsSubtitle: String
    val hapticFeedback: String
    val hapticSubtitle: String

    // Security & Lock
    val securitySection: String
    val passcodeLock: String
    val passcodeSubtitle: String
    val setPasscode: String
    val changePasscode: String
    val removePasscode: String
    val enterPasscode: String
    val enterNewPasscode: String
    val confirmPasscode: String
    val passcodesDoNotMatch: String
    val passcodeIncorrect: String
    val biometricUnlock: String
    val biometricSubtitle: String
    val biometricPromptTitle: String
    val biometricPromptSubtitle: String
    val biometricAuth: String
    val unlockApp: String
    val forgotPasscode: String
    val resetPasscodeConfirm: String
    val unhideSecurityTitle: String
    val unhideSecuritySubtitle: String
    val securityUnlockTitle: String
    val securityUnlockDesc: String

    // Backup & Import/Export
    val backupSection: String
    val backupDialogTitle: String
    val backupDialogDesc: String
    val exportData: String
    val exportSubtitle: String
    val importData: String
    val importSubtitle: String
    val exportTab: String
    val importTab: String
    val exportJsonButton: String
    val shareFileButton: String
    val copyJson: String
    val shareBackup: String
    val pasteJson: String
    val importDataButton: String
    val importConfirmTitle: String
    val importConfirmText: (Int, Int, Int) -> String
    val importSuccess: String
    val exportSuccess: String
    val importError: String
    val invalidBackupFile: String

    // Form fields
    val fieldName: String
    val fieldSymbol: String
    val fieldCategory: String
    val fieldQuantity: String
    val fieldUnitPrice: String
    val fieldTargetWeight: String
    val fieldNotes: String
    val quickUpdateTitle: String
    val quickUpdateSubtitle: (String) -> String
}

object PersianStrings : Strings {
    override val appTitle = "مدیریت سبد دارایی"
    override val appSubtitle = "تحلیل و ریبالانس هوشمند پورتفوی"
    override val tabDashboard = "داشبورد"
    override val tabAssets = "دارایی‌ها"
    override val tabRebalance = "ریبالانس"
    override val tabAnalytics = "تحلیل"
    override val tabCategories = "کلاس‌ها"

    // Multi-Portfolio
    override val portfolioTitle = "سبدهای سرمایه‌گذاری"
    override val currentPortfolio = "سبد جاری"
    override val allPortfolios = "لیست سبدها"
    override val switchPortfolio = "تغییر سبد"
    override val newPortfolio = "سبد جدید"
    override val editPortfolio = "ویرایش سبد"
    override val deletePortfolio = "حذف سبد"
    override val portfolioName = "نام سبد"
    override val portfolioDescription = "توضیحات و هدف سبد"
    override val portfolioDesc = "توضیحات یا استراتژی این سبد"
    override val portfolioColor = "رنگ نشانگر سبد"
    override val isDefaultPortfolio = "سبد پیش‌فرض"
    override val deletePortfolioConfirmTitle = "حذف سبد دارایی"
    override val deletePortfolioConfirmText: (String) -> String = { "آیا از حذف سبد «$it» و تمام دارایی‌های درون آن اطمینان دارید؟" }
    override val portfolioCreatedSuccess = "سبد سرمایه‌گذاری با موفقیت ایجاد شد"
    override val portfolioUpdatedSuccess = "مشخصات سبد بروزرسانی شد"
    override val createPortfolio = "ایجاد سبد جدید"
    override val managePortfolios = "مدیریت سبدهای سرمایه‌گذاری"
    override val activePortfolioBadge = "فعال"
    override val createNewPortfolio = "افزودن سبد سرمایه‌گذاری جدید"

    override val save = "ذخیره"
    override val cancel = "انصراف"
    override val delete = "حذف"
    override val edit = "ویرایش"
    override val quickUpdate = "بروزرسانی سریع"
    override val add = "افزودن"
    override val confirm = "تایید"
    override val close = "بستن"
    override val apply = "اعمال"
    override val search = "جستجو"
    override val searchPlaceholder = "جستجوی نام، نماد یا کلاس..."
    override val filterAll = "همه"
    override val filterAllCategories = "همه کلاس‌ها"
    override val filterAllActions = "همه وضعیت‌ها"
    override val sort = "مرتب‌سازی"
    override val sortBy = "مرتب‌سازی بر اساس"
    override val copy = "کپی"
    override val copiedToClipboard: (String) -> String = { "مقدار «$it» در حافظه کپی شد" }
    override val tapToCopyHint = "لمس برای کپی مقدار"
    override val categoryColor = "رنگ اختصاصی"

    override val sortValueDesc = "ارزش (زیاد به کم)"
    override val sortValueAsc = "ارزش (کم به زیاد)"
    override val sortWeightDesc = "وزن فعلی (بیشترین)"
    override val sortUrgency = "ضرورت ریبالانس"
    override val sortNameAsc = "نام دارایی (الفبا)"

    override val totalPortfolioValue = "ارزش کل خالص (Net Worth)"
    override val liquidPortfolioValue = "ارزش دارایی‌های نقدشونده"
    override val frozenPortfolioValue = "ارزش دارایی‌های منجمد"
    override val hideValues = "مخفی‌سازی مقادیر"
    override val showValues = "نمایش مقادیر"
    override val privacyMode = "حالت حریم خصوصی"
    override val unlockToReveal = "برای مشاهده مقادیر، قفل را باز کنید"
    override val assetsCount: (Int) -> String = { "$it دارایی" }
    override val buyNeeded = "مجموع خرید لازم"
    override val sellSurplus = "مجموع فروش (سیو سود)"
    override val assetsNeedBuy: (Int) -> String = { "$it دارایی نیازمند خرید" }
    override val assetsNeedSell: (Int) -> String = { "$it دارایی در مازاد وزن" }
    override val frozenAssetsCount: (Int) -> String = { "$it دارایی منجمد" }
    override val targetWeightsValid = "مجموع اوزان هدف: ۱۰۰٪ (معتبر)"
    override val targetWeightsInvalid: (String) -> String = { "مجموع اوزان هدف نقدشونده: $it" }
    override val normalizeWeightsHint = "برای ریبالانس دقیق، مجموع اوزان دارایی‌های نقدشونده باید ۱۰۰٪ باشد"
    override val autoNormalize = "تعدیل خودکار"
    override val allocationBreakdown = "ترکیب تخصیص دارایی‌ها"
    override val tapForDetails = "لمس برای جزییات"
    override val assetClasses = "کلاس‌های دارایی پورتفوی"
    override val quickActions = "عملیات سریع"
    override val smartRebalance = "ریبالانس هوشمند"
    override val cashInjection = "تزریق نقدینگی"
    override val takeSnapshot = "ثبت اسنپ‌شات"
    override val priorityRebalanceActions = "اقدامات فوری ریبالانس"
    override val itemsNeedRebalance: (Int) -> String = { "$it مورد نیازمند تعادل" }
    override val currentVsTarget = "مقایسه وزن فعلی با هدف"
    override val viewFullRebalance = "مشاهده جزئیات ریبالانس"

    // Balance Health & Drift Visualizer
    override val balanceHealthScore = "شاخص توازن سبد"
    override val balanceHealthOptimal = "توازن عالی و منطبق بر هدف"
    override val balanceHealthModerate = "انحراف متوسط از اوزان هدف"
    override val balanceHealthPoor = "انحراف بالا (نیازمند ریبالانس فوری)"
    override val portfolioDriftChart = "نمودار انحراف و سلامت توازن پورتفوی"
    override val portfolioDriftSubtitle = "بررسی بصری میزان فاصله وزن هر دارایی نسبت به درصد هدف تعیین‌شده"
    override val targetAllocationDelta = "انحراف وزنی (Delta)"
    override val underAllocated = "کسری وزن (نیازمند خرید)"
    override val overAllocated = "مازاد وزن (نیازمند فروش/سیو سود)"
    override val wellBalanced = "در محدوده تعادل"

    // Frozen Asset
    override val frozenAssetToggle = "دارایی منجمد و غیرقابل معامله (Frozen)"
    override val frozenAssetSubtitle = "در ارزش کل سبد محاسبه می‌شود؛ می‌توانید کل دارایی یا درصدی از آن را منجمد کنید."
    override val frozenAssetBadge = "❄️ منجمد"
    override val frozenAssetLockedHint = "وزن هدف برای دارایی ۱۰۰٪ منجمد روی ۰٪ قفل است."
    override val filterFrozenOnly = "فقط منجمد"
    override val frozenPercentage = "درصد انجماد دارایی"
    override val frozenPercentageLabel: (Double) -> String = { "${CurrencyFormatter.formatSmartFloat(it)}٪ منجمد" }
    override val releasedPercentageLabel: (Double) -> String = { "${CurrencyFormatter.formatSmartFloat(it)}٪ آزاد (در ریبالانس)" }
    override val frozenPortion = "بخش منجمد (خارج از ریبالانس)"
    override val releasedPortion = "بخش نقدشونده / آزاد (در ریبالانس)"
    override val fullyFrozenNote = "این دارایی ۱۰۰٪ منجمد است و وزن هدف آن روی ۰٪ قفل است."
    override val partiallyFrozenHint: (Double) -> String = { "وزن هدف برای بخش آزاد (${CurrencyFormatter.formatSmartFloat(it)}٪) در ریبالانس پورتفوی نقدشونده اعمال می‌شود." }

    override val addNewAsset = "افزودن دارایی جدید"
    override val editAsset = "ویرایش مشخصات دارایی"
    override val assetListTitle: (Int) -> String = { "لیست دارایی‌ها ($it)" }
    override val emptyAssetsTitle = "دارایی یافت نشد"
    override val emptyAssetsSubtitle = "می‌توانید فیلترها را تغییر داده یا دارایی جدیدی به سبد اضافه کنید."
    override val deleteAssetConfirmTitle = "حذف دارایی"
    override val deleteAssetConfirmText: (String) -> String = { "آیا مطمئن هستید که می‌خواهید دارایی «$it» را از پورتفوی حذف کنید؟" }

    override val actionBuy = "خرید"
    override val actionSell = "فروش"
    override val actionBalanced = "متعادل"
    override val actionFrozen = "منجمد"
    override val actionBuyLabel = "🟢 خرید (کسری)"
    override val actionSellLabel = "🔴 فروش (سود)"
    override val actionBalancedLabel = "⚪ بالانس"
    override val actionFrozenLabel = "❄️ منجمد"

    override val quantity = "مقدار / تعداد"
    override val unitPrice = "قیمت واحد"
    override val totalValue = "ارزش کل"
    override val currentWeight = "وزن فعلی"
    override val targetWeight = "وزن هدف"
    override val deviation = "انحراف از هدف"
    override val rebalanceSuggestion = "پیشنهاد ریبالانس"
    override val notes = "یادداشت‌ها"
    override val manualEditValue = "ویرایش مستقیم مقدار عددی"
    override val stepDecrease = "کاهش پله‌ای"
    override val stepIncrease = "افزایش پله‌ای"

    override val categoriesTitle = "مدیریت کلاس‌های دارایی"
    override val categoriesSubtitle = "تعریف دسته‌بندی‌های دارایی و نظارت بر وزن و ارزش هر کلاس در پورتفوی"
    override val activeCategories: (Int) -> String = { "کلاس‌های فعال ($it)" }
    override val newCategory = "کلاس دارایی جدید"
    override val editCategory = "ویرایش کلاس دارایی"
    override val categoryName = "نام کلاس"
    override val categoryIcon = "آیکون"
    override val categoryTotalValue = "ارزش کل این کلاس:"
    override val categoryPortfolioShare = "سهم از کل پورتفوی:"
    override val assetsInClass: (Int) -> String = { "$it دارایی در این کلاس" }
    override val deleteCategoryConfirmTitle = "حذف کلاس دارایی"
    override val deleteCategoryConfirmText: (String) -> String = { "آیا از حذف کلاس «$it» اطمینان دارید؟ دارایی‌های درون این کلاس نیز حذف خواهند شد." }
    override val classGeneralTab = "مشخصات عمومی"
    override val classAllocationTab = "سیاست تخصیص و ریسک"
    override val classTargetWeight = "وزن هدف در پورتفوی"
    override val classTargetWeightHint = "درصد مطلوب این کلاس از کل سبد دارایی"
    override val classAllocationBounds = "محدوده مجاز تخصیص (کف و سقف)"
    override val classMinWeight = "حداقل مجاز (کف)"
    override val classMaxWeight = "حداکثر مجاز (سقف)"
    override val classTolerance = "آستانه انحراف اختصاصی"
    override val classToleranceHint = "حساسیت ریبالانس برای این کلاس (۰ = استفاده از آستانه پیش‌فرض برنامه)"
    override val classDescription = "توضیحات و استراتژی کلاس"
    override val classDescriptionPlaceholder = "مثال: پوشش ریسک تورم و نوسانات ارزی / بازدهی میان‌مدت"
    override val classStatusWithinRange = "در محدوده مجاز تخصیص"
    override val classStatusBelowMin = "کمتر از کف مجاز"
    override val classStatusAboveMax = "بیشتر از سقف مجاز"

    override val rebalanceOverviewTitle = "ماتریس ریبالانس و تعدیل سبد"
    override val rebalanceOverviewSubtitle = "محاسبه دقیق مقدار و تعداد خرید یا فروش هر دارایی جهت همگرایی پورتفوی به اوزان هدف ایده‌آل."
    override val rebalanceSummaryTotal = "خلاصه جابجایی سرمایه"
    override val rebalancePlan = "دستورالعمل‌های معاملاتی"
    override val unitsToBuy: (String, Double) -> String = { name, units -> "خرید $units واحد از $name" }
    override val unitsToSell: (String, Double) -> String = { name, units -> "فروش $units واحد از $name" }
    override val rebalanceFrozenDisclaimer = "دارایی‌های منجمد (املاک، وجوه قفل شده و...) در محاسبات معاملات ریبالانس دخالت داده نمی‌شوند."
    override val applyRebalance = "اعمال تغییرات ریبالانس"

    override val analyticsTitle = "تحلیل روند و تنوع‌بخشی پورتفوی"
    override val snapshotSuccess = "اسنپ‌شات با موفقیت ثبت شد"
    override val recordNewSnapshot = "ثبت اسنپ‌شات جدید از ارزش فعلی سبد"
    override val snapshotHistory = "تاریخچه ثبت ارزش پورتفوی"
    override val snapshotCount: (Int) -> String = { "$it اسنپ‌شات تاریخی" }
    override val diversificationTitle = "تحلیل تنوع‌بخشی و تمرکز ریسک"
    override val topHolding = "بزرگترین موقعیت سبد:"
    override val riskConcentration = "سطح تمرکز ریسک:"
    override val riskLow = "کم (پورتفوی متوازن و توزیع‌شده)"
    override val riskMedium = "متوسط (نیاز به پایش موقعیت‌های بزرگ)"
    override val riskHigh = "بالا (تمرکز بیش از حد روی یک دارایی)"
    override val assetClassBreakdown = "تفکیک ارزش و وزن کلاس‌های دارایی"
    override val deleteSnapshotConfirm = "آیا از حذف این اسنپ‌شات اطمینان دارید؟"

    override val cashSimulatorTitle = "شبیه‌ساز تزریق نقدینگی هوشمند"
    override val cashSimulatorSubtitle = "محاسبه نحوه تخصیص بهینه پول نقد جدید به دارایی‌های دارای کسری وزن بدون نیاز به فروش دارایی‌های سودده."
    override val injectionAmount = "مبلغ نقدینگی ورودی جدید:"
    override val simulateButton = "شبیه‌سازی و محاسبه تخصیص"
    override val simulationResultTitle = "تخصیص پیشنهادی نقدینگی"
    override val optimalCashDistribution = "توزیع بهینه پول نقد جدید:"
    override val cashInjectionTitle = "تزریق نقدینگی به سبد دارایی"
    override val cashInjectionDesc = "با ورود پول جدید، دارایی‌های دارای کسری وزن تقویت می‌شوند."
    override val cashAmount = "مبلغ نقدینگی"
    override val smartAllocation = "محاسبه هوشمند"

    override val settingsTitle = "تنظیمات و سفارشی‌سازی"
    override val generalSettingsSection = "تنظیمات عمومی و ظاهر"
    override val languageSetting = "زبان برنامه (Language)"
    override val themeModeSetting = "حالت تم (Theme)"
    override val themeSystem = "خودکار"
    override val themeLight = "روشن"
    override val themeDark = "تاریک"
    override val currencySetting = "واحد پولی نمایشی"
    override val persianDigitsSetting = "ارقام فارسی (۱۲۳۴۵۶)"
    override val persianDigitsSubtitle = "نمایش اعداد و ارقام با فونت و فرمت فارسی"
    override val toleranceSetting = "حساسیت آستانه تعادل"
    override val toleranceSubtitle = "انحراف کمتر از این درصد به عنوان «متعادل» شناسایی می‌شود."
    override val resetSettingsButton = "بازنشانی تنظیمات به پیش‌فرض"
    override val resetSettingsConfirmTitle = "بازنشانی تنظیمات برنامه"
    override val resetSettingsConfirmText = "تنظیمات ظاهری، زبان، تم و تلرانس به حالت اولیه برمی‌گردند. دارایی‌ها و داده‌های شما دست‌نخورده باقی می‌مانند."
    override val resetSettingsConfirmAction = "بازنشانی تنظیمات"
    override val resetDataButton = "بازنشانی داده‌ها به نمونه اولیه اکسل"
    override val resetConfirmTitle = "بازنشانی پایگاه داده دارایی‌ها"
    override val resetConfirmText = "آیا مایلید تمام دارایی‌ها و کلاس‌ها به داده‌های نمونه اولیه (طلا، سهام، تتر، مس، فملی و...) بازنشانی شوند؟"
    override val resetConfirmAction = "بازنشانی"
    override val applyAndClose = "ذخیره و اعمال"
    override val backupRestoreDesc = "پشتیبان‌گیری کامل از اطلاعات، سبدها و انتقال داده‌ها با فرمت استاندارد JSON"
    override val backupRestoreAction = "پشتیبان‌گیری و بازیابی داده‌ها"

    // Sound & Haptics
    override val soundHapticsSection = "صدا و بازخورد لمسی (Haptic)"
    override val soundEffects = "افکت‌های صوتی تعاملی"
    override val soundEffectsSubtitle = "پخش صدای ملایم کلیدها، تایید و عملیات"
    override val hapticFeedback = "بازخورد لرزشی (Haptic)"
    override val hapticSubtitle = "لرزش حسی کوتاه هنگام کلیک، سوئیچ و تغییرات"

    // Security & Lock
    override val securitySection = "امنیت و قفل برنامه"
    override val passcodeLock = "قفل با رمز عبور (PIN)"
    override val passcodeSubtitle = "درخواست رمز هنگام ورود به برنامه یا رفع مخفی‌سازی"
    override val setPasscode = "تنظیم رمز عبور"
    override val changePasscode = "تغییر رمز عبور"
    override val removePasscode = "حذف رمز عبور"
    override val enterPasscode = "رمز عبور را وارد کنید"
    override val enterNewPasscode = "رمز عبور ۴ رقمی جدید را وارد کنید"
    override val confirmPasscode = "تکرار رمز عبور جدید"
    override val passcodesDoNotMatch = "رمزهای وارد شده یکسان نیستند"
    override val passcodeIncorrect = "رمز عبور نادرست است"
    override val biometricUnlock = "ورود با اثر انگشت / بیومتریک"
    override val biometricSubtitle = "استفاده از سنسور اثر انگشت یا چهره برای احراز هویت سریع"
    override val biometricPromptTitle = "احراز هویت امن سبد دارایی"
    override val biometricPromptSubtitle = "جهت تایید هویت، حسگر اثر انگشت را لمس کنید"
    override val biometricAuth = "احراز هویت با اثر انگشت"
    override val unlockApp = "بازگشایی قفل برنامه"
    override val forgotPasscode = "فراموشی رمز عبور؟"
    override val resetPasscodeConfirm = "در صورت فراموشی رمز، با بازنشانی اطلاعات به داده‌های اولیه می‌توانید وارد شوید."
    override val unhideSecurityTitle = "احراز هویت امنیتی"
    override val unhideSecuritySubtitle = "جهت نمایش ارقام و مبالغ، رمز عبور یا اثر انگشت خود را وارد نمایید."
    override val securityUnlockTitle = "احراز هویت امن"
    override val securityUnlockDesc = "جهت ادامه، رمز عبور خود را وارد کنید."

    // Backup & Import/Export
    override val backupSection = "پشتیبان‌گیری و انتقال داده‌ها"
    override val backupDialogTitle = "پشتیبان‌گیری و بازیابی داده‌ها"
    override val backupDialogDesc = "خروجی و ورودی داده‌های پورتفوی با فرمت استاندارد JSON"
    override val exportData = "خروجی گرفتن از داده‌ها (Export)"
    override val exportSubtitle = "دریافت فایل پشتیبان JSON از کلیه سبدها، دارایی‌ها، کلاس‌ها و تاریخچه"
    override val importData = "بارگذاری فایل پشتیبان (Import)"
    override val importSubtitle = "بازیابی اطلاعات از فایل پشتیبان JSON یا متن"
    override val exportTab = "خروجی (Export)"
    override val importTab = "ورودی (Import)"
    override val exportJsonButton = "تولید و دانلود JSON"
    override val shareFileButton = "اشتراک‌گذاری فایل"
    override val copyJson = "کپی متن پشتیبان (JSON)"
    override val shareBackup = "اشتراک‌گذاری / ذخیره فایل"
    override val pasteJson = "چسباندن و وارد کردن متن JSON"
    override val importDataButton = "وارد کردن فایل پشتیبان"
    override val importConfirmTitle = "تایید بازیابی فایل پشتیبان"
    override val importConfirmText: (Int, Int, Int) -> String = { assets, cats, snaps ->
        "فایل پشتیبان شامل $assets دارایی، $cats کلاس دارایی و $snaps رکورد تاریخچه است. آیا مایلید داده‌های فعلی با این اطلاعات جایگزین شوند؟"
    }
    override val importSuccess = "اطلاعات با موفقیت بازیابی شد"
    override val exportSuccess = "فایل پشتیبان با موفقیت ایجاد شد"
    override val importError = "فایل پشتیبان نامعتبر یا دارای خطا است"
    override val invalidBackupFile = "فرمت فایل نامعتبر است"

    override val fieldName = "نام دارایی (مثال: طلا بلو)"
    override val fieldSymbol = "نماد یا کد اختصاری (اختیاری)"
    override val fieldCategory = "کلاس دارایی"
    override val fieldQuantity = "مقدار یا تعداد"
    override val fieldUnitPrice = "قیمت هر واحد"
    override val fieldTargetWeight = "وزن هدف در سبد (درصد)"
    override val fieldNotes = "یادداشت یا توضیحات"
    override val quickUpdateTitle = "بروزرسانی سریع قیمت و موجودی"
    override val quickUpdateSubtitle: (String) -> String = { "تغییر سریع مقادیر دارایی «$it»" }
}

object EnglishStrings : Strings {
    override val appTitle = "Portfolio Tracker"
    override val appSubtitle = "Smart Rebalancing & Portfolio Analytics"
    override val tabDashboard = "Dashboard"
    override val tabAssets = "Assets"
    override val tabRebalance = "Rebalance"
    override val tabAnalytics = "Analytics"
    override val tabCategories = "Classes"

    // Multi-Portfolio
    override val portfolioTitle = "Portfolios"
    override val currentPortfolio = "Current Portfolio"
    override val allPortfolios = "All Portfolios"
    override val switchPortfolio = "Switch Portfolio"
    override val newPortfolio = "New Portfolio"
    override val editPortfolio = "Edit Portfolio"
    override val deletePortfolio = "Delete Portfolio"
    override val portfolioName = "Portfolio Name"
    override val portfolioDescription = "Description & Objective"
    override val portfolioDesc = "Portfolio description or strategy notes"
    override val portfolioColor = "Color Accent"
    override val isDefaultPortfolio = "Default Portfolio"
    override val deletePortfolioConfirmTitle = "Delete Portfolio"
    override val deletePortfolioConfirmText: (String) -> String = { "Are you sure you want to delete portfolio \"$it\" and all its assets?" }
    override val portfolioCreatedSuccess = "Portfolio created successfully"
    override val portfolioUpdatedSuccess = "Portfolio details updated"
    override val createPortfolio = "Create Portfolio"
    override val managePortfolios = "Manage Investment Portfolios"
    override val activePortfolioBadge = "Active"
    override val createNewPortfolio = "Create New Portfolio"

    override val save = "Save"
    override val cancel = "Cancel"
    override val delete = "Delete"
    override val edit = "Edit"
    override val quickUpdate = "Quick Update"
    override val add = "Add"
    override val confirm = "Confirm"
    override val close = "Close"
    override val apply = "Apply"
    override val search = "Search"
    override val searchPlaceholder = "Search asset name, symbol, or class..."
    override val filterAll = "All"
    override val filterAllCategories = "All Classes"
    override val filterAllActions = "All Statuses"
    override val sort = "Sort"
    override val sortBy = "Sort by"
    override val copy = "Copy"
    override val copiedToClipboard: (String) -> String = { "Copied \"$it\" to clipboard" }
    override val tapToCopyHint = "Tap to copy value"
    override val categoryColor = "Accent Color"

    override val sortValueDesc = "Value (High to Low)"
    override val sortValueAsc = "Value (Low to High)"
    override val sortWeightDesc = "Current Weight (Highest)"
    override val sortUrgency = "Rebalance Urgency"
    override val sortNameAsc = "Asset Name (A-Z)"

    override val totalPortfolioValue = "Total Net Worth"
    override val liquidPortfolioValue = "Liquid Portfolio Value"
    override val frozenPortfolioValue = "Frozen / Illiquid Value"
    override val hideValues = "Hide Values"
    override val showValues = "Show Values"
    override val privacyMode = "Privacy Mode"
    override val unlockToReveal = "Unlock to reveal portfolio values"
    override val assetsCount: (Int) -> String = { "$it Assets" }
    override val buyNeeded = "Total Buy Needed"
    override val sellSurplus = "Total Sell Surplus"
    override val assetsNeedBuy: (Int) -> String = { "$it assets need buying" }
    override val assetsNeedSell: (Int) -> String = { "$it assets in surplus" }
    override val frozenAssetsCount: (Int) -> String = { "$it frozen assets" }
    override val targetWeightsValid = "Target Weights Total: 100% (Valid)"
    override val targetWeightsInvalid: (String) -> String = { "Liquid Target Weights Total: $it" }
    override val normalizeWeightsHint = "Liquid asset target weights must equal 100% for accurate rebalancing"
    override val autoNormalize = "Auto Normalize"
    override val allocationBreakdown = "Asset Allocation Mix"
    override val tapForDetails = "Tap for details"
    override val assetClasses = "Portfolio Asset Classes"
    override val quickActions = "Quick Actions"
    override val smartRebalance = "Smart Rebalance"
    override val cashInjection = "Cash Injection"
    override val takeSnapshot = "Take Snapshot"
    override val priorityRebalanceActions = "Priority Rebalance Actions"
    override val itemsNeedRebalance: (Int) -> String = { "$it items need adjustment" }
    override val currentVsTarget = "Current vs. Target Weight"
    override val viewFullRebalance = "View Full Rebalance Details"

    // Balance Health & Drift Visualizer
    override val balanceHealthScore = "Portfolio Health Score"
    override val balanceHealthOptimal = "Optimal Balance & Target Alignment"
    override val balanceHealthModerate = "Moderate Drift from Targets"
    override val balanceHealthPoor = "High Drift (Rebalance Recommended)"
    override val portfolioDriftChart = "Allocation Drift & Health Breakdown"
    override val portfolioDriftSubtitle = "Visual inspection of current asset weights compared to target allocation percentages"
    override val targetAllocationDelta = "Weight Deviation (Delta)"
    override val underAllocated = "Under-allocated (Buy Needed)"
    override val overAllocated = "Over-allocated (Sell Surplus)"
    override val wellBalanced = "Well-Balanced"

    // Frozen Asset
    override val frozenAssetToggle = "Frozen / Illiquid Asset"
    override val frozenAssetSubtitle = "Included in total net worth assessment. You can freeze the entire asset or a percentage of it."
    override val frozenAssetBadge = "❄️ Frozen"
    override val frozenAssetLockedHint = "Target weight is locked to 0% for 100% frozen assets."
    override val filterFrozenOnly = "Frozen Only"
    override val frozenPercentage = "Frozen Percentage"
    override val frozenPercentageLabel: (Double) -> String = { "${CurrencyFormatter.formatSmartFloat(it)}% Frozen" }
    override val releasedPercentageLabel: (Double) -> String = { "${CurrencyFormatter.formatSmartFloat(it)}% Released (In Rebalance)" }
    override val frozenPortion = "Frozen Portion (Excluded from Rebalance)"
    override val releasedPortion = "Liquid / Released Portion (In Rebalance)"
    override val fullyFrozenNote = "This asset is 100% frozen; target weight is locked at 0%."
    override val partiallyFrozenHint: (Double) -> String = { "Target weight applies to the released portion (${CurrencyFormatter.formatSmartFloat(it)}%) in the liquid portfolio." }

    override val addNewAsset = "Add New Asset"
    override val editAsset = "Edit Asset"
    override val assetListTitle: (Int) -> String = { "Assets List ($it)" }
    override val emptyAssetsTitle = "No Assets Found"
    override val emptyAssetsSubtitle = "Try adjusting your search filters or add a new asset to your portfolio."
    override val deleteAssetConfirmTitle = "Delete Asset"
    override val deleteAssetConfirmText: (String) -> String = { "Are you sure you want to remove \"$it\" from your portfolio?" }

    override val actionBuy = "Buy"
    override val actionSell = "Sell"
    override val actionBalanced = "Balanced"
    override val actionFrozen = "Frozen"
    override val actionBuyLabel = "🟢 Buy (Deficit)"
    override val actionSellLabel = "🔴 Sell (Surplus)"
    override val actionBalancedLabel = "⚪ Balanced"
    override val actionFrozenLabel = "❄️ Frozen"

    override val quantity = "Quantity"
    override val unitPrice = "Unit Price"
    override val totalValue = "Total Value"
    override val currentWeight = "Current Weight"
    override val targetWeight = "Target Weight"
    override val deviation = "Target Deviation"
    override val rebalanceSuggestion = "Rebalance Suggestion"
    override val notes = "Notes"
    override val manualEditValue = "Edit numerical value directly"
    override val stepDecrease = "Step decrease"
    override val stepIncrease = "Step increase"

    override val categoriesTitle = "Asset Class Management"
    override val categoriesSubtitle = "Categorize your assets (Gold, Stocks, Crypto, Real Estate, etc.) and monitor asset class weights."
    override val activeCategories: (Int) -> String = { "Active Classes ($it)" }
    override val newCategory = "New Asset Class"
    override val editCategory = "Edit Asset Class"
    override val categoryName = "Class Name"
    override val categoryIcon = "Icon"
    override val categoryTotalValue = "Total Class Value:"
    override val categoryPortfolioShare = "Portfolio Share:"
    override val assetsInClass: (Int) -> String = { "$it assets in this class" }
    override val deleteCategoryConfirmTitle = "Delete Asset Class"
    override val deleteCategoryConfirmText: (String) -> String = { "Are you sure you want to delete class \"$it\"? All assets in this class will also be removed." }
    override val classGeneralTab = "General Info"
    override val classAllocationTab = "Allocation & Risk"
    override val classTargetWeight = "Target Portfolio Weight"
    override val classTargetWeightHint = "Desired percentage of total portfolio for this asset class"
    override val classAllocationBounds = "Allocation Bounds (Min & Max)"
    override val classMinWeight = "Minimum Allowed (Floor)"
    override val classMaxWeight = "Maximum Allowed (Cap)"
    override val classTolerance = "Custom Tolerance Threshold"
    override val classToleranceHint = "Rebalance sensitivity for this class (0 = use app global default)"
    override val classDescription = "Class Description & Strategy"
    override val classDescriptionPlaceholder = "e.g., Inflation hedge, growth compounding, risk reserve..."
    override val classStatusWithinRange = "Within Target Allocation Range"
    override val classStatusBelowMin = "Below Minimum Bound"
    override val classStatusAboveMax = "Exceeds Maximum Bound"

    override val rebalanceOverviewTitle = "Rebalance & Allocation Matrix"
    override val rebalanceOverviewSubtitle = "Calculates exact trade amounts and units to align your holdings with target allocation percentages."
    override val rebalanceSummaryTotal = "Capital Movement Summary"
    override val rebalancePlan = "Trade Execution Plan"
    override val unitsToBuy: (String, Double) -> String = { name, units -> "Buy $units units of $name" }
    override val unitsToSell: (String, Double) -> String = { name, units -> "Sell $units units of $name" }
    override val rebalanceFrozenDisclaimer = "Frozen assets (real estate, illiquid positions) are excluded from rebalance trades."
    override val applyRebalance = "Apply Rebalance Trades"

    override val analyticsTitle = "Historical Analytics & Diversification"
    override val snapshotSuccess = "Portfolio snapshot recorded successfully"
    override val recordNewSnapshot = "Record New Portfolio Snapshot"
    override val snapshotHistory = "Portfolio Value History"
    override val snapshotCount: (Int) -> String = { "$it Historical Snapshots" }
    override val diversificationTitle = "Diversification & Risk Concentration"
    override val topHolding = "Top Portfolio Position:"
    override val riskConcentration = "Risk Concentration Level:"
    override val riskLow = "Low (Well diversified portfolio)"
    override val riskMedium = "Medium (Monitor top holding sizes)"
    override val riskHigh = "High (Heavy concentration in single asset)"
    override val assetClassBreakdown = "Asset Class Breakdown & Weights"
    override val deleteSnapshotConfirm = "Are you sure you want to delete this snapshot?"

    override val cashSimulatorTitle = "Smart Cash Injection Simulator"
    override val cashSimulatorSubtitle = "Calculates optimal cash distribution to buy deficit assets without selling existing winning positions."
    override val injectionAmount = "New Cash Injection Amount:"
    override val simulateButton = "Calculate Optimal Allocation"
    override val simulationResultTitle = "Proposed Cash Allocation"
    override val optimalCashDistribution = "Optimal Cash Distribution:"
    override val cashInjectionTitle = "Cash Injection Simulator"
    override val cashInjectionDesc = "Allocate new cash to deficit assets without liquidating profitable holdings."
    override val cashAmount = "Cash Amount"
    override val smartAllocation = "Smart Allocation"

    override val settingsTitle = "Settings & Preferences"
    override val generalSettingsSection = "General Settings & UI"
    override val languageSetting = "App Language"
    override val themeModeSetting = "Theme Mode"
    override val themeSystem = "System"
    override val themeLight = "Light"
    override val themeDark = "Dark"
    override val currencySetting = "Display Currency Unit"
    override val persianDigitsSetting = "Persian Digits (۱۲۳۴۵۶)"
    override val persianDigitsSubtitle = "Format all numbers with Persian digits"
    override val toleranceSetting = "Balance Tolerance Threshold"
    override val toleranceSubtitle = "Deviations smaller than this percentage are treated as balanced."
    override val resetSettingsButton = "Reset Settings to Default"
    override val resetSettingsConfirmTitle = "Reset Application Settings"
    override val resetSettingsConfirmText = "Appearance, language, theme and tolerance will be restored to defaults. Your portfolio assets will remain untouched."
    override val resetSettingsConfirmAction = "Reset Settings"
    override val resetDataButton = "Reset Database to Sample Data"
    override val resetConfirmTitle = "Reset Holdings Database"
    override val resetConfirmText = "Are you sure you want to reload default sample assets (Gold, Stocks, USDT, Copper, Fixed Income, etc.)?"
    override val resetConfirmAction = "Reset"
    override val applyAndClose = "Save & Apply"
    override val backupRestoreDesc = "Backup and restore your portfolios, holdings, and categories via standard JSON"
    override val backupRestoreAction = "Backup & Restore Data"

    // Sound & Haptics
    override val soundHapticsSection = "Sound & Haptic Feedback"
    override val soundEffects = "UI Sound Effects"
    override val soundEffectsSubtitle = "Play subtle sounds for taps, keypads, and success cues"
    override val hapticFeedback = "Haptic Vibration Feedback"
    override val hapticSubtitle = "Tactile physical vibration on interactions"

    // Security & Lock
    override val securitySection = "Security & App Lock"
    override val passcodeLock = "Passcode Lock (PIN)"
    override val passcodeSubtitle = "Require numeric PIN when opening the app or unhiding values"
    override val setPasscode = "Set PIN Passcode"
    override val changePasscode = "Change PIN"
    override val removePasscode = "Remove PIN"
    override val enterPasscode = "Enter Passcode"
    override val enterNewPasscode = "Enter 4-digit New PIN"
    override val confirmPasscode = "Confirm New PIN"
    override val passcodesDoNotMatch = "Entered PINs do not match"
    override val passcodeIncorrect = "Incorrect PIN"
    override val biometricUnlock = "Fingerprint / Biometrics"
    override val biometricSubtitle = "Use biometric sensor for quick authentication"
    override val biometricPromptTitle = "Unlock Portfolio"
    override val biometricPromptSubtitle = "Touch the fingerprint sensor to authenticate"
    override val biometricAuth = "Biometric Authentication"
    override val unlockApp = "Unlock App"
    override val forgotPasscode = "Forgot PIN?"
    override val resetPasscodeConfirm = "In case you forgot your PIN, resetting to sample data will restore access."
    override val unhideSecurityTitle = "Security Verification"
    override val unhideSecuritySubtitle = "Enter your PIN or use biometrics to reveal portfolio values."
    override val securityUnlockTitle = "Security Unlock"
    override val securityUnlockDesc = "Please enter your passcode to proceed."

    // Backup & Import/Export
    override val backupSection = "Data Backup & Transfer"
    override val backupDialogTitle = "Data Backup & Restore"
    override val backupDialogDesc = "Export and import portfolio data in standard JSON format"
    override val exportData = "Export Backup Data (JSON)"
    override val exportSubtitle = "Export full JSON backup of all portfolios, holdings, categories & history"
    override val importData = "Import Backup Data (JSON)"
    override val importSubtitle = "Restore holdings from a backup JSON file or text"
    override val exportTab = "Export"
    override val importTab = "Import"
    override val exportJsonButton = "Generate JSON"
    override val shareFileButton = "Share Backup File"
    override val copyJson = "Copy JSON to Clipboard"
    override val shareBackup = "Share / Save Backup File"
    override val pasteJson = "Paste JSON Content"
    override val importDataButton = "Import Backup File"
    override val importConfirmTitle = "Confirm Data Restore"
    override val importConfirmText: (Int, Int, Int) -> String = { assets, cats, snaps ->
        "Backup contains $assets assets, $cats asset classes and $snaps history records. Replace current portfolio with this data?"
    }
    override val importSuccess = "Portfolio restored successfully"
    override val exportSuccess = "Backup created successfully"
    override val importError = "Invalid or corrupted backup format"
    override val invalidBackupFile = "Invalid backup format"

    // Form fields
    override val fieldName = "Asset Name (e.g. Gold Blue)"
    override val fieldSymbol = "Symbol / Ticker (Optional)"
    override val fieldCategory = "Asset Class"
    override val fieldQuantity = "Quantity"
    override val fieldUnitPrice = "Unit Price"
    override val fieldTargetWeight = "Target Weight (%)"
    override val fieldNotes = "Notes & Comments"
    override val quickUpdateTitle = "Quick Price & Quantity Update"
    override val quickUpdateSubtitle: (String) -> String = { "Update quantity and price for \"$it\"" }
}
