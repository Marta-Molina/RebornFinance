package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.ProjectCost
import com.example.rebornfinance.domain.model.ProjectMaterialConsumption

object ProjectCalculator {

    fun calculateTotalCost(costs: List<ProjectCost>, materialConsumptions: List<ProjectMaterialConsumption>): Long {
        val manualTotal = costs.sumOf { it.amountCents }
        val materialsTotal = materialConsumptions.sumOf { it.assignedCostCents }
        return manualTotal + materialsTotal
    }

    fun calculateEstimatedProfit(predictedSalePriceCents: Long?, totalCostCents: Long): Long? {
        if (predictedSalePriceCents == null) return null
        return predictedSalePriceCents - totalCostCents
    }

    fun calculateSaleProfit(actualSalePriceCents: Long?, totalCostCents: Long): Long? {
        if (actualSalePriceCents == null) return null
        return actualSalePriceCents - totalCostCents
    }
}
