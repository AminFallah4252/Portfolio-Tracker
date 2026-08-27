package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.PortfolioRepository
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.AppThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption {
    VALUE_DESC,
    VALUE_ASC,
    WEIGHT_DESC,
    REBALANCE_URGENCY,
    NAME_ASC;

    fun getTitle(strings: com.example.util.Strings): String = when (this) {
        VALUE_DESC -> strings.sortValueDesc
        VALUE_ASC -> strings.sortValueAsc
        WEIGHT_DESC -> strings.sortWeightDesc
        REBALANCE_URGENCY -> strings.sortUrgency
        NAME_ASC -> strings.sortNameAsc
    }
}

class PortfolioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PortfolioRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = PortfolioRepository(
            database.assetDao(),
            database.categoryDao(),
            database.snapshotDao()
        )
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.populateInitialData(database)
        }
    }

    val allCategories: StateFlow<List<AssetCategory>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssets: StateFlow<List<AssetItem>> = repository.allAssets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSnapshots: StateFlow<List<PortfolioSnapshot>> = repository.allSnapshots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appLanguage = MutableStateFlow(AppLanguage.PERSIAN)
    val themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<Int?>(null) // null = all
    val selectedActionFilter = MutableStateFlow<RebalanceActionType?>(null) // null = all
    val sortOption = MutableStateFlow(SortOption.VALUE_DESC)
    val currency = MutableStateFlow("تومان")
    val usePersianDigits = MutableStateFlow(false)
    val tolerancePercent = MutableStateFlow(0.2) // 0.2% tolerance
    val isPrivacyMode = MutableStateFlow(false)

    fun togglePrivacyMode() {
        isPrivacyMode.value = !isPrivacyMode.value
    }

    fun toggleLanguage() {
        if (appLanguage.value == AppLanguage.PERSIAN) {
            appLanguage.value = AppLanguage.ENGLISH
            if (currency.value == "تومان" || currency.value == "ریال") {
                // Keep currency or leave as is
            }
        } else {
            appLanguage.value = AppLanguage.PERSIAN
        }
    }

    fun toggleTheme() {
        themeMode.value = when (themeMode.value) {
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
        }
    }

    // Combined Reactive Portfolio Calculations
    val portfolioSummary: StateFlow<PortfolioSummary> = combine(
        repository.allAssets,
        repository.allCategories,
        tolerancePercent
    ) { assets, categories, tolerance ->
        calculatePortfolio(assets, categories, tolerance)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PortfolioSummary(
            totalValue = 0.0,
            totalTargetWeight = 0.0,
            isTargetWeightValid = true,
            totalBuyAmount = 0.0,
            totalSellAmount = 0.0,
            balancedCount = 0,
            buyCount = 0,
            sellCount = 0,
            topAsset = null,
            calculatedAssets = emptyList(),
            categorySummaries = emptyList()
        )
    )

    // Filtered & Sorted Assets
    val filteredCalculatedAssets: StateFlow<List<CalculatedAsset>> = combine(
        portfolioSummary,
        searchQuery,
        selectedCategoryFilter,
        selectedActionFilter,
        sortOption
    ) { summary, query, catFilter, actionFilter, sort ->
        var list = summary.calculatedAssets

        if (query.isNotBlank()) {
            list = list.filter {
                it.asset.name.contains(query, ignoreCase = true) ||
                it.asset.symbol.contains(query, ignoreCase = true) ||
                (it.category?.name?.contains(query, ignoreCase = true) ?: false)
            }
        }

        if (catFilter != null) {
            list = list.filter { it.asset.categoryId == catFilter }
        }

        if (actionFilter != null) {
            list = list.filter { it.actionType == actionFilter }
        }

        when (sort) {
            SortOption.VALUE_DESC -> list.sortedByDescending { it.currentValue }
            SortOption.VALUE_ASC -> list.sortedBy { it.currentValue }
            SortOption.WEIGHT_DESC -> list.sortedByDescending { it.currentWeight }
            SortOption.REBALANCE_URGENCY -> list.sortedByDescending { Math.abs(it.rebalanceAmount) }
            SortOption.NAME_ASC -> list.sortedBy { it.asset.name }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun calculatePortfolio(
        assets: List<AssetItem>,
        categories: List<AssetCategory>,
        tolerance: Double
    ): PortfolioSummary {
        val categoryMap = categories.associateBy { it.id }
        val totalValue = assets.sumOf { it.quantity * it.unitPrice }
        val totalTargetWeight = assets.sumOf { it.targetWeight }
        val isValidWeight = totalTargetWeight in 99.5..100.5

        val calculatedAssets = assets.map { asset ->
            val currentValue = asset.quantity * asset.unitPrice
            val currentWeight = if (totalValue > 0) (currentValue / totalValue) * 100.0 else 0.0
            val targetValue = if (totalValue > 0) totalValue * (asset.targetWeight / 100.0) else 0.0
            val rebalanceAmount = targetValue - currentValue
            val rebalanceUnits = if (asset.unitPrice > 0) rebalanceAmount / asset.unitPrice else 0.0
            val deviation = currentWeight - asset.targetWeight

            val actionType = when {
                Math.abs(deviation) <= tolerance -> RebalanceActionType.BALANCED
                rebalanceAmount > 0 -> RebalanceActionType.BUY
                else -> RebalanceActionType.SELL
            }

            CalculatedAsset(
                asset = asset,
                category = categoryMap[asset.categoryId],
                currentValue = currentValue,
                currentWeight = currentWeight,
                targetValue = targetValue,
                rebalanceAmount = rebalanceAmount,
                rebalanceUnits = rebalanceUnits,
                actionType = actionType,
                weightDeviation = deviation
            )
        }

        val totalBuy = calculatedAssets
            .filter { it.actionType == RebalanceActionType.BUY }
            .sumOf { it.rebalanceAmount }

        val totalSell = calculatedAssets
            .filter { it.actionType == RebalanceActionType.SELL }
            .sumOf { Math.abs(it.rebalanceAmount) }

        val balancedCount = calculatedAssets.count { it.actionType == RebalanceActionType.BALANCED }
        val buyCount = calculatedAssets.count { it.actionType == RebalanceActionType.BUY }
        val sellCount = calculatedAssets.count { it.actionType == RebalanceActionType.SELL }
        val topAsset = calculatedAssets.maxByOrNull { it.currentValue }

        // Category summaries
        val categorySummaries = categories.map { cat ->
            val catAssets = calculatedAssets.filter { it.asset.categoryId == cat.id }
            val catTotal = catAssets.sumOf { it.currentValue }
            val catWeight = if (totalValue > 0) (catTotal / totalValue) * 100.0 else 0.0
            val catTargetWeight = catAssets.sumOf { it.asset.targetWeight }

            CategorySummary(
                category = cat,
                totalValue = catTotal,
                currentWeight = catWeight,
                targetWeight = catTargetWeight,
                assetCount = catAssets.size
            )
        }.filter { it.totalValue > 0 || it.targetWeight > 0 }

        return PortfolioSummary(
            totalValue = totalValue,
            totalTargetWeight = totalTargetWeight,
            isTargetWeightValid = isValidWeight,
            totalBuyAmount = totalBuy,
            totalSellAmount = totalSell,
            balancedCount = balancedCount,
            buyCount = buyCount,
            sellCount = sellCount,
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
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAsset(
                AssetItem(
                    name = name.trim(),
                    symbol = symbol.trim().uppercase(),
                    categoryId = categoryId,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    targetWeight = targetWeight,
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
        notes: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAsset(
                AssetItem(
                    id = id,
                    name = name.trim(),
                    symbol = symbol.trim().uppercase(),
                    categoryId = categoryId,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    targetWeight = targetWeight,
                    notes = notes.trim(),
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun quickUpdateHolding(id: Int, newQuantity: Double, newUnitPrice: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val asset = allAssets.value.find { it.id == id } ?: return@launch
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
            repository.normalizeTargetWeights(allAssets.value)
        }
    }

    fun recordCurrentSnapshot(note: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val summary = portfolioSummary.value
            repository.recordSnapshot(summary.totalValue, note.ifBlank { "ثبت دستی" })
        }
    }

    fun deleteSnapshot(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSnapshot(id)
        }
    }

    // Smart Cash Injection Simulator
    // Calculates how to allocate fresh cash into underweight assets to approach target balance without selling
    fun simulateCashInjection(freshCash: Double): CashInjectionResult {
        val currentSummary = portfolioSummary.value
        val newTotal = currentSummary.totalValue + freshCash
        val categoryMap = allCategories.value.associateBy { it.id }

        if (freshCash <= 0 || newTotal <= 0) {
            return CashInjectionResult(0.0, currentSummary.totalValue, emptyList())
        }

        // Ideal target value for each asset in the new portfolio: newTarget = newTotal * (targetWeight / 100)
        // Deficit = max(0, newTarget - currentValue)
        val deficits = currentSummary.calculatedAssets.map { calc ->
            val newTargetVal = newTotal * (calc.asset.targetWeight / 100.0)
            val deficit = Math.max(0.0, newTargetVal - calc.currentValue)
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
            val projectedVal = calc.currentValue + allocatedCash
            val projectedWeight = if (newTotal > 0) (projectedVal / newTotal) * 100.0 else 0.0

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
            newTotalPortfolioValue = newTotal,
            simulatedAllocations = allocations
        )
    }

    // Apply cash injection: updates each asset's quantity with the bought units
    fun applyCashInjection(injections: List<Pair<Int, Double>>) { // list of (assetId, additionalUnits)
        viewModelScope.launch(Dispatchers.IO) {
            val currentAssets = allAssets.value.associateBy { it.id }
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

    fun clearAllAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllAssets()
            repository.clearAllSnapshots()
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(getApplication(), viewModelScope)
            repository.clearAllAssets()
            repository.clearAllSnapshots()
            AppDatabase.populateInitialData(db)
        }
    }

    fun restoreBackup(payload: com.example.util.PortfolioBackupPayload) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.restoreData(
                categories = payload.categories,
                assets = payload.assets,
                snapshots = payload.snapshots
            )
            if (payload.currency.isNotBlank()) {
                currency.value = payload.currency
            }
            if (payload.tolerancePercent > 0.0) {
                tolerancePercent.value = payload.tolerancePercent
            }
        }
    }
}
