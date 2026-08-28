package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.data.model.PortfolioProfile
import com.example.data.model.PortfolioSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PortfolioProfile::class, AssetCategory::class, AssetItem::class, PortfolioSnapshot::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioProfileDao(): PortfolioProfileDao
    abstract fun assetDao(): AssetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun snapshotDao(): SnapshotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "portfolio_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val profileDao = database.portfolioProfileDao()
            val categoryDao = database.categoryDao()

            if (profileDao.getPortfolioCount() == 0) {
                val defaultPortfolio = PortfolioProfile(
                    id = 1,
                    name = "سبد اصلی سرمایه‌گذاری",
                    description = "",
                    isDefault = true,
                    colorHex = "#3B82F6"
                )
                profileDao.insertPortfolios(listOf(defaultPortfolio))
            }

            if (categoryDao.getCategoryCount() == 0) {
                val catGold = AssetCategory(
                    id = 1,
                    name = "طلا و فلزات",
                    colorHex = "#F59E0B",
                    iconName = "gold",
                    targetWeight = 0.0,
                    minWeight = 0.0,
                    maxWeight = 100.0,
                    targetTolerance = 0.0,
                    description = "",
                    sortOrder = 1
                )
                val catFixed = AssetCategory(
                    id = 2,
                    name = "درآمد ثابت و صندوق‌ها",
                    colorHex = "#3B82F6",
                    iconName = "shield",
                    targetWeight = 0.0,
                    minWeight = 0.0,
                    maxWeight = 100.0,
                    targetTolerance = 0.0,
                    description = "",
                    sortOrder = 2
                )
                val catStock = AssetCategory(
                    id = 3,
                    name = "سهام و بورس",
                    colorHex = "#8B5CF6",
                    iconName = "trending_up",
                    targetWeight = 0.0,
                    minWeight = 0.0,
                    maxWeight = 100.0,
                    targetTolerance = 0.0,
                    description = "",
                    sortOrder = 3
                )
                val catFx = AssetCategory(
                    id = 4,
                    name = "نقدینگی و ارز",
                    colorHex = "#10B981",
                    iconName = "currency_exchange",
                    targetWeight = 0.0,
                    minWeight = 0.0,
                    maxWeight = 100.0,
                    targetTolerance = 0.0,
                    description = "",
                    sortOrder = 4
                )
                val catCrypto = AssetCategory(
                    id = 5,
                    name = "ارز دیجیتال (کریپتو)",
                    colorHex = "#EC4899",
                    iconName = "currency_bitcoin",
                    targetWeight = 0.0,
                    minWeight = 0.0,
                    maxWeight = 100.0,
                    targetTolerance = 0.0,
                    description = "",
                    sortOrder = 5
                )
                val catRealEstate = AssetCategory(
                    id = 6,
                    name = "املاک و دارایی منجمد",
                    colorHex = "#0EA5E9",
                    iconName = "home",
                    targetWeight = 0.0,
                    minWeight = 0.0,
                    maxWeight = 100.0,
                    targetTolerance = 0.0,
                    description = "",
                    sortOrder = 6
                )

                categoryDao.insertCategories(listOf(catGold, catFixed, catStock, catFx, catCrypto, catRealEstate))
            }
        }
    }
}
