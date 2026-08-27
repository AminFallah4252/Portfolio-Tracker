package com.example.util

import com.example.data.model.AssetCategory
import com.example.data.model.AssetItem
import com.example.data.model.PortfolioSnapshot
import org.json.JSONArray
import org.json.JSONObject

data class PortfolioBackupPayload(
    val version: Int = 1,
    val exportDate: Long = System.currentTimeMillis(),
    val currency: String = "تومان",
    val tolerancePercent: Double = 0.5,
    val categories: List<AssetCategory> = emptyList(),
    val assets: List<AssetItem> = emptyList(),
    val snapshots: List<PortfolioSnapshot> = emptyList()
)

object DataBackupHelper {

    fun exportToJson(
        categories: List<AssetCategory>,
        assets: List<AssetItem>,
        snapshots: List<PortfolioSnapshot>,
        currency: String,
        tolerancePercent: Double
    ): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportDate", System.currentTimeMillis())
        root.put("currency", currency)
        root.put("tolerancePercent", tolerancePercent)

        // Categories
        val catArray = JSONArray()
        for (cat in categories) {
            val obj = JSONObject()
            obj.put("id", cat.id)
            obj.put("name", cat.name)
            obj.put("colorHex", cat.colorHex)
            obj.put("iconName", cat.iconName)
            obj.put("targetWeight", cat.targetWeight)
            obj.put("minWeight", cat.minWeight)
            obj.put("maxWeight", cat.maxWeight)
            obj.put("targetTolerance", cat.targetTolerance)
            obj.put("description", cat.description)
            obj.put("sortOrder", cat.sortOrder)
            catArray.put(obj)
        }
        root.put("categories", catArray)

        // Assets
        val assetArray = JSONArray()
        for (asset in assets) {
            val obj = JSONObject()
            obj.put("id", asset.id)
            obj.put("name", asset.name)
            obj.put("symbol", asset.symbol)
            obj.put("categoryId", asset.categoryId)
            obj.put("quantity", asset.quantity)
            obj.put("unitPrice", asset.unitPrice)
            obj.put("targetWeight", asset.targetWeight)
            obj.put("notes", asset.notes)
            obj.put("lastUpdated", asset.lastUpdated)
            assetArray.put(obj)
        }
        root.put("assets", assetArray)

        // Snapshots
        val snapArray = JSONArray()
        for (snap in snapshots) {
            val obj = JSONObject()
            obj.put("id", snap.id)
            obj.put("timestamp", snap.timestamp)
            obj.put("totalValue", snap.totalValue)
            obj.put("note", snap.note)
            snapArray.put(obj)
        }
        root.put("snapshots", snapArray)

        return root.toString(2)
    }

    fun parseFromJson(jsonString: String): Result<PortfolioBackupPayload> {
        return try {
            val root = JSONObject(jsonString)
            val version = root.optInt("version", 1)
            val exportDate = root.optLong("exportDate", System.currentTimeMillis())
            val currency = root.optString("currency", "تومان")
            val tolerance = root.optDouble("tolerancePercent", 0.5)

            val categories = mutableListOf<AssetCategory>()
            val catArray = root.optJSONArray("categories")
            if (catArray != null) {
                for (i in 0 until catArray.length()) {
                    val obj = catArray.getJSONObject(i)
                    categories.add(
                        AssetCategory(
                            id = obj.optInt("id", 0),
                            name = obj.optString("name", "دسته‌بندی"),
                            colorHex = obj.optString("colorHex", "#3B82F6"),
                            iconName = obj.optString("iconName", "category"),
                            targetWeight = obj.optDouble("targetWeight", 0.0),
                            minWeight = obj.optDouble("minWeight", 0.0),
                            maxWeight = obj.optDouble("maxWeight", 100.0),
                            targetTolerance = obj.optDouble("targetTolerance", 0.0),
                            description = obj.optString("description", ""),
                            sortOrder = obj.optInt("sortOrder", i)
                        )
                    )
                }
            }

            val assets = mutableListOf<AssetItem>()
            val assetArray = root.optJSONArray("assets")
            if (assetArray != null) {
                for (i in 0 until assetArray.length()) {
                    val obj = assetArray.getJSONObject(i)
                    assets.add(
                        AssetItem(
                            id = obj.optInt("id", 0),
                            name = obj.optString("name", "دارایی"),
                            symbol = obj.optString("symbol", ""),
                            categoryId = obj.optInt("categoryId", 1),
                            quantity = obj.optDouble("quantity", 0.0),
                            unitPrice = obj.optDouble("unitPrice", 0.0),
                            targetWeight = obj.optDouble("targetWeight", 0.0),
                            notes = obj.optString("notes", ""),
                            lastUpdated = obj.optLong("lastUpdated", System.currentTimeMillis())
                        )
                    )
                }
            }

            val snapshots = mutableListOf<PortfolioSnapshot>()
            val snapArray = root.optJSONArray("snapshots")
            if (snapArray != null) {
                for (i in 0 until snapArray.length()) {
                    val obj = snapArray.getJSONObject(i)
                    snapshots.add(
                        PortfolioSnapshot(
                            id = obj.optInt("id", 0),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            totalValue = obj.optDouble("totalValue", 0.0),
                            note = obj.optString("note", "")
                        )
                    )
                }
            }

            if (categories.isEmpty() && assets.isEmpty()) {
                Result.failure(IllegalArgumentException("فایل پشتیبان فاقد اطلاعات دارایی یا دسته‌بندی است."))
            } else {
                Result.success(
                    PortfolioBackupPayload(
                        version = version,
                        exportDate = exportDate,
                        currency = currency,
                        tolerancePercent = tolerance,
                        categories = categories,
                        assets = assets,
                        snapshots = snapshots
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
