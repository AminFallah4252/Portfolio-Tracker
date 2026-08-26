package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.data.model.PortfolioSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [AssetCategory::class, AssetItem::class, PortfolioSnapshot::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
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
            val categoryDao = database.categoryDao()
            val assetDao = database.assetDao()
            val snapshotDao = database.snapshotDao()

            // Always ensure assets and snapshots are clean
            assetDao.clearAssets()
            snapshotDao.clearSnapshots()

            if (categoryDao.getCategoryCount() > 0) return

            // Seed clean predefined Asset Classes with full risk & allocation settings
            val catGold = AssetCategory(
                id = 1,
                name = "طلا و فلزات",
                colorHex = "#F59E0B",
                iconName = "gold",
                targetWeight = 15.0,
                minWeight = 10.0,
                maxWeight = 25.0,
                targetTolerance = 0.0,
                description = "پوشش ریسک تورم و نوسانات ارزی",
                sortOrder = 1
            )
            val catFixed = AssetCategory(
                id = 2,
                name = "بورس (بدون ریسک)",
                colorHex = "#3B82F6",
                iconName = "shield",
                targetWeight = 15.0,
                minWeight = 10.0,
                maxWeight = 30.0,
                targetTolerance = 0.0,
                description = "سود روزشمار و جریان نقدی بدون ریسک",
                sortOrder = 2
            )
            val catStock = AssetCategory(
                id = 3,
                name = "بورس (سهام)",
                colorHex = "#8B5CF6",
                iconName = "trending_up",
                targetWeight = 19.0,
                minWeight = 10.0,
                maxWeight = 35.0,
                targetTolerance = 0.0,
                description = "رشد سرمایه میان‌مدت و بلندمدت در بورس",
                sortOrder = 3
            )
            val catFx = AssetCategory(
                id = 4,
                name = "نقدینگی ارزی",
                colorHex = "#10B981",
                iconName = "currency_exchange",
                targetWeight = 45.0,
                minWeight = 20.0,
                maxWeight = 60.0,
                targetTolerance = 0.0,
                description = "نقدینگی دلاری و حفظ قدرت خرید بین‌المللی",
                sortOrder = 4
            )
            val catFiat = AssetCategory(
                id = 5,
                name = "ریال و بانک",
                colorHex = "#64748B",
                iconName = "account_balance",
                targetWeight = 0.0,
                minWeight = 0.0,
                maxWeight = 10.0,
                targetTolerance = 0.0,
                description = "نقدینگی جاری برای فرصت‌های خرید",
                sortOrder = 5
            )
            val catCrypto = AssetCategory(
                id = 6,
                name = "کریپتوکارنسی",
                colorHex = "#EC4899",
                iconName = "currency_bitcoin",
                targetWeight = 6.0,
                minWeight = 0.0,
                maxWeight = 15.0,
                targetTolerance = 0.0,
                description = "دارایی‌های دیجیتال با بازدهی بالا",
                sortOrder = 6
            )

            categoryDao.insertCategories(listOf(catGold, catFixed, catStock, catFx, catFiat, catCrypto))
        }
    }
}
