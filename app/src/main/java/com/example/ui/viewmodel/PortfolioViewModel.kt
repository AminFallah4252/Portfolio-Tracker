package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.PortfolioRepository
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppThemeMode
import com.example.util.PortfolioBackupPayload
import com.example.util.SettingsPreferencesManager
import com.example.util.Strings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption {
    VALUE_DESC,
    VALUE_ASC,
    WEIGHT_DESC,
    REBALANCE_URGENCY,
    NAME_ASC;

    fun getTitle(strings: Strings): String = when (this) {
        VALUE_DESC -> strings.sortValueDesc
        VALUE_ASC -> strings.sortValueAsc
        WEIGHT_DESC -> strings.sortWeightDesc
        REBALANCE_URGENCY -> strings.sortUrgency
        NAME_ASC -> strings.sortNameAsc
    }
}

class PortfolioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PortfolioRepository
    private val prefsManager = SettingsPreferencesManager(application)

    val appLanguage = MutableStateFlow(prefsManager.getLanguage())
    val themeMode = MutableStateFlow(prefsManager.getThemeMode())
    val currency = MutableStateFlow(prefsManager.getCurrency())
    val usePersianDigits = MutableStateFlow(prefsManager.getUsePersianDigits())
    val tolerancePercent = MutableStateFlow(prefsManager.getTolerance())
    val activePortfolioId = MutableStateFlow(prefsManager.getActivePortfolioId())
    val isPrivacyMode = MutableStateFlow(prefsManager.getPrivacyMode())

    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<Int?>(null) // null = all
    val selectedActionFilter = MutableStateFlow<RebalanceActionType?>(null) // null = all
    val showFrozenOnly = MutableStateFlow(false)
    val sortOption = MutableStateFlow(SortOption.VALUE_DESC)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = PortfolioRepository(
            database.portfolioProfileDao(),
            database.assetDao(),
            database.categoryDao(),
            database.snapshotDao()
        )
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.populateInitialData(database)
        }
    }

    val allPortfolios: StateFlow<List<PortfolioProfile>> = repository.allPortfolios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentPortfolio: StateFlow<PortfolioProfile?> = combine(
        allPortfolios,
        activePortfolioId
    ) { portfolios, activeId ->
        portfolios.find { it.id == activeId } ?: portfolios.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allCategories: StateFlow<List<AssetCategory>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssetsForActivePortfolio: StateFlow<List<AssetItem>> = activePortfolioId
        .flatMapLatest { pId -> repository.getAssetsForPortfolio(pId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSnapshotsForActivePortfolio: StateFlow<List<PortfolioSnapshot>> = activePortfolioId
        .flatMapLatest { pId -> repository.getSnapshotsForPortfolio(pId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPortfolio(portfolioId: Int) {
        activePortfolioId.value = portfolioId
        prefsManager.setActivePortfolioId(portfolioId)
    }

    fun addPortfolio(name: String, description: String = "", colorHex: String = "#3B82F6") {
        viewModelScope.launch(Dispatchers.IO) {
            val newId = repository.insertPortfolio(
                PortfolioProfile(
                    name = name.trim(),
                    description = description.trim(),
                    colorHex = colorHex
                )
            )
            selectPortfolio(newId.toInt())
        }
    }

    fun updatePortfolio(portfolio: PortfolioProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePortfolio(portfolio)
        }
    }

    fun updatePortfolio(id: Int, name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = allPortfolios.value.find { it.id == id }
            if (existing != null) {
                repository.updatePortfolio(existing.copy(name = name.trim(), description = description.trim()))
            }
        }
    }

    fun deletePortfolio(portfolioId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePortfolio(portfolioId)
            val remaining = allPortfolios.value.filter { it.id != portfolioId }
            if (remaining.isNotEmpty()) {
                selectPortfolio(remaining.first().id)
            } else {
                // recreate a default portfolio if all deleted
                val defaultId = repository.insertPortfolio(
                    PortfolioProfile(
                        name = "سبد اصلی سرمایه‌گذاری",
                        isDefault = true
                    )
                )
                selectPortfolio(defaultId.toInt())
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        appLanguage.value = lang
        prefsManager.setLanguage(lang)
    }

    fun toggleLanguage() {
        val newLang = if (appLanguage.value == AppLanguage.PERSIAN) AppLanguage.ENGLISH else AppLanguage.PERSIAN
        setLanguage(newLang)
    }

    fun setThemeMode(mode: AppThemeMode) {
        themeMode.value = mode
        prefsManager.setThemeMode(mode)
    }

    fun toggleTheme() {
        val newMode = when (themeMode.value) {
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
        }
        setThemeMode(newMode)
    }

    fun setCurrency(newCurrency: String) {
        currency.value = newCurrency
        prefsManager.setCurrency(newCurrency)
    }

    fun setUsePersianDigits(enabled: Boolean) {
        usePersianDigits.value = enabled
        prefsManager.setUsePersianDigits(enabled)
    }

    fun setTolerance(tolerance: Double) {
        tolerancePercent.value = tolerance
        prefsManager.setTolerance(tolerance)
    }

    fun setPrivacyMode(enabled: Boolean) {
        isPrivacyMode.value = enabled
        prefsManager.setPrivacyMode(enabled)
    }

    fun togglePrivacyMode() {
        setPrivacyMode(!isPrivacyMode.value)
    }

    // Combined Reactive Portfolio Calculations
    val portfolioSummary: StateFlow<PortfolioSummary> = combine(
        allAssetsForActivePortfolio,
        allCategories,
        tolerancePercent
    ) { assets, categories, tolerance ->
        calculatePortfolio(assets, categories, tolerance)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PortfolioSummary(
            totalValue = 0.0,
            liquidTotalValue = 0.0,
            frozenTotalValue = 0.0,
            totalTargetWeight = 0.0,
            isTargetWeightValid = true,
            totalBuyAmount = 0.0,
            totalSellAmount = 0.0,
            balancedCount = 0,
            buyCount = 0,
            sellCount = 0,
            frozenCount = 0,
            healthScore = 100,
            topAsset = null,
            calculatedAssets = emptyList(),
            categorySummaries = emptyList()
        )
    )

    // Filtered & Sorted Assets
    private val filterCriteria = combine(
        searchQuery,
        selectedCategoryFilter,
        selectedActionFilter,
        showFrozenOnly,
        sortOption
    ) { query, catFilter, actionFilter, frozenOnly, sort ->
        FilterParams(query, catFilter, actionFilter, frozenOnly, sort)
    }

    val filteredCalculatedAssets: StateFlow<List<CalculatedAsset>> = combine(
        portfolioSummary,
        filterCriteria
    ) { summary, criteria ->
        var list = summary.calculatedAssets

        if (criteria.query.isNotBlank()) {
            list = list.filter {
                it.asset.name.contains(criteria.query, ignoreCase = true) ||
                it.asset.symbol.contains(criteria.query, ignoreCase = true) ||
                (it.category?.name?.contains(criteria.query, ignoreCase = true) ?: false)
            }
        }

        if (criteria.catFilter != null) {
            list = list.filter { it.asset.categoryId == criteria.catFilter }
        }

        if (criteria.actionFilter != null) {
            list = list.filter { it.actionType == criteria.actionFilter }
        }

        if (criteria.frozenOnly) {
            list = list.filter { it.asset.isFrozen }
        }

        when (criteria.sort) {
            SortOption.VALUE_DESC -> list.sortedByDescending { it.currentValue }
            SortOption.VALUE_ASC -> list.sortedBy { it.currentValue }
            SortOption.WEIGHT_DESC -> list.sortedByDescending { it.currentWeight }
            SortOption.REBALANCE_URGENCY -> list.sortedByDescending { Math.abs(it.rebalanceAmount) }
            SortOption.NAME_ASC -> list.sortedBy { it.asset.name }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

private data class FilterParams(
    val query: String,
    val catFilter: Int?,
    val actionFilter: RebalanceActionType?,
    val frozenOnly: Boolean,
    val sort: SortOption
)

    private fun calculatePortfolio(
        assets: List<AssetItem>,
        categories: List<AssetCategory>,
        tolerance: Double
    ): PortfolioSummary {
        val categoryMap = categories.associateBy { it.id }
        val totalNetWorth = assets.sumOf { it.quantity * it.unitPrice }

        val frozenTotal = assets.sumOf { (it.quantity * it.unitPrice) * (it.effectiveFrozenPercent / 100.0) }
        val liquidTotal = assets.sumOf { (it.quantity * it.unitPrice) * (it.effectiveLiquidPercent / 100.0) }

        // Assets that have a tradeable liquid portion
        val tradeableAssets = assets.filter { it.effectiveLiquidPercent > 0.0 }

        // Liquid Target Weights sum:
        val liquidTargetWeightSum = tradeableAssets.sumOf { it.targetWeight }
        val isValidWeight = tradeableAssets.isEmpty() || liquidTargetWeightSum in 99.5..100.5

        val calculatedAssets = assets.map { asset ->
            val totalCurrentValue = asset.quantity * asset.unitPrice
            val currentNetWorthWeight = if (totalNetWorth > 0) (totalCurrentValue / totalNetWorth) * 100.0 else 0.0

            val frozenVal = totalCurrentValue * (asset.effectiveFrozenPercent / 100.0)
            val liquidVal = totalCurrentValue * (asset.effectiveLiquidPercent / 100.0)

            if (asset.isFullyFrozen) {
                CalculatedAsset(
                    asset = asset,
                    category = categoryMap[asset.categoryId],
                    currentValue = totalCurrentValue,
                    currentWeight = currentNetWorthWeight,
                    targetValue = totalCurrentValue, // Fully frozen assets are not adjusted
                    rebalanceAmount = 0.0,
                    rebalanceUnits = 0.0,
                    actionType = RebalanceActionType.FROZEN,
                    weightDeviation = 0.0,
                    liquidValue = 0.0,
                    frozenValue = totalCurrentValue,
                    targetLiquidValue = 0.0,
                    liquidWeight = 0.0
                )
            } else {
                val targetLiquidVal = if (liquidTotal > 0) liquidTotal * (asset.targetWeight / 100.0) else 0.0
                val targetTotalVal = frozenVal + targetLiquidVal
                val rebalanceAmount = targetLiquidVal - liquidVal
                val rebalanceUnits = if (asset.unitPrice > 0) rebalanceAmount / asset.unitPrice else 0.0

                // Compare liquid share vs liquid target weight
                val currentLiquidWeight = if (liquidTotal > 0) (liquidVal / liquidTotal) * 100.0 else 0.0
                val deviation = currentLiquidWeight - asset.targetWeight

                val actionType = when {
                    Math.abs(deviation) <= tolerance -> RebalanceActionType.BALANCED
                    rebalanceAmount > 0 -> RebalanceActionType.BUY
                    else -> RebalanceActionType.SELL
                }

                CalculatedAsset(
                    asset = asset,
                    category = categoryMap[asset.categoryId],
                    currentValue = totalCurrentValue,
                    currentWeight = currentNetWorthWeight,
                    targetValue = targetTotalVal,
                    rebalanceAmount = rebalanceAmount,
                    rebalanceUnits = rebalanceUnits,
                    actionType = actionType,
                    weightDeviation = deviation,
                    liquidValue = liquidVal,
                    frozenValue = frozenVal,
                    targetLiquidValue = targetLiquidVal,
                    liquidWeight = currentLiquidWeight
                )
            }
        }

        val tradeableCalculated = calculatedAssets.filter { !it.asset.isFullyFrozen }
        val totalBuy = tradeableCalculated
            .filter { it.actionType == RebalanceActionType.BUY }
            .sumOf { it.rebalanceAmount }

        val totalSell = tradeableCalculated
            .filter { it.actionType == RebalanceActionType.SELL }
            .sumOf { Math.abs(it.rebalanceAmount) }

        val balancedCount = tradeableCalculated.count { it.actionType == RebalanceActionType.BALANCED }
        val buyCount = tradeableCalculated.count { it.actionType == RebalanceActionType.BUY }
        val sellCount = tradeableCalculated.count { it.actionType == RebalanceActionType.SELL }
        val frozenCount = calculatedAssets.count { it.asset.isFrozen }
        val topAsset = calculatedAssets.maxByOrNull { it.currentValue }

        // Balance Health Score (0..100): 100% minus average absolute deviation
        val totalAbsDeviation = tradeableCalculated.sumOf { Math.abs(it.weightDeviation) }
        val healthScore = if (tradeableCalculated.isEmpty()) 100 else {
            val score = 100.0 - (totalAbsDeviation / 2.0)
            score.coerceIn(0.0, 100.0).toInt()
        }

        // Category summaries
        val categorySummaries = categories.map { cat ->
            val catAssets = calculatedAssets.filter { it.asset.categoryId == cat.id }
            val catTotal = catAssets.sumOf { it.currentValue }
            val catFrozen = catAssets.sumOf { it.frozenValue }
            val catWeight = if (totalNetWorth > 0) (catTotal / totalNetWorth) * 100.0 else 0.0
            val catTargetWeight = catAssets.filter { !it.asset.isFullyFrozen }.sumOf { it.asset.targetWeight }

            CategorySummary(
                category = cat,
                totalValue = catTotal,
                currentWeight = catWeight,
                targetWeight = catTargetWeight,
                assetCount = catAssets.size,
                frozenValue = catFrozen
            )
        }.filter { it.totalValue > 0 || it.targetWeight > 0 }

        return PortfolioSummary(
            totalValue = totalNetWorth,
            liquidTotalValue = liquidTotal,
            frozenTotalValue = frozenTotal,
            totalTargetWeight = liquidTargetWeightSum,
            isTargetWeightValid = isValidWeight,
            totalBuyAmount = totalBuy,
            totalSellAmount = totalSell,
            balancedCount = balancedCount,
            buyCount = buyCount,
            sellCount = sellCount,
            frozenCount = frozenCount,
            healthScore = healthScore,
            topAsset = topAsset,
            calculatedAssets = calculatedAssets,
            categorySummaries = categorySummaries
        )
    }

    fun addAsset(
        name: String,
        symbol: String,
        categoryId: Int,
        quantity: Double,
        unitPrice: Double,
        targetWeight: Double,
        isFrozen: Boolean,
        frozenPercentage: Double = 100.0,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val effectiveFrozen = if (isFrozen) frozenPercentage.coerceIn(0.0, 100.0) else 0.0
            val effectiveTargetWeight = if (isFrozen && effectiveFrozen >= 100.0) 0.0 else targetWeight
            repository.insertAsset(
                AssetItem(
                    portfolioId = activePortfolioId.value,
                    name = name.trim(),
                    symbol = symbol.trim().uppercase(),
                    categoryId = categoryId,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    targetWeight = effectiveTargetWeight,
                    isFrozen = isFrozen,
                    frozenPercentage = if (isFrozen) effectiveFrozen else 100.0,
                    notes = notes.trim()
                )
            )
        }
    }

    fun updateAsset(
        id: Int,
        name: String,
        symbol: String,
        categoryId: Int,
        quantity: Double,
        unitPrice: Double,
        targetWeight: Double,
        isFrozen: Boolean,
        frozenPercentage: Double = 100.0,
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val effectiveFrozen = if (isFrozen) frozenPercentage.coerceIn(0.0, 100.0) else 0.0
            val effectiveTargetWeight = if (isFrozen && effectiveFrozen >= 100.0) 0.0 else targetWeight
            repository.updateAsset(
                AssetItem(
                    id = id,
                    portfolioId = activePortfolioId.value,
                    name = name.trim(),
                    symbol = symbol.trim().uppercase(),
                    categoryId = categoryId,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    targetWeight = effectiveTargetWeight,
                    isFrozen = isFrozen,
                    frozenPercentage = if (isFrozen) effectiveFrozen else 100.0,
                    notes = notes.trim(),
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun quickUpdateHolding(id: Int, newQuantity: Double, newUnitPrice: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val asset = allAssetsForActivePortfolio.value.find { it.id == id } ?: return@launch
            repository.updateAsset(
                asset.copy(
                    quantity = newQuantity,
                    unitPrice = newUnitPrice,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteAsset(asset: AssetItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAsset(asset)
        }
    }

    fun addCategory(
        name: String,
        colorHex: String,
        iconName: String,
        targetWeight: Double = 0.0,
        minWeight: Double = 0.0,
        maxWeight: Double = 100.0,
        targetTolerance: Double = 0.0,
        description: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(
                AssetCategory(
                    name = name.trim(),
                    colorHex = colorHex,
                    iconName = iconName,
                    targetWeight = targetWeight,
                    minWeight = minWeight,
                    maxWeight = maxWeight,
                    targetTolerance = targetTolerance,
                    description = description.trim()
                )
            )
        }
    }

    fun updateCategory(category: AssetCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: AssetCategory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }

    fun normalizeTargetWeights() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.normalizeTargetWeights(allAssetsForActivePortfolio.value)
        }
    }

    fun recordCurrentSnapshot(note: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val summary = portfolioSummary.value
            repository.recordSnapshot(activePortfolioId.value, summary.totalValue, note.ifBlank { "ثبت دستی" })
        }
    }

    fun deleteSnapshot(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSnapshot(id)
        }
    }

    fun simulateCashInjection(freshCash: Double): CashInjectionResult {
        val currentSummary = portfolioSummary.value
        val liquidSummaryValue = currentSummary.liquidTotalValue
        val newLiquidTotal = liquidSummaryValue + freshCash
        val categoryMap = allCategories.value.associateBy { it.id }

        if (freshCash <= 0 || newLiquidTotal <= 0) {
            return CashInjectionResult(0.0, currentSummary.totalValue, emptyList())
        }

        val liquidAssets = currentSummary.calculatedAssets.filter { !it.asset.isFullyFrozen }
        val deficits = liquidAssets.map { calc ->
            val newTargetVal = newLiquidTotal * (calc.asset.targetWeight / 100.0)
            val deficit = Math.max(0.0, newTargetVal - calc.liquidValue)
            Pair(calc, deficit)
        }

        val totalDeficit = deficits.sumOf { it.second }

        val allocations = deficits.map { (calc, deficit) ->
            val allocatedCash = if (totalDeficit > 0) {
                (deficit / totalDeficit) * freshCash
            } else {
                freshCash * (calc.asset.targetWeight / 100.0)
            }
            val unitsToBuy = if (calc.asset.unitPrice > 0) allocatedCash / calc.asset.unitPrice else 0.0
            val projectedLiquidVal = calc.liquidValue + allocatedCash
            val projectedVal = calc.currentValue + allocatedCash
            val projectedWeight = if (newLiquidTotal > 0) (projectedLiquidVal / newLiquidTotal) * 100.0 else 0.0

            CashInjectionSimulationItem(
                asset = calc.asset,
                category = categoryMap[calc.asset.categoryId],
                currentHoldings = calc.asset.quantity,
                currentValue = calc.currentValue,
                currentWeight = calc.currentWeight,
                newInvestAmount = allocatedCash,
                newUnitsToBuy = unitsToBuy,
                projectedValue = projectedVal,
                projectedWeight = projectedWeight,
                targetWeight = calc.asset.targetWeight
            )
        }

        return CashInjectionResult(
            injectionAmount = freshCash,
            newTotalPortfolioValue = currentSummary.totalValue + freshCash,
            simulatedAllocations = allocations
        )
    }

    fun applyCashInjection(injections: List<Pair<Int, Double>>) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentAssets = allAssetsForActivePortfolio.value.associateBy { it.id }
            injections.forEach { (assetId, additionalUnits) ->
                if (additionalUnits > 0) {
                    val asset = currentAssets[assetId]
                    if (asset != null) {
                        repository.updateAsset(asset.copy(quantity = asset.quantity + additionalUnits))
                    }
                }
            }
            recordCurrentSnapshot("تزریق نقدینگی")
        }
    }

    fun resetSettingsToDefaults() {
        prefsManager.resetSettingsToDefaults()
        appLanguage.value = prefsManager.getLanguage()
        themeMode.value = prefsManager.getThemeMode()
        currency.value = prefsManager.getCurrency()
        usePersianDigits.value = prefsManager.getUsePersianDigits()
        tolerancePercent.value = prefsManager.getTolerance()
        isPrivacyMode.value = prefsManager.getPrivacyMode()
    }

    fun resetHoldingsToSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(getApplication(), viewModelScope)
            repository.clearAllAssets()
            repository.clearAllSnapshots()
            repository.clearAllCategories()
            AppDatabase.populateInitialData(db)
        }
    }

    fun restoreBackup(payload: PortfolioBackupPayload) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restoreData(
                portfolios = payload.portfolios,
                categories = payload.categories,
                assets = payload.assets,
                snapshots = payload.snapshots
            )
            if (payload.currency.isNotBlank()) {
                setCurrency(payload.currency)
            }
            if (payload.tolerancePercent > 0.0) {
                setTolerance(payload.tolerancePercent)
            }
        }
    }
}
