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

    // Sort Options
    val sortValueDesc: String
    val sortValueAsc: String
    val sortWeightDesc: String
    val sortUrgency: String
    val sortNameAsc: String

    // Dashboard
    val totalPortfolioValue: String
    val assetsCount: (Int) -> String
    val buyNeeded: String
    val sellSurplus: String
    val assetsNeedBuy: (Int) -> String
    val assetsNeedSell: (Int) -> String
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
    val actionBuyLabel: String
    val actionSellLabel: String
    val actionBalancedLabel: String

    // Asset Card Details
    val quantity: String
    val unitPrice: String
    val totalValue: String
    val currentWeight: String
    val targetWeight: String
    val deviation: String
    val rebalanceSuggestion: String
    val notes: String

    // Classes / Categories Screen
    val categoriesTitle: String
    val categoriesSubtitle: String
    val activeCategories: (Int) -> String
    val newCategory: String
    val editCategory: String
    val categoryName: String
    val categoryColor: String
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

    // Settings
    val settingsTitle: String
    val languageSetting: String
    val themeModeSetting: String
    val currencySetting: String
    val persianDigitsSetting: String
    val persianDigitsSubtitle: String
    val toleranceSetting: String
    val toleranceSubtitle: String
    val resetDataButton: String
    val resetConfirmTitle: String
    val resetConfirmText: String
    val resetConfirmAction: String
    val applyAndClose: String

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

    override val sortValueDesc = "ارزش (زیاد به کم)"
    override val sortValueAsc = "ارزش (کم به زیاد)"
    override val sortWeightDesc = "وزن فعلی (بیشترین)"
    override val sortUrgency = "ضرورت ریبالانس"
    override val sortNameAsc = "نام دارایی (الفبا)"

    override val totalPortfolioValue = "ارزش کل پورتفوی"
    override val assetsCount: (Int) -> String = { "$it دارایی" }
    override val buyNeeded = "مجموع خرید لازم"
    override val sellSurplus = "مجموع فروش (سیو سود)"
    override val assetsNeedBuy: (Int) -> String = { "$it دارایی نیازمند خرید" }
    override val assetsNeedSell: (Int) -> String = { "$it دارایی در مازاد وزن" }
    override val targetWeightsValid = "مجموع اوزان هدف: ۱۰۰٪ (معتبر)"
    override val targetWeightsInvalid: (String) -> String = { "مجموع اوزان هدف: $it" }
    override val normalizeWeightsHint = "برای ریبالانس دقیق، مجموع باید ۱۰۰٪ باشد"
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
    override val actionBuyLabel = "🟢 خرید (کسری)"
    override val actionSellLabel = "🔴 فروش (سود)"
    override val actionBalancedLabel = "⚪ بالانس"

    override val quantity = "مقدار / تعداد"
    override val unitPrice = "قیمت واحد"
    override val totalValue = "ارزش کل"
    override val currentWeight = "وزن فعلی"
    override val targetWeight = "وزن هدف"
    override val deviation = "انحراف از هدف"
    override val rebalanceSuggestion = "پیشنهاد ریبالانس"
    override val notes = "یادداشت‌ها"

    override val categoriesTitle = "مدیریت کلاس‌های دارایی"
    override val categoriesSubtitle = "تعریف دسته‌بندی‌های دارایی و نظارت بر وزن و ارزش هر کلاس در پورتفوی"
    override val activeCategories: (Int) -> String = { "کلاس‌های فعال ($it)" }
    override val newCategory = "کلاس دارایی جدید"
    override val editCategory = "ویرایش کلاس دارایی"
    override val categoryName = "نام کلاس"
    override val categoryColor = "رنگ اختصاصی"
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

    override val settingsTitle = "تنظیمات و سفارشی‌سازی"
    override val languageSetting = "زبان برنامه (Language)"
    override val themeModeSetting = "حالت تم (Theme)"
    override val currencySetting = "واحد پولی نمایشی"
    override val persianDigitsSetting = "ارقام فارسی (۱۲۳۴۵۶)"
    override val persianDigitsSubtitle = "نمایش اعداد و ارقام با فونت و فرمت فارسی"
    override val toleranceSetting = "حساسیت آستانه تعادل"
    override val toleranceSubtitle = "انحراف کمتر از این درصد به عنوان «متعادل» شناسایی می‌شود."
    override val resetDataButton = "بارگذاری مجدد داده‌های اکسل اولیه"
    override val resetConfirmTitle = "بازنشانی داده‌های نمونه"
    override val resetConfirmText = "آیا مایلید تمام دارایی‌ها به داده‌های اولیه اکسل (طلا بلو، تتر، مس، فملی، بورس و...) بازنشانی شوند؟"
    override val resetConfirmAction = "بازنشانی"
    override val applyAndClose = "ذخیره و اعمال"

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

    override val sortValueDesc = "Value (High to Low)"
    override val sortValueAsc = "Value (Low to High)"
    override val sortWeightDesc = "Current Weight (Highest)"
    override val sortUrgency = "Rebalance Urgency"
    override val sortNameAsc = "Asset Name (A-Z)"

    override val totalPortfolioValue = "Total Portfolio Value"
    override val assetsCount: (Int) -> String = { "$it Assets" }
    override val buyNeeded = "Total Buy Needed"
    override val sellSurplus = "Total Sell Surplus"
    override val assetsNeedBuy: (Int) -> String = { "$it assets need buying" }
    override val assetsNeedSell: (Int) -> String = { "$it assets in surplus" }
    override val targetWeightsValid = "Target Weights Total: 100% (Valid)"
    override val targetWeightsInvalid: (String) -> String = { "Target Weights Total: $it" }
    override val normalizeWeightsHint = "Total must equal 100% for accurate rebalancing"
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
    override val actionBuyLabel = "🟢 Buy (Deficit)"
    override val actionSellLabel = "🔴 Sell (Surplus)"
    override val actionBalancedLabel = "⚪ Balanced"

    override val quantity = "Quantity"
    override val unitPrice = "Unit Price"
    override val totalValue = "Total Value"
    override val currentWeight = "Current Weight"
    override val targetWeight = "Target Weight"
    override val deviation = "Target Deviation"
    override val rebalanceSuggestion = "Rebalance Suggestion"
    override val notes = "Notes"

    override val categoriesTitle = "Asset Class Management"
    override val categoriesSubtitle = "Categorize your assets (Gold, Stocks, Crypto, Real Estate, etc.) and monitor asset class weights."
    override val activeCategories: (Int) -> String = { "Active Classes ($it)" }
    override val newCategory = "New Asset Class"
    override val editCategory = "Edit Asset Class"
    override val categoryName = "Class Name"
    override val categoryColor = "Class Color"
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

    override val settingsTitle = "Settings & Preferences"
    override val languageSetting = "App Language"
    override val themeModeSetting = "Theme Mode"
    override val currencySetting = "Display Currency Unit"
    override val persianDigitsSetting = "Persian Digits (۱۲۳۴۵۶)"
    override val persianDigitsSubtitle = "Format all numbers with Persian digits"
    override val toleranceSetting = "Balance Tolerance Threshold"
    override val toleranceSubtitle = "Deviations smaller than this percentage are treated as balanced."
    override val resetDataButton = "Reload Sample Excel Data"
    override val resetConfirmTitle = "Reset to Sample Data"
    override val resetConfirmText = "Are you sure you want to reload default sample assets (Gold, Stocks, USDT, Copper, Fixed Income, etc.)?"
    override val resetConfirmAction = "Reset"
    override val applyAndClose = "Save & Apply"

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
