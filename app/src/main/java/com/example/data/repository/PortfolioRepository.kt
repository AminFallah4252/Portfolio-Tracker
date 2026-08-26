package com.example.data.repository

import com.example.data.local.AssetDao
import com.example.data.local.CategoryDao
import com.example.data.local.SnapshotDao
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.data.model.PortfolioSnapshot
import kotlinx.coroutines.flow.Flow

class PortfolioRepository(
    private val assetDao: AssetDao,
    private val categoryDao: CategoryDao,
    private val snapshotDao: SnapshotDao
) {
    val allAssets: Flow<List<AssetItem>> = assetDao.getAllAssets()
    val allCategories: Flow<List<AssetCategory>> = categoryDao.getAllCategories()
    val allSnapshots: Flow<List<PortfolioSnapshot>> = snapshotDao.getAllSnapshots()

    suspend fun insertAsset(asset: AssetItem): Long = assetDao.insertAsset(asset)

    suspend fun updateAsset(asset: AssetItem) = assetDao.updateAsset(asset)

    suspend fun deleteAsset(asset: AssetItem) = assetDao.deleteAsset(asset)

    suspend fun deleteAssetById(id: Int) = assetDao.deleteAssetById(id)

    suspend fun clearAllAssets() = assetDao.clearAssets()

    suspend fun clearAllSnapshots() = snapshotDao.clearSnapshots()

    suspend fun insertCategory(category: AssetCategory): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: AssetCategory) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: AssetCategory) {
        assetDao.deleteAssetsByCategoryId(category.id)
        categoryDao.deleteCategory(category)
    }

    suspend fun recordSnapshot(totalValue: Double, note: String = ""): Long {
        return snapshotDao.insertSnapshot(
            PortfolioSnapshot(
                timestamp = System.currentTimeMillis(),
                totalValue = totalValue,
                note = note
            )
        )
    }

    suspend fun deleteSnapshot(id: Int) = snapshotDao.deleteSnapshot(id)

    suspend fun normalizeTargetWeights(assets: List<AssetItem>) {
        val totalWeight = assets.sumOf { it.targetWeight }
        if (totalWeight > 0) {
            val updated = assets.map { asset ->
                val normalizedWeight = (asset.targetWeight / totalWeight) * 100.0
                asset.copy(targetWeight = Math.round(normalizedWeight * 10.0) / 10.0)
            }
            assetDao.insertAssets(updated)
        }
    }
}
