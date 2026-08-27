package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "portfolios")
data class PortfolioProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val isDefault: Boolean = false,
    val colorHex: String = "#3B82F6",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "asset_categories")
data class AssetCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String = "#3B82F6",
    val iconName: String = "category",
    val targetWeight: Double = 0.0,
    val minWeight: Double = 0.0,
    val maxWeight: Double = 100.0,
    val targetTolerance: Double = 0.0,
    val description: String = "",
    val sortOrder: Int = 0
)

@Entity(tableName = "assets")
data class AssetItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val portfolioId: Int = 1,
    val name: String,
    val symbol: String = "",
    val categoryId: Int,
    val quantity: Double,
    val unitPrice: Double,
    val targetWeight: Double, // in percentage e.g. 15.0 for 15% (applies to released/liquid portion)
    val isFrozen: Boolean = false, // ❄️ Frozen/locked asset flag
    val frozenPercentage: Double = 100.0, // 0.0 .. 100.0 percentage of this asset that is frozen. Default 100.0 if frozen.
    val notes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val effectiveFrozenPercent: Double
        get() = if (isFrozen) frozenPercentage.coerceIn(0.0, 100.0) else 0.0

    val effectiveLiquidPercent: Double
        get() = (100.0 - effectiveFrozenPercent).coerceIn(0.0, 100.0)

    val isFullyFrozen: Boolean
        get() = isFrozen && effectiveFrozenPercent >= 100.0

    val isPartiallyFrozen: Boolean
        get() = isFrozen && effectiveFrozenPercent in 0.001..99.999

    val frozenQuantity: Double
        get() = quantity * (effectiveFrozenPercent / 100.0)

    val liquidQuantity: Double
        get() = quantity * (effectiveLiquidPercent / 100.0)
}

@Entity(tableName = "portfolio_snapshots")
data class PortfolioSnapshot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val portfolioId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val totalValue: Double,
    val note: String = ""
)
