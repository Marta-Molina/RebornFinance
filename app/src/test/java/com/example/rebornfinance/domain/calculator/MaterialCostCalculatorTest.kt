package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.MaterialPurchaseLot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MaterialCostCalculatorTest {

    @Test
    fun testAllocateConsumptionFIFO_SingleLot() {
        val lot = MaterialPurchaseLot(
            id = 1L,
            materialId = 10L,
            purchasedQuantity = 10000L, // 10 ml
            remainingQuantity = 10000L,
            paidAmountCents = 2000L, // 20.00 €
            purchaseDate = 1000L,
            createdAt = 1000L
        )

        // Consume 3000 (3 ml)
        val result = MaterialCostCalculator.allocateConsumptionFIFO(3000L, listOf(lot))

        assertTrue(result.isSuccessful)
        assertEquals(600L, result.totalCostCents) // 3 ml * 2.00 €/ml = 6.00 € (600 cents)
        assertEquals(1, result.allocations.size)
        assertEquals(3000L, result.allocations[0].consumedQuantity)
    }

    @Test
    fun testAllocateConsumptionFIFO_MultipleLots() {
        val lot1 = MaterialPurchaseLot(
            id = 1L,
            materialId = 10L,
            purchasedQuantity = 5000L, // 5 ml
            remainingQuantity = 2000L, // only 2 ml left
            paidAmountCents = 1000L, // 10.00 € (5.00 €/ml? No, 1000 / 5 = 200 cents/ml)
            purchaseDate = 1000L,
            createdAt = 1000L
        )
        val lot2 = MaterialPurchaseLot(
            id = 2L,
            materialId = 10L,
            purchasedQuantity = 10000L, // 10 ml
            remainingQuantity = 10000L,
            paidAmountCents = 1500L, // 15.00 € (150 cents/ml)
            purchaseDate = 2000L,
            createdAt = 2000L
        )

        // Consume 4000 (4 ml): 2 ml from lot1 (at 200 cents/ml = 400 cents) + 2 ml from lot2 (at 150 cents/ml = 300 cents) = 700 cents
        val result = MaterialCostCalculator.allocateConsumptionFIFO(4000L, listOf(lot1, lot2))

        assertTrue(result.isSuccessful)
        assertEquals(700L, result.totalCostCents)
        assertEquals(2, result.allocations.size)
        assertEquals(2000L, result.allocations[0].consumedQuantity)
        assertEquals(2000L, result.allocations[1].consumedQuantity)
    }

    @Test
    fun testAllocateConsumptionFIFO_InsufficientStock() {
        val lot = MaterialPurchaseLot(
            id = 1L,
            materialId = 10L,
            purchasedQuantity = 5000L,
            remainingQuantity = 1000L, // 1 ml left
            paidAmountCents = 500L,
            purchaseDate = 1000L,
            createdAt = 1000L
        )

        // Request 3000 (3 ml) when only 1 ml available
        val result = MaterialCostCalculator.allocateConsumptionFIFO(3000L, listOf(lot))

        assertFalse(result.isSuccessful)
        assertTrue(result.errorMessage?.contains("Stock insuficiente") == true)
    }
}
