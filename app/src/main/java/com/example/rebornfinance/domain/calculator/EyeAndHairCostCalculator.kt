package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.core.validation.QuantityParser
import com.example.rebornfinance.domain.model.EyePurchaseLot
import com.example.rebornfinance.domain.model.HairPurchaseLot

data class EyeLotAllocation(
    val lotId: Long,
    val consumedQuantity: Long,
    val costCents: Long
)

data class EyeAllocationResult(
    val totalCostCents: Long,
    val allocations: List<EyeLotAllocation>,
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)

data class HairLotAllocation(
    val lotId: Long,
    val consumedQuantityGrams: Long,
    val costCents: Long
)

data class HairAllocationResult(
    val totalCostCents: Long,
    val averageUnitCostCentsPerGram: Long,
    val allocations: List<HairLotAllocation>,
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)

object EyeAndHairCostCalculator {

    fun allocateEyeConsumptionFIFO(
        requestedQuantity: Long,
        availableLots: List<EyePurchaseLot>
    ): EyeAllocationResult {
        if (requestedQuantity <= 0L) {
            return EyeAllocationResult(0L, emptyList(), false, "Cantidad solicitada inválida.")
        }

        var remaining = requestedQuantity
        val allocations = mutableListOf<EyeLotAllocation>()
        var totalCost = 0L

        val sortedLots = availableLots.sortedWith(compareBy({ it.purchaseDate }, { it.createdAt }))

        for (lot in sortedLots) {
            if (remaining <= 0L) break
            if (lot.remainingQuantity <= 0L) continue

            val consume = minOf(remaining, lot.remainingQuantity)
            val costForConsume = if (lot.purchasedQuantity > 0L) {
                if (consume == lot.purchasedQuantity) {
                    lot.paidAmountCents
                } else {
                    val numerator = consume * lot.paidAmountCents
                    (numerator + (lot.purchasedQuantity / 2L)) / lot.purchasedQuantity
                }
            } else {
                0L
            }

            totalCost += costForConsume
            allocations.add(EyeLotAllocation(lot.id, consume, costForConsume))
            remaining -= consume
        }

        if (remaining > 0L) {
            return EyeAllocationResult(0L, emptyList(), false, "Stock de ojos insuficiente. Faltan $remaining unidades.")
        }

        return EyeAllocationResult(
            totalCostCents = totalCost,
            allocations = allocations,
            isSuccessful = true
        )
    }

    fun allocateHairConsumptionFIFO(
        requestedQuantityGrams: Long,
        availableLots: List<HairPurchaseLot>
    ): HairAllocationResult {
        if (requestedQuantityGrams <= 0L) {
            return HairAllocationResult(0L, 0L, emptyList(), false, "Cantidad de pelo solicitada inválida.")
        }

        var remaining = requestedQuantityGrams
        val allocations = mutableListOf<HairLotAllocation>()
        var totalCost = 0L

        val sortedLots = availableLots.sortedWith(compareBy({ it.purchaseDate }, { it.createdAt }))

        for (lot in sortedLots) {
            if (remaining <= 0L) break
            if (lot.remainingQuantityGrams <= 0L) continue

            val consume = minOf(remaining, lot.remainingQuantityGrams)
            val costForConsume = if (lot.purchasedQuantityGrams > 0L) {
                if (consume == lot.purchasedQuantityGrams) {
                    lot.paidAmountCents
                } else {
                    val numerator = consume * lot.paidAmountCents
                    (numerator + (lot.purchasedQuantityGrams / 2L)) / lot.purchasedQuantityGrams
                }
            } else {
                0L
            }

            totalCost += costForConsume
            allocations.add(HairLotAllocation(lot.id, consume, costForConsume))
            remaining -= consume
        }

        if (remaining > 0L) {
            val missingGrams = QuantityParser.formatThousandths(remaining)
            return HairAllocationResult(0L, 0L, emptyList(), false, "Stock de pelo insuficiente. Faltan $missingGrams gramos.")
        }

        val avgUnitCost = if (requestedQuantityGrams > 0L) {
            val numerator = totalCost * 1000L
            (numerator + (requestedQuantityGrams / 2L)) / requestedQuantityGrams
        } else {
            0L
        }

        return HairAllocationResult(
            totalCostCents = totalCost,
            averageUnitCostCentsPerGram = avgUnitCost,
            allocations = allocations,
            isSuccessful = true
        )
    }
}
