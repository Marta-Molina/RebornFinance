package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.ProjectCost
import com.example.rebornfinance.domain.model.ProjectMaterialConsumption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProjectCalculatorTest {

    @Test
    fun testCalculateTotalCost() {
        val costs = listOf(
            ProjectCost(id = 1, projectId = 1, concept = "Kit", amountCents = 7500L, date = 0L, createdAt = 0L),
            ProjectCost(id = 2, projectId = 1, concept = "Cuerpo", amountCents = 1250L, date = 0L, createdAt = 0L)
        )
        val consumptions = listOf(
            ProjectMaterialConsumption(id = 1, projectId = 1, category = "Imprimación", consumedQuantity = 1000L, unit = "ml", unitCostCents = 100L, assignedCostCents = 100L, date = 0L, createdAt = 0L),
            ProjectMaterialConsumption(id = 2, projectId = 1, category = "Pintura", consumedQuantity = 5000L, unit = "ml", unitCostCents = 100L, assignedCostCents = 500L, date = 0L, createdAt = 0L)
        )
        val total = ProjectCalculator.calculateTotalCost(costs, consumptions)
        assertEquals(9350L, total) // 7500 + 1250 + 100 + 500 = 9350
    }

    @Test
    fun testCalculateProfits() {
        val totalCost = 9350L
        val predicted = 35000L
        val actual = 32000L

        val estProfit = ProjectCalculator.calculateEstimatedProfit(predicted, totalCost)
        val saleProfit = ProjectCalculator.calculateSaleProfit(actual, totalCost)

        assertEquals(25650L, estProfit)
        assertEquals(22650L, saleProfit)

        assertNull(ProjectCalculator.calculateEstimatedProfit(null, totalCost))
        assertNull(ProjectCalculator.calculateSaleProfit(null, totalCost))
    }
}
