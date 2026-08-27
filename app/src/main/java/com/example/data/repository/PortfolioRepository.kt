package com.example.data.repository

import com.example.data.local.AssetDao
import com.example.data.local.CategoryDao
import com.example.data.local.PortfolioProfileDao
import com.example.data.local.SnapshotDao
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.data.model.PortfolioProfile
import com.example.data.model.PortfolioSnapshot
import kotlinx.coroutines.flow.Flow

class PortfolioRepository(
    private val portfolioProfileDao: PortfolioProfileDao,
    private val assetDao: AssetDao,
    private val categoryDao: CategoryDao,
    private val snapshotDao: SnapshotDao
) {
    val allPortfolios: Flow<List<PortfolioProfile>> = portfolioProfileDao.getAllPortfolios()
    val allCategories: Flow<List<AssetCategory>> = categoryDao.getAllCategories()
    val allAssets: Flow<List<AssetItem>> = assetDao.getAllAssets()
    val allSnapshots: Flow<List<PortfolioSnapshot>> = snapshotDao.getAllSnapshots()

    fun getAssetsForPortfolio(portfolioId: Int): Flow<List<AssetItem>> {
        return assetDao.getAssetsByPortfolio(portfolioId)
    }

    fun getSnapshotsForPortfolio(portfolioId: Int): Flow<List<PortfolioSnapshot>> {
        return snapshotDao.getSnapshotsByPortfolio(portfolioId)
    }

    suspend fun insertPortfolio(portfolio: PortfolioProfile): Long = portfolioProfileDao.insertPortfolio(portfolio)

    suspend fun updatePortfolio(portfolio: PortfolioProfile) = portfolioProfileDao.updatePortfolio(portfolio)

    suspend fun deletePortfolio(portfolioId: Int) {
        assetDao.deleteAssetsByPortfolioId(portfolioId)
        snapshotDao.deleteSnapshotsByPortfolioId(portfolioId)
        portfolioProfileDao.deletePortfolioById(portfolioId)
    }

    suspend fun insertAsset(asset: AssetItem): Long = assetDao.insertAsset(asset)

    suspend fun updateAsset(asset: AssetItem) = assetDao.updateAsset(asset)

    suspend fun deleteAsset(asset: AssetItem) = assetDao.deleteAsset(asset)

    suspend fun deleteAssetById(id: Int) = assetDao.deleteAssetById(id)

    suspend fun clearAllAssets() = assetDao.clearAssets()

    suspend fun clearAllCategories() = categoryDao.clearCategories()

    suspend fun clearAllSnapshots() = snapshotDao.clearSnapshots()

    suspend fun restoreData(
        portfolios: List<PortfolioProfile> = emptyList(),
        categories: List<AssetCategory>,
        assets: List<AssetItem>,
        snapshots: List<PortfolioSnapshot>
    ) {
        if (portfolios.isNotEmpty()) {
            portfolioProfileDao.clearPortfolios()
            portfolioProfileDao.insertPortfolios(portfolios)
        }
        if (categories.isNotEmpty()) {
            categoryDao.clearCategories()
            categoryDao.insertCategories(categories)
        }
        if (assets.isNotEmpty()) {
            assetDao.clearAssets()
            assetDao.insertAssets(assets)
        }
        if (snapshots.isNotEmpty()) {
            snapshotDao.clearSnapshots()
            snapshotDao.insertSnapshots(snapshots)
        }
    }

    suspend fun insertCategory(category: AssetCategory): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: AssetCategory) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: AssetCategory) {
        assetDao.deleteAssetsByCategoryId(category.id)
        categoryDao.deleteCategory(category)
    }

    suspend fun recordSnapshot(portfolioId: Int, totalValue: Double, note: String = ""): Long {
        return snapshotDao.insertSnapshot(
            PortfolioSnapshot(
                portfolioId = portfolioId,
                timestamp = System.currentTimeMillis(),
                totalValue = totalValue,
                note = note
            )
        )
    }

    suspend fun deleteSnapshot(id: Int) = snapshotDao.deleteSnapshot(id)

    suspend fun normalizeTargetWeights(assets: List<AssetItem>) {
        val tradeableAssets = assets.filter { !it.isFullyFrozen }
        val totalWeight = tradeableAssets.sumOf { it.targetWeight }
        if (totalWeight > 0) {
            val updated = assets.map { asset ->
                if (asset.isFullyFrozen) {
                    asset.copy(targetWeight = 0.0)
                } else {
                    val normalizedWeight = (asset.targetWeight / totalWeight) * 100.0
                    asset.copy(targetWeight = Math.round(normalizedWeight * 10.0) / 10.0)
                }
            }
            assetDao.insertAssets(updated)
        }
    }
}
