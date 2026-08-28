package com.example

import com.example.data.model.*
import com.example.util.*
import org.junit.Assert.*
import org.junit.Test

class PortfolioLogicTest {

    @Test
    fun testHelpTopicsAreCompleteAndLocalized() {
        val faStrings = AppStrings.get(AppLanguage.PERSIAN)
        val enStrings = AppStrings.get(AppLanguage.ENGLISH)

        assertEquals(9, faStrings.helpTopics.size)
        assertEquals(9, enStrings.helpTopics.size)

        val requiredTopicIds = listOf(
            "portfolios",
            "dashboard",
            "assets_frozen",
            "rebalance",
            "cash_injection",
            "categories",
            "analytics",
            "security",
            "backup"
        )

        for (id in requiredTopicIds) {
            val faTopic = faStrings.helpTopics.find { it.id == id }
            val enTopic = enStrings.helpTopics.find { it.id == id }

            assertNotNull("Persian topic $id should exist", faTopic)
            assertNotNull("English topic $id should exist", enTopic)

            assertTrue(faTopic!!.title.isNotBlank())
            assertTrue(faTopic.description.isNotBlank())
            assertTrue(faTopic.steps.isNotEmpty())

            assertTrue(enTopic!!.title.isNotBlank())
            assertTrue(enTopic.description.isNotBlank())
            assertTrue(enTopic.steps.isNotEmpty())
        }
    }

    @Test
    fun testCurrencyFormatting() {
        val formattedFa = CurrencyFormatter.formatCurrency(1500000.0, currency = "تومان", usePersianDigits = true)
        val formattedEn = CurrencyFormatter.formatCurrency(1500000.0, currency = "USD", usePersianDigits = false)

        assertTrue(formattedFa.contains("تومان"))
        assertTrue(formattedEn.contains("USD"))
    }

    @Test
    fun testSmartFloatFormatting() {
        assertEquals("125", CurrencyFormatter.formatSmartFloat(125.0))
        assertEquals("0.5", CurrencyFormatter.formatSmartFloat(0.5))
        assertEquals("0", CurrencyFormatter.formatSmartFloat(0.0))
    }

    @Test
    fun testAssetItemFrozenLogic() {
        val fullyFrozenAsset = AssetItem(
            id = 1,
            portfolioId = 1,
            categoryId = 1,
            name = "Real Estate",
            quantity = 1.0,
            unitPrice = 1000000.0,
            targetWeight = 0.0,
            isFrozen = true,
            frozenPercentage = 100.0
        )
        assertTrue(fullyFrozenAsset.isFullyFrozen)
        assertEquals(100.0, fullyFrozenAsset.effectiveFrozenPercent, 0.001)
        assertEquals(0.0, fullyFrozenAsset.effectiveLiquidPercent, 0.001)

        val partialFrozenAsset = AssetItem(
            id = 2,
            portfolioId = 1,
            categoryId = 1,
            name = "Staked Crypto",
            quantity = 10.0,
            unitPrice = 100.0,
            targetWeight = 20.0,
            isFrozen = true,
            frozenPercentage = 40.0
        )
        assertFalse(partialFrozenAsset.isFullyFrozen)
        assertEquals(40.0, partialFrozenAsset.effectiveFrozenPercent, 0.001)
        assertEquals(60.0, partialFrozenAsset.effectiveLiquidPercent, 0.001)
    }
}
