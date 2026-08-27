package com.example.data.local

import androidx.room.*
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.data.model.PortfolioSnapshot
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY id ASC")
    fun getAllAssets(): Flow<List<AssetItem>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Int): AssetItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssets(assets: List<AssetItem>)

    @Update
    suspend fun updateAsset(asset: AssetItem)

    @Delete
    suspend fun deleteAsset(asset: AssetItem)

    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun deleteAssetById(id: Int)

    @Query("DELETE FROM assets WHERE categoryId = :categoryId")
    suspend fun deleteAssetsByCategoryId(categoryId: Int)

    @Query("DELETE FROM assets")
    suspend fun clearAssets()

    @Query("SELECT COUNT(*) FROM assets")
    suspend fun getAssetCount(): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM asset_categories ORDER BY sortOrder ASC, id ASC")
    fun getAllCategories(): Flow<List<AssetCategory>>

    @Query("SELECT * FROM asset_categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): AssetCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: AssetCategory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<AssetCategory>)

    @Update
    suspend fun updateCategory(category: AssetCategory)

    @Delete
    suspend fun deleteCategory(category: AssetCategory)

    @Query("DELETE FROM asset_categories")
    suspend fun clearCategories()

    @Query("SELECT COUNT(*) FROM asset_categories")
    suspend fun getCategoryCount(): Int
}

@Dao
interface SnapshotDao {
    @Query("SELECT * FROM portfolio_snapshots ORDER BY timestamp ASC")
    fun getAllSnapshots(): Flow<List<PortfolioSnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: PortfolioSnapshot): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshots(snapshots: List<PortfolioSnapshot>)

    @Query("DELETE FROM portfolio_snapshots WHERE id = :id")
    suspend fun deleteSnapshot(id: Int)

    @Query("DELETE FROM portfolio_snapshots")
    suspend fun clearSnapshots()
}
