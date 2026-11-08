package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.core.validation.QuantityParser
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
     * Uses pure integer arithmetic with half-up rounding in cents.
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
            // Calculate cost for consumeFromLot using integer arithmetic with standard half-up rounding
            val costForThisConsumption = if (lot.purchasedQuantity > 0L) {
                if (consumeFromLot == lot.purchasedQuantity) {
                    lot.paidAmountCents
                } else {
                    val numerator = consumeFromLot * lot.paidAmountCents
                    (numerator + (lot.purchasedQuantity / 2L)) / lot.purchasedQuantity
                }
            } else {
                0L
            }

            totalCostCents += costForThisConsumption
            allocations.add(LotConsumptionResult(lot.id, consumeFromLot, costForThisConsumption))
            remainingToConsume -= consumeFromLot
        }

        if (remainingToConsume > 0L) {
            val missingFormatted = QuantityParser.formatThousandths(remainingToConsume)
            return ConsumptionAllocationResult(0L, 0L, emptyList(), false, "Stock insuficiente. Faltan $missingFormatted unidades.")
        }

        val averageUnitCost = if (requestedQuantity > 0L) {
            val numerator = totalCostCents * 1000L
            (numerator + (requestedQuantity / 2L)) / requestedQuantity
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
