package com.example.data.model

enum class RebalanceActionType {
    BUY,       // 🟢 خرید (کسری وزن)
    SELL,      // 🔴 فروش (سیو سود)
    BALANCED   // ⚪ بالانس
}

data class CalculatedAsset(
    val asset: AssetItem,
    val category: AssetCategory?,
    val currentValue: Double,
    val currentWeight: Double,      // in percentage 0..100
    val targetValue: Double,
    val rebalanceAmount: Double,    // targetValue - currentValue (positive = Buy, negative = Sell)
    val rebalanceUnits: Double,     // rebalanceAmount / unitPrice
    val actionType: RebalanceActionType,
    val weightDeviation: Double     // currentWeight - targetWeight
)

data class CategorySummary(
    val category: AssetCategory,
    val totalValue: Double,
    val currentWeight: Double,
    val targetWeight: Double,
    val assetCount: Int
)

data class PortfolioSummary(
    val totalValue: Double,
    val totalTargetWeight: Double,
    val isTargetWeightValid: Boolean, // totalTargetWeight is approximately 100% (99.5..100.5)
    val totalBuyAmount: Double,
    val totalSellAmount: Double,
    val balancedCount: Int,
    val buyCount: Int,
    val sellCount: Int,
    val topAsset: CalculatedAsset?,
    val calculatedAssets: List<CalculatedAsset>,
    val categorySummaries: List<CategorySummary>
)

data class CashInjectionSimulationItem(
    val asset: AssetItem,
    val category: AssetCategory?,
    val currentHoldings: Double,
    val currentValue: Double,
    val currentWeight: Double,
    val newInvestAmount: Double,
    val newUnitsToBuy: Double,
    val projectedValue: Double,
    val projectedWeight: Double,
    val targetWeight: Double
)

data class CashInjectionResult(
    val injectionAmount: Double,
    val newTotalPortfolioValue: Double,
    val simulatedAllocations: List<CashInjectionSimulationItem>
)
