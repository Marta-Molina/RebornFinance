package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.MaterialPurchaseLot

data class LotConsumptionResult(
    val lotId: Long,
    val consumedQuantity: Long,
    val costCents: Long
)

data class ConsumptionAllocationResult(
    val totalCostCents: Long,
    val averageUnitCostCentsPerThousand: Long,
    val allocations: List<LotConsumptionResult>,
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)

object MaterialCostCalculator {

    /**
     * Calculates FIFO allocation of material consumption across available purchase lots.
     * quantities are in thousandths (e.g., 5000 = 5.000 ml).
     */
    fun allocateConsumptionFIFO(
        requestedQuantity: Long,
        availableLots: List<MaterialPurchaseLot>
    ): ConsumptionAllocationResult {
        if (requestedQuantity <= 0L) {
            return ConsumptionAllocationResult(0L, 0L, emptyList(), false, "Cantidad solicitada inválida.")
        }

        var remainingToConsume = requestedQuantity
        val allocations = mutableListOf<LotConsumptionResult>()
        var totalCostCents = 0L

        // Sort lots by purchase date / creation ascending (FIFO)
        val sortedLots = availableLots.sortedWith(compareBy({ it.purchaseDate }, { it.createdAt }))

        for (lot in sortedLots) {
            if (remainingToConsume <= 0L) break
            if (lot.remainingQuantity <= 0L) continue

            val consumeFromLot = minOf(remainingToConsume, lot.remainingQuantity)
            // Calculate cost for consumeFromLot based on lot's original paid amount and purchased quantity
            // unitCostPerThousand = paidAmountCents / purchasedQuantity
            val costForThisConsumption = if (lot.purchasedQuantity > 0L) {
                Math.round((consumeFromLot.toDouble() * lot.paidAmountCents.toDouble()) / lot.purchasedQuantity.toDouble())
            } else {
                0L
            }

            totalCostCents += costForThisConsumption
            allocations.add(LotConsumptionResult(lot.id, consumeFromLot, costForThisConsumption))
            remainingToConsume -= consumeFromLot
        }

        if (remainingToConsume > 0L) {
            return ConsumptionAllocationResult(0L, 0L, emptyList(), false, "Stock insuficiente. Faltan ${remainingToConsume / 1000.0} unidades.")
        }

        val averageUnitCost = if (requestedQuantity > 0L) {
            Math.round((totalCostCents.toDouble() * 1000.0) / requestedQuantity.toDouble()).toLong()
        } else {
            0L
        }

        return ConsumptionAllocationResult(
            totalCostCents = totalCostCents,
            averageUnitCostCentsPerThousand = averageUnitCost,
            allocations = allocations,
            isSuccessful = true
        )
    }
}
