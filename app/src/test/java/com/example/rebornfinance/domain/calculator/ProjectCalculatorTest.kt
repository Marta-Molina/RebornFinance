package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.ProjectCost
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProjectCalculatorTest {

    @Test
    fun testCalculateTotalCost() {
        val costs = listOf(
            ProjectCost(id = 1, projectId = 1, concept = "Kit", amountCents = 7500L, date = 0L, createdAt = 0L),
            ProjectCost(id = 2, projectId = 1, concept = "Cuerpo", amountCents = 1250L, date = 0L, createdAt = 0L),
            ProjectCost(id = 3, projectId = 1, concept = "Envío", amountCents = 600L, date = 0L, createdAt = 0L)
        )
        val total = ProjectCalculator.calculateTotalCost(costs)
        assertEquals(9350L, total) // 93.50 €
    }

    @Test
    fun testCalculateProfits() {
        val totalCost = 9350L
        val predicted = 35000L // 350.00 €
        val actual = 32000L // 320.00 €

        val estProfit = ProjectCalculator.calculateEstimatedProfit(predicted, totalCost)
        val saleProfit = ProjectCalculator.calculateSaleProfit(actual, totalCost)

        assertEquals(25650L, estProfit) // 35000 - 9350 = 25650
        assertEquals(22650L, saleProfit) // 32000 - 9350 = 22650

        assertNull(ProjectCalculator.calculateEstimatedProfit(null, totalCost))
        assertNull(ProjectCalculator.calculateSaleProfit(null, totalCost))
    }
}
