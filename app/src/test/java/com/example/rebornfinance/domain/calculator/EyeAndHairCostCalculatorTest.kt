package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.EyePurchaseLot
import com.example.rebornfinance.domain.model.HairPurchaseLot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EyeAndHairCostCalculatorTest {

    @Test
    fun testAllocateEyeConsumptionFIFO() {
        val lot = EyePurchaseLot(
            id = 1L,
            eyeMaterialId = 10L,
            purchasedQuantity = 5L, // 5 pairs
            remainingQuantity = 5L,
            paidAmountCents = 1500L, // 15.00 € (3.00 €/pair)
            purchaseDate = 1000L,
            createdAt = 1000L
        )

        val result = EyeAndHairCostCalculator.allocateEyeConsumptionFIFO(2L, listOf(lot))

        assertTrue(result.isSuccessful)
        assertEquals(600L, result.totalCostCents) // 2 pairs * 3.00 € = 6.00 € (600 cents)
        assertEquals(1, result.allocations.size)
        assertEquals(2L, result.allocations[0].consumedQuantity)
    }

    @Test
    fun testAllocateHairConsumptionFIFO() {
        val lot = HairPurchaseLot(
            id = 1L,
            hairMaterialId = 20L,
            purchasedQuantityGrams = 10000L, // 10.000 g (10g)
            remainingQuantityGrams = 10000L,
            paidAmountCents = 2000L, // 20.00 € (2.00 €/g)
            purchaseDate = 1000L,
            createdAt = 1000L
        )

        // Consume 3000 (3.000 g)
        val result = EyeAndHairCostCalculator.allocateHairConsumptionFIFO(3000L, listOf(lot))

        assertTrue(result.isSuccessful)
        assertEquals(600L, result.totalCostCents) // 3g * 2.00 € = 6.00 € (600 cents)
        assertEquals(1, result.allocations.size)
        assertEquals(3000L, result.allocations[0].consumedQuantityGrams)
    }
}
