package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val name: String,
    val symbol: String = "",
    val categoryId: Int,
    val quantity: Double,
    val unitPrice: Double,
    val targetWeight: Double, // in percentage e.g. 15.0 for 15%
    val notes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_snapshots")
data class PortfolioSnapshot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val totalValue: Double,
    val note: String = ""
)
