package com.example.rebornfinance.domain.calculator

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
                Math.round((consume.toDouble() * lot.paidAmountCents.toDouble()) / lot.purchasedQuantity.toDouble())
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
                Math.round((consume.toDouble() * lot.paidAmountCents.toDouble()) / lot.purchasedQuantityGrams.toDouble())
            } else {
                0L
            }

            totalCost += costForConsume
            allocations.add(HairLotAllocation(lot.id, consume, costForConsume))
            remaining -= consume
        }

        if (remaining > 0L) {
            return HairAllocationResult(0L, 0L, emptyList(), false, "Stock de pelo insuficiente. Faltan ${remaining / 1000.0} gramos.")
        }

        val avgUnitCost = if (requestedQuantityGrams > 0L) {
            Math.round((totalCost.toDouble() * 1000.0) / requestedQuantityGrams.toDouble()).toLong()
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
