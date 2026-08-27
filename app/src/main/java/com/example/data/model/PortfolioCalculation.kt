package com.example.data.model

enum class RebalanceActionType {
    BUY,       // 🟢 خرید (کسری وزن)
    SELL,      // 🔴 فروش (سیو سود)
    BALANCED,  // ⚪ بالانس
    FROZEN     // ❄️ منجمد / غیرقابل معامله (فقط در ارزش کل لحاظ می‌شود)
}

data class CalculatedAsset(
    val asset: AssetItem,
    val category: AssetCategory?,
    val currentValue: Double,
    val currentWeight: Double,      // in percentage of total net worth 0..100
    val targetValue: Double,
    val rebalanceAmount: Double,    // targetLiquidValue - currentLiquidValue (positive = Buy, negative = Sell, 0 for 100% frozen)
    val rebalanceUnits: Double,     // rebalanceAmount / unitPrice
    val actionType: RebalanceActionType,
    val weightDeviation: Double,    // currentLiquidWeight - targetWeight
    val liquidValue: Double = currentValue * (asset.effectiveLiquidPercent / 100.0),
    val frozenValue: Double = currentValue * (asset.effectiveFrozenPercent / 100.0),
    val targetLiquidValue: Double = 0.0,
    val liquidWeight: Double = 0.0
)

data class CategorySummary(
    val category: AssetCategory,
    val totalValue: Double,
    val currentWeight: Double,
    val targetWeight: Double,
    val assetCount: Int,
    val frozenValue: Double = 0.0
)

data class PortfolioSummary(
    val totalValue: Double,            // Total Net Worth (including frozen assets)
    val liquidTotalValue: Double,      // Liquid / Exchangeable value for rebalancing
    val frozenTotalValue: Double,      // Illiquid / Frozen value
    val totalTargetWeight: Double,
    val isTargetWeightValid: Boolean,  // totalTargetWeight is approximately 100% (99.5..100.5)
    val totalBuyAmount: Double,
    val totalSellAmount: Double,
    val balancedCount: Int,
    val buyCount: Int,
    val sellCount: Int,
    val frozenCount: Int,
    val healthScore: Int,              // 0..100% Balance Health score
    val topAsset: CalculatedAsset?,
    val calculatedAssets: List<CalculatedAsset>,
    val categorySummaries: List<CategorySummary>
) {
    val frozenAssetsCount: Int get() = frozenCount
    val frozenAssetsValue: Double get() = frozenTotalValue
}

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
