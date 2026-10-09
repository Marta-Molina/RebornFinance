package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.ProjectCost

object ProjectCalculator {

    fun calculateTotalCost(costs: List<ProjectCost>): Long {
        return costs.sumOf { it.amountCents }
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
